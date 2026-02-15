package cn.sweetberry.mcmod.dglab.manager

import cn.sweetberry.mcmod.dglab.config.ModConfig
import cn.sweetberry.mcmod.dglab.config.ServerConfig
import cn.sweetberry.mcmod.dglab.websocket.client.DgLabClientService
import cn.sweetberry.codes.dglab.websocket.server.DgLabSocketService
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

/**
 * DgLab 连接管理器
 * 
 * 统一管理配置读取、保存、预览生成和服务重启
 * 注意：LOCAL 模式下，改端口必须同时重启服务端（SocketServer）和客户端（ClientService）
 */
object ConnectionManager {
    private val LOGGER = LoggerFactory.getLogger(ConnectionManager::class.java)

    /**
     * 从运行中的服务获取当前连接链接
     * 
     * @return 当前链接，服务未运行返回 null
     */
    fun getCurrentLink(): String? {
        return DgLabClientService.getClientLink()
    }

    /**
     * 基于配置生成预览链接（无需服务运行）
     * 
     * @param type 服务器类型
     * @param address 服务器地址
     * @param port 服务器端口
     * @return 预览链接，LOCAL 模式返回模板（含 {uuid} 占位符）
     */
    fun generatePreviewLink(type: ServerConfig.ServerType, address: String, port: Int): String? {
        return when (type) {
            ServerConfig.ServerType.LOCAL -> {
                val base = "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#"
                val wsUrl = "ws://$address:$port"
                // LOCAL 模式预览时显示模板，实际运行时替换 {uuid}
                "$base$wsUrl/{uuid}"
            }
            ServerConfig.ServerType.REMOTE -> {
                val base = "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#"
                val wsUrl = "ws://$address:$port"
                "$base$wsUrl/{uuid}"
            }
            ServerConfig.ServerType.OFFICIAL -> {
                // 官方模式链接格式待定
                "wss://api.dglab.com/{uuid}"
            }
        }
    }

    /**
     * 基于当前配置生成预览链接
     */
    fun generatePreviewLinkFromCurrentConfig(): String? {
        val config = AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().serverConfig
        return generatePreviewLink(config.serverType, config.serverAddress, config.serverPort)
    }

    /**
     * 检查模式是否可用（已实现）
     * 
     * @param type 服务器类型
     * @return 是否可用
     */
    fun isModeAvailable(type: ServerConfig.ServerType): Boolean {
        return when (type) {
            ServerConfig.ServerType.LOCAL -> true
            ServerConfig.ServerType.REMOTE -> false
            ServerConfig.ServerType.OFFICIAL -> false
        }
    }

    /**
     * 保存配置到文件
     * 
     * @param type 服务器类型
     * @param address 服务器地址
     * @param port 服务器端口
     */
    fun saveConfig(type: ServerConfig.ServerType, address: String, port: Int) {
        AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().serverConfig.apply {
            serverType = type
            serverAddress = address
            serverPort = port
        }
        AutoConfig.getConfigHolder(ModConfig::class.java).save()
    }

    /**
     * 应用配置并重启服务
     * 
     * LOCAL 模式下，会先停止 SocketServer，再用新端口重启
     * 
     * @param type 服务器类型
     * @param address 服务器地址
     * @param port 服务器端口
     * @param onComplete 完成回调，参数为是否成功
     */
    fun applyConfigAndRestart(
        type: ServerConfig.ServerType,
        address: String,
        port: Int,
        onComplete: (Boolean) -> Unit
    ) {
        // 1. 保存配置
        saveConfig(type, address, port)

        // 2. 停止当前客户端
        DgLabClientService.stopCurrentClient()

        // 3. LOCAL 模式下，重启服务端（SocketServer）
        if (type == ServerConfig.ServerType.LOCAL) {
            try {
                // 尝试停止现有服务端（如果支持）
                DgLabSocketService.stop()
                // 使用新端口启动服务端
                DgLabSocketService.start(port)
            } catch (e: Exception) {
                // 如果 stop() 方法不存在或失败，尝试直接 start（某些实现会内部处理重启）
                LOGGER.warn("Failed to stop SocketServer, attempting to start directly: {}", e.message)
                try {
                    DgLabSocketService.start(port)
                } catch (e2: Exception) {
                    // 服务端启动失败
                    LOGGER.error("Failed to start SocketServer on port {}: {}", port, e2.message)
                    MinecraftClient.getInstance().execute {
                        onComplete(false)
                    }
                    return
                }
            }
        }

        // 4. 启动新客户端
        DgLabClientService.startClient(convertToClientType(type)).invokeOnCompletion { throwable ->
            val success = throwable == null && DgLabClientService.isClientRunning()
            
            // 在主线程回调
            MinecraftClient.getInstance().execute {
                onComplete(success)
            }
        }
    }

    /**
     * 获取当前服务运行状态
     */
    fun isServiceRunning(): Boolean {
        return DgLabClientService.isClientRunning()
    }

    /**
     * 转换配置类型到客户端类型
     */
    private fun convertToClientType(type: ServerConfig.ServerType): DgLabClientService.ClientType {
        return when (type) {
            ServerConfig.ServerType.LOCAL -> DgLabClientService.ClientType.LOCAL
            ServerConfig.ServerType.REMOTE -> DgLabClientService.ClientType.REMOTE
            ServerConfig.ServerType.OFFICIAL -> DgLabClientService.ClientType.OFFICIAL
        }
    }

    /**
     * 发送提示消息到玩家聊天栏
     */
    fun sendMessage(text: Text) {
        MinecraftClient.getInstance().player?.sendMessage(text, false)
    }
}
