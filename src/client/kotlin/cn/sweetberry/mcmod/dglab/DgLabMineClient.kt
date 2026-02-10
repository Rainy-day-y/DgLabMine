package cn.sweetberry.mcmod.dglab

import cn.sweetberry.mcmod.dglab.config.ModConfig
import cn.sweetberry.mcmod.dglab.config.ServerConfig.ServerType
import cn.sweetberry.mcmod.dglab.events.damage.DamageEventBus
import cn.sweetberry.mcmod.dglab.websocket.client.DgLabClientService
import cn.sweetberry.mcmod.dglab.websocket.server.DgLabSocketService
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object DgLabMineClient : ClientModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger("dglab-minecraft")
    fun debugLog(msg: String) {
        LOGGER.info(msg)
    }

    override fun onInitializeClient() {
        DamageEventBus.initialize()
        DgLabMineKeyBindings.register()
        AutoConfig.register(ModConfig::class.java, ::Toml4jConfigSerializer)

        val serverConfig = AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().serverConfig
        when (serverConfig.serverType) {
            ServerType.LOCAL -> {
                DgLabSocketService.start(serverConfig.serverPort)
                DgLabClientService.startClient(DgLabClientService.ClientType.LOCAL)
                    .invokeOnCompletion { DamageEventBus.register(DgLabClientService::onHurt) }
            }

            ServerType.OFFICIAL -> {
                LOGGER.warn("Official server haven't implementation")
            }

            ServerType.REMOTE -> {
                LOGGER.warn("Remote server haven't implementation")
            }
        }

        DamageEventBus.register(DamageLogger::logDamageEvent)
    }
}