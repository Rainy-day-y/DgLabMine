package cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference

import cn.sweetberry.mcmod.dglab.websocket.common.data.PulseData

// 本地有完整数据
data class LocalReference(
    override val id: String,
    override val data: PulseData
) : PulseReference

