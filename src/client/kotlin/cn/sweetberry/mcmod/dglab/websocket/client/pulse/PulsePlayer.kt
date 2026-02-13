package cn.sweetberry.mcmod.dglab.websocket.client.pulse

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference
import cn.sweetberry.codes.dglab.websocket.common.codes.Channel

interface PulsePlayer {
    val playing: PulseReference?
    val playQueue: List<PulseReference>

    fun play(pulseId: String, channel: Channel)
    fun enqueue(pulseId: String, channel: Channel)
    fun clearQueue(channel: Channel)
}