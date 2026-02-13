package cn.sweetberry.mcmod.dglab.websocket.client

import cn.sweetberry.mcmod.dglab.config.ModConfig
import cn.sweetberry.mcmod.dglab.config.action.ActionContext
import cn.sweetberry.mcmod.dglab.config.action.ActionRuntimeStore
import cn.sweetberry.mcmod.dglab.config.action.OutgoingCommand
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider.PulseProvider
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider.PulseProviderImpl
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.LocalReference
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference
import cn.sweetberry.codes.dglab.websocket.common.Endpoint
import cn.sweetberry.codes.dglab.websocket.common.Payload
import cn.sweetberry.codes.dglab.websocket.common.codes.Channel
import cn.sweetberry.codes.dglab.websocket.common.codes.Error
import cn.sweetberry.codes.dglab.websocket.common.codes.FeedbackIndex
import cn.sweetberry.codes.dglab.websocket.common.data.ChannelStrengthLimit
import cn.sweetberry.codes.dglab.websocket.common.data.PulseData
import cn.sweetberry.codes.dglab.websocket.server.DgLabSocketService
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import kotlinx.serialization.json.Json
import me.shedaniel.autoconfig.AutoConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DgLabLocalClient : DgLabClient {
    // loggers
    val logger: Logger = LoggerFactory.getLogger(DgLabLocalClient::class.java)
    fun debugLog(msg: String) {
        logger.debug(msg)
    }

    // 本地 Endpoint，nullable，启动失败时可能为 null
    private var selfEndpoint: Endpoint.Local? = null
    private var bindingUUID: UUID? = null

    // 是否已启动
    private var running = false

    private val lock = Any()

    private var strengthStatus = ChannelStrengthLimit(
        mapOf(Channel.CHANNEL_A to 0.toShort(), Channel.CHANNEL_B to 0.toShort()),
        mapOf(Channel.CHANNEL_A to 0.toShort(), Channel.CHANNEL_B to 0.toShort()),
    )

    override val pulseProvider: PulseProvider = PulseProviderImpl(
        listOf<PulseReference>(
            LocalReference(
                "A",
                PulseData.fromHexData(
                    listOf(
                        "0A0A0A0A00000000",
                        "0A0A0A0A0A0A0A0A",
                        "0A0A0A0A14141414",
                        "0A0A0A0A1E1E1E1E",
                        "0A0A0A0A28282828",
                        "0A0A0A0A32323232",
                        "0A0A0A0A3C3C3C3C",
                        "0A0A0A0A46464646",
                        "0A0A0A0A50505050",
                        "0A0A0A0A5A5A5A5A",
                        "0A0A0A0A64646464"
                    )
                )
            ),
            LocalReference(
                "B",
                PulseData.fromHexData(
                    listOf(
                        "0A0A0A0A00000000",
                        "0D0D0D0D0F0F0F0F",
                        "101010101E1E1E1E",
                        "1313131332323232",
                        "1616161641414141",
                        "1A1A1A1A50505050",
                        "1D1D1D1D64646464",
                        "202020205A5A5A5A",
                        "2323232350505050",
                        "262626264B4B4B4B",
                        "2A2A2A2A41414141"
                    )
                )
            )
        )
    )

    // 播放队列管理 - 每个通道独立的队列
    private val playQueues = ConcurrentHashMap<Channel, MutableList<PulseReference>>()

    // 当前播放索引 - 每个通道独立
    private val currentPlayIndex = ConcurrentHashMap<Channel, Int>()

    // 定时任务调度器
    private val scheduler = Executors.newScheduledThreadPool(2)

    // 每个通道的播放任务
    private val playTasks = ConcurrentHashMap<Channel, ScheduledFuture<*>>()

    companion object {
        // 基础播放间隔（毫秒）
        private const val BASE_INTERVAL_MS = 100L
    }

    init {
        // 初始化每个通道的队列
        Channel.entries.forEach { channel ->
            playQueues[channel] = mutableListOf()
            currentPlayIndex[channel] = 0
        }
    }

    val actionRuntimeStore = ActionRuntimeStore()

    /**
     * 启动本地客户端
     *
     * @return 是否成功启动
     */
    override fun start(): Boolean {
        synchronized(lock) {
            if (running) return true // 幂等
            val endpoint = DgLabSocketService.openLocal { msg ->
                // 收到来自服务端的消息回调
                onMessage(msg)
            }

            if (endpoint == null) {
                LoggerFactory.getLogger("DgLabLocalClient")
                    .warn("Cannot start local client: server not running")
                return false
            }

            selfEndpoint = endpoint
            selfEndpoint?.let { logger.info("starting local client,UUID: ${it.id}") }
            running = true
            return true
        }
    }

    override fun getLink(): String? {
        val base = "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#"
        val serverConfig = AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().serverConfig
        val address = "ws://${serverConfig.serverAddress}:${serverConfig.serverPort}"
        val id = selfEndpoint?.id?.toString() ?: return null
        return "$base$address/$id"
    }

    /**
     * 停止客户端
     */
    override fun stop() {
        synchronized(lock) {
            // 停止所有播放任务
            Channel.entries.forEach { channel ->
                stopPlayTask(channel)
            }

            selfEndpoint?.let {
                DgLabSocketService.closeLocal(it)
            }
            selfEndpoint = null
            bindingUUID = null
            running = false
        }
    }

    /**
     * 是否正在运行
     */
    fun isRunning(): Boolean = running

    /**
     * 处理游戏逻辑触发的"受伤事件"
     */
    override fun onHurt(data: DamageData) {
        if (!running) return
        if (bindingUUID == null) return
        if (!AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().dgLabConfig.enabled) return

        val commands = AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().dgLabConfig.rules
            .flatMap { it.collectCommands(ActionContext(actionRuntimeStore, strengthStatus, data)) }

        debugLog(commands.toString())
        commands.forEach {
            when (it) {
                is OutgoingCommand.SendPulse -> {
                    play(it.pulseID, it.channel)
                }

                is OutgoingCommand.SetStrength -> {
                    send(
                        Payload.setStrength(
                            selfEndpoint!!.id,
                            bindingUUID!!,
                            it.channel,
                            it.mode,
                            it.strength
                        )
                    )
                }

                is OutgoingCommand.ClearPulse -> {
                    send(
                        Payload.clearPulse(
                            selfEndpoint!!.id,
                            bindingUUID!!,
                            it.channel
                        )
                    )
                }
            }
        }
    }

    /**
     * 将消息发送给服务端
     */
    private fun send(message: Payload) {
        selfEndpoint?.emit(message)
    }

    /**
     * 收到服务端消息
     */
    private fun onMessage(message: String) {
        // 这里可以处理服务端下发的指令或反馈
        try {
            val payload = Json.decodeFromString<Payload>(message)
            when (payload.type) {
                "bind" -> {
                    if (payload.message == Error.SUCCESS.code) {
                        bindingUUID = UUID.fromString(payload.targetId)
                    } else {
                        logger.warn("Binding Defeat, message: $payload")
                    }
                }

                "msg" -> {
                    payload.toChannelStrengthLimit()?.let { strengthStatus = it }
                    payload.toFeedbackIndexCode()?.let(::handleFeedback)
                }

                "break" -> {
                    logger.info("Connect closed, restarting, message: $payload")
                    stop()
                    start()
                }

                "error" -> logger.error("receive error message: $payload")

                else -> throw IllegalArgumentException("Unknown message type: $payload")
            }
        } catch (e: Exception) {
            logger.error("an Error occurred when client parsing message: ${e.message}")
        }
    }

    private fun handleFeedback(index: FeedbackIndex) {
        debugLog("receive feedback: index: $index")
    }

    // 实现播放功能

    /**
     * 获取当前正在播放的波形引用
     */
    override val playing: PulseReference
        get() {
            // 默认返回 Channel A 的当前播放项
            val queue = playQueues[Channel.CHANNEL_A] ?: return pulseProvider.allPulses.first()
            val index = currentPlayIndex[Channel.CHANNEL_A] ?: 0
            return if (queue.isNotEmpty()) queue[index % queue.size] else pulseProvider.allPulses.first()
        }

    /**
     * 获取播放队列（为了兼容性，返回 Channel A 的队列）
     */
    override val playQueue: MutableList<LocalReference>
        get() = playQueues[Channel.CHANNEL_A]?.filterIsInstance<LocalReference>()?.toMutableList()
            ?: mutableListOf()

    /**
     * 无参 play() - 循环播放当前队列中的所有波形
     * 如果队列为空，则播放 pulseProvider 中的所有波形
     */
    fun play() {
        Channel.entries.forEach { channel ->
            val queue = playQueues[channel]!!

            // 如果队列为空，使用 pulseProvider 中的所有波形
            if (queue.isEmpty()) {
                queue.addAll(pulseProvider.allPulses)
            }

            // 启动循环播放
            startPlayTask(channel)
        }
    }

    /**
     * 播放指定波形一次 - 播放单个波形后停止
     *
     * @param pulseId 波形 ID
     * @param channel 通道
     */
    override fun play(pulseId: String, channel: Channel) {
        if (!running || bindingUUID == null) {
            logger.warn("Cannot play: client not running or not bound")
            return
        }

        val pulse = pulseProvider.getPulseById(pulseId)
        if (pulse == null) {
            logger.warn("Pulse which try play not found: $pulseId")
            return
        }

        // 停止当前播放任务
        stopPlayTask(channel)

        val latch = java.util.concurrent.CountDownLatch(1)

        pulse.data?.let { pulseData ->
            val payload = Payload.sendPulse(
                selfEndpoint!!.id,
                bindingUUID!!,
                channel,
                pulseData
            )
            send(payload)

            val waveformLength = pulseData.size()
            val delayMs = (waveformLength * 100L) + 100L

            // 调度一个延迟任务，播放完成后释放 latch
            val task = scheduler.schedule({
                latch.countDown()
            }, delayMs, TimeUnit.MILLISECONDS)

            playTasks[channel] = task

            // 阻塞当前协程线程等待播放完成
            latch.await()
        } ?: run {
            logger.warn("Pulse ${pulse.id} has no data (RemoteReference?)")
        }
    }

    /**
     * 将波形添加到队列末尾
     *
     * @param pulseId 波形 ID
     * @param channel 通道
     */
    override fun enqueue(pulseId: String, channel: Channel) {
        val pulse = pulseProvider.getPulseById(pulseId)
        if (pulse == null) {
            logger.warn("Pulse not found: $pulseId")
            return
        }

        val queue = playQueues[channel]!!
        queue.add(pulse)

        // 如果队列之前为空，启动播放
        if (queue.size == 1) {
            startPlayTask(channel)
        }

        debugLog("Enqueued pulse $pulseId to channel ${channel.numberCode}, queue size: ${queue.size}")
    }

    /**
     * 清空指定通道的播放队列
     *
     * @param channel 通道
     */
    override fun clearQueue(channel: Channel) {
        stopPlayTask(channel)
        playQueues[channel]?.clear()
        currentPlayIndex[channel] = 0
        debugLog("Cleared queue for channel ${channel.numberCode}")
    }

    /**
     * 启动指定通道的播放任务
     */
    private fun startPlayTask(channel: Channel) {
        // 先停止现有任务
        stopPlayTask(channel)

        // 立即播放第一个波形，之后根据波形长度动态调度
        scheduler.execute {
            try {
                playNextWithSchedule(channel)
            } catch (e: Exception) {
                logger.error("Error in play task for channel ${channel.numberCode}: ${e.message}", e)
            }
        }

        debugLog("Started play task for channel ${channel.numberCode}")
    }

    /**
     * 停止指定通道的播放任务
     */
    private fun stopPlayTask(channel: Channel) {
        playTasks[channel]?.let { task ->
            task.cancel(false)
            playTasks.remove(channel)
            debugLog("Stopped play task for channel ${channel.numberCode}")
        }
    }

    /**
     * 播放下一个波形并安排下次播放
     */
    private fun playNextWithSchedule(channel: Channel) {
        if (!running || bindingUUID == null) return

        val queue = playQueues[channel]!!
        if (queue.isEmpty()) {
            stopPlayTask(channel)
            return
        }

        val index = currentPlayIndex[channel]!!
        val pulse = queue[index % queue.size]

        // 发送波形数据
        var delayMs = BASE_INTERVAL_MS // 默认延迟
        pulse.data?.let { pulseData ->
            val payload = Payload.sendPulse(
                selfEndpoint!!.id,
                bindingUUID!!,
                channel,
                pulseData
            )
            send(payload)

            // 计算延迟：波形数据长度 * 100ms + 100ms
            val waveformLength = pulseData.size()
            delayMs = (waveformLength * 100L) + 100L

            debugLog("Playing pulse ${pulse.id} on channel ${channel.numberCode} (index: $index, length: $waveformLength, next delay: ${delayMs}ms)")
        }

        // 更新索引，循环播放
        currentPlayIndex[channel] = (index + 1) % queue.size

        // 根据波形长度安排下次播放
        val nextTask = scheduler.schedule({
            try {
                playNextWithSchedule(channel)
            } catch (e: Exception) {
                logger.error("Error in play task for channel ${channel.numberCode}: ${e.message}", e)
            }
        }, delayMs, TimeUnit.MILLISECONDS)

        playTasks[channel] = nextTask
    }

    /**
     * 获取指定通道的当前播放波形
     */
    fun getCurrentPlaying(channel: Channel): PulseReference? {
        val queue = playQueues[channel] ?: return null
        if (queue.isEmpty()) return null
        val index = currentPlayIndex[channel] ?: 0
        return queue[index % queue.size]
    }

    /**
     * 获取指定通道的队列大小
     */
    fun getQueueSize(channel: Channel): Int {
        return playQueues[channel]?.size ?: 0
    }

    /**
     * 判断指定通道是否正在播放
     */
    fun isPlaying(channel: Channel): Boolean {
        return playTasks.containsKey(channel) && playTasks[channel]?.isDone == false
    }
}
