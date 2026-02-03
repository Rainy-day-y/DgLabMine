package cn.sweetberry.mcmod.dglab.websocket.client

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.PulsePlayer
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider.PulseProvider
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData

interface DgLabClient: PulsePlayer {
    val pulseProvider: PulseProvider

    fun start(): Boolean
    fun onHurt(data: DamageData)
    fun stop()
}