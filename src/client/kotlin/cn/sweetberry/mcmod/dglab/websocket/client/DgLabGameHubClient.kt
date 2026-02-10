package cn.sweetberry.mcmod.dglab.websocket.client

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider.PulseProvider
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference
import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData

class DgLabGameHubClient: DgLabClient {
    override val pulseProvider: PulseProvider
        get() = TODO("Not yet implemented")

    override fun start(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLink(): String? {
        TODO("Not yet implemented")
    }

    override fun onHurt(data: DamageData) {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override val playing: PulseReference
        get() = TODO("Not yet implemented")
    override val playQueue: List<PulseReference>
        get() = TODO("Not yet implemented")

    override fun play(pulseId: String, channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun enqueue(pulseId: String, channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun clearQueue(channel: Channel) {
        TODO("Not yet implemented")
    }
}