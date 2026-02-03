package cn.sweetberry.mcmod.dglab

import cn.sweetberry.mcmod.dglab.events.damage.DamageEventBus
import cn.sweetberry.mcmod.dglab.utils.DamageLogger
import cn.sweetberry.mcmod.dglab.websocket.client.DgLabSocketClient
import cn.sweetberry.mcmod.dglab.websocket.server.DgLabSocketService
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DgLabMineClient : ClientModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger("dglab-minecraft")
    fun debugLog(msg: String) {
        LOGGER.debug(msg)
    }

    override fun onInitializeClient() {
        DamageEventBus.initialize()
        DamageEventBus.register(DamageLogger::logDamageEvent)

        DgLabSocketService.start()
        DgLabSocketClient.start()

        DamageEventBus.register(DgLabSocketClient::onHurt)
    }
}