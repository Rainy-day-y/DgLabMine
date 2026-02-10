package cn.sweetberry.mcmod.dglab.websocket.client

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

/**
 * DgLab 客户端统一管理服务
 * 
 * 特性：
 * - 单例模式，全局唯一实例
 * - 使用协程在独立线程中启动和管理客户端
 * - 同一时间只允许运行一个客户端实例
 * - 线程安全的客户端切换
 * - 自动转发 onHurt 事件到当前活跃的客户端
 */
object DgLabClientService {
    private val logger: Logger = LoggerFactory.getLogger(DgLabClientService::class.java)
    
    // 协程作用域 - 使用自定义调度器在独立线程池中运行
    private val serviceScope = CoroutineScope(
        Dispatchers.IO + SupervisorJob() + CoroutineName("DgLabClientService")
    )
    
    // 当前活跃的客户端引用 - 使用 AtomicReference 保证线程安全
    private val activeClient = AtomicReference<DgLabClient?>(null)
    
    // 客户端类型枚举
    enum class ClientType {
        LOCAL,
        REMOTE,
        OFFICIAL,
        GAME_HUB
    }
    
    // 当前客户端类型
    private val currentClientType = AtomicReference<ClientType?>(null)
    
    // 客户端启动任务 - 用于取消
    private var startJob: Job? = null
    
    // 服务运行状态
    @Volatile
    private var serviceRunning = false
    
    /**
     * 获取当前活跃的客户端
     */
    fun getActiveClient(): DgLabClient? = activeClient.get()
    
    /**
     * 获取当前客户端类型
     */
    fun getCurrentClientType(): ClientType? = currentClientType.get()
    
    /**
     * 检查是否有客户端正在运行
     */
    fun isClientRunning(): Boolean = activeClient.get() != null
    
    /**
     * 启动指定类型的客户端
     * 
     * @param type 要启动的客户端类型
     * @return 启动是否成功的 Deferred
     */
    fun startClient(type: ClientType): Deferred<Boolean> = serviceScope.async {
        try {
            logger.info("Attempting to start client: $type")

            if (currentClientType.get() == type) return@async true
            
            // 停止当前正在运行的客户端
            stopCurrentClient()
            
            // 获取对应类型的客户端实例
            val client = when (type) {
                ClientType.LOCAL -> DgLabLocalClient()
                ClientType.REMOTE -> DgLabRemoteClient()
                ClientType.OFFICIAL -> DgLabOfficialClient()
                ClientType.GAME_HUB -> DgLabGameHubClient()
            }
            
            // 在 IO 调度器中启动客户端
            val success = withContext(Dispatchers.IO) {
                client.start()
            }
            
            if (success) {
                activeClient.set(client)
                currentClientType.set(type)
                serviceRunning = true
                logger.info("Successfully started client: $type")
            } else {
                logger.warn("Failed to start client: $type")
            }
            
            success
        } catch (e: Exception) {
            logger.error("Error starting client $type: ${e.message}", e)
            false
        }
    }
    
    /**
     * 停止当前运行的客户端
     */
    fun stopCurrentClient() = runBlocking {
        activeClient.get()?.let { client ->
            try {
                logger.info("Stopping current client: ${currentClientType.get()}")
                
                // 取消正在进行的启动任务
                startJob?.cancel()
                startJob = null
                
                // 在 IO 调度器中停止客户端
                withContext(Dispatchers.IO) {
                    client.stop()
                }
                
                logger.info("Client stopped: ${currentClientType.get()}")
            } catch (e: Exception) {
                logger.error("Error stopping client: ${e.message}", e)
            } finally {
                activeClient.set(null)
                currentClientType.set(null)
                serviceRunning = false
            }
        }
    }
    
    /**
     * 切换到指定类型的客户端
     * 如果已经是该类型，则不执行任何操作
     * 
     * @param type 目标客户端类型
     * @return 切换是否成功的 Deferred
     */
    fun switchClient(type: ClientType): Deferred<Boolean> = serviceScope.async {
        val current = currentClientType.get()
        
        if (current == type && isClientRunning()) {
            logger.info("Client $type is already running, no need to switch")
            return@async true
        }
        
        logger.info("Switching client from $current to $type")
        startClient(type).await()
    }

    // 声明队列
    private val hurtQueue = Channel<DamageData>(Channel.UNLIMITED)

    // 启动处理协程
    private val hurtScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        hurtScope.launch {
            for (data in hurtQueue) {
                val client = activeClient.get() // 每次取最新 client
                if (client == null) {
                    logger.trace("No active client to handle onHurt event")
                    continue // 跳过本次事件
                }

                try {
                    client.onHurt(data)
                } catch (e: Exception) {
                    logger.error("Error handling onHurt event in client ${currentClientType.get()}: ${e.message}", e)
                }
            }
        }
    }

    /**
     * 转发 onHurt 事件到当前活跃的客户端
     * 使用协程异步处理，不阻塞调用线程
     * 
     * @param data 伤害数据
     */
    fun onHurt(data: DamageData) {
        hurtScope.launch {
            hurtQueue.send(data) // 入队，按顺序执行
        }
    }

    /**
     * 重启当前客户端
     * 
     * @return 重启是否成功的 Deferred
     */
    fun restartCurrentClient(): Deferred<Boolean> = serviceScope.async {
        val type = currentClientType.get()
        
        if (type == null) {
            logger.warn("No active client to restart")
            return@async false
        }
        
        logger.info("Restarting client: $type")
        stopCurrentClient()
        delay(500) // 给一点时间让资源完全释放
        startClient(type).await()
    }
    
    /**
     * 检查客户端健康状态
     * 
     * @return 客户端是否健康
     */
    fun checkClientHealth(): Boolean {
        val client = activeClient.get() ?: return false
        
        // 对于 LocalClient，可以检查 isRunning 状态
        return when (client) {
            is DgLabLocalClient -> client.isRunning()
            else -> isClientRunning()
        }
    }
    
    /**
     * 获取服务状态信息
     */
    fun getServiceStatus(): ServiceStatus {
        return ServiceStatus(
            isServiceRunning = serviceRunning,
            currentClientType = currentClientType.get(),
            isClientHealthy = checkClientHealth(),
            activeClientInfo = activeClient.get()?.let { client ->
                when (client) {
                    is DgLabLocalClient -> "LocalClient(running=${client.isRunning()})"
                    else -> client.javaClass.simpleName
                }
            }
        )
    }
    
    /**
     * 关闭服务，清理所有资源
     */
    fun shutdown() {
        logger.info("Shutting down DgLabClientService")
        
        try {
            // 停止当前客户端
            stopCurrentClient()
            
            // 取消所有协程
            serviceScope.cancel()
            
            logger.info("DgLabClientService shutdown complete")
        } catch (e: Exception) {
            logger.error("Error during shutdown: ${e.message}", e)
        }
    }
    
    /**
     * 服务状态数据类
     */
    data class ServiceStatus(
        val isServiceRunning: Boolean,
        val currentClientType: ClientType?,
        val isClientHealthy: Boolean,
        val activeClientInfo: String?
    ) {
        override fun toString(): String {
            return """
                |DgLabClientService Status:
                |  Service Running: $isServiceRunning
                |  Current Client: $currentClientType
                |  Client Healthy: $isClientHealthy
                |  Client Info: ${activeClientInfo ?: "N/A"}
            """.trimMargin()
        }
    }
}
