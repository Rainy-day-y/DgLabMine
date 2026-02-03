package cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference

// PulseProvider: 提供波形的统一只读接口
interface PulseProvider {
    val allPulses: List<PulseReference>
    fun getPulseById(id: String): PulseReference?
}
