package cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference

import cn.sweetberry.mcmod.dglab.websocket.common.data.PulseData

// 远程只知道 ID
data class RemoteReference(override val id: String) : PulseReference {
    override val data: PulseData? get() = null
}

