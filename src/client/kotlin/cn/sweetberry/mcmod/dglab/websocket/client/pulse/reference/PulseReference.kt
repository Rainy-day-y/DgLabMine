package cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference

import cn.sweetberry.mcmod.dglab.websocket.common.data.PulseData

// 不管是完整波形还是仅 ID，这个接口都能表示它
interface PulseReference {
    val id: String
    val data: PulseData?  // 本地有数据时提供，远程可能为 null
}