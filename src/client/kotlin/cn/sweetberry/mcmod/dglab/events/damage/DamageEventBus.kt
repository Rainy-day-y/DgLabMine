package cn.sweetberry.mcmod.dglab.events.damage

import cn.sweetberry.mcmod.dglab.DgLabMineClient
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageS2CPayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object DamageEventBus {
    private var isInitialized = false
    private val listeners = mutableListOf<(DamageData) -> Unit>()

    fun initialize() {
        if (isInitialized) return
        ClientPlayConnectionEvents.INIT.register { _, _ ->
            ClientPlayNetworking.registerReceiver(
                DamageS2CPayload.ID,
                { payload, _ -> post(payload.data) }
            )
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            ClientPlayNetworking.unregisterReceiver(DamageS2CPayload.SUMMON_LIGHTNING_PAYLOAD_ID)
        }
        isInitialized = true
    }

    fun register(listener: (DamageData) -> Unit) {
        listeners += listener
    }

    fun post(data: DamageData) {
        if (!isInitialized) {
            DgLabMineClient.LOGGER.warn("post called before DamageEventBus was initialized.")
            return
        }
        listeners.forEach { it(data) }
    }
}