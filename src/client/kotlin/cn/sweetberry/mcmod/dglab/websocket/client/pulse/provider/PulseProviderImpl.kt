package cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference

class PulseProviderImpl(
    pulses: List<PulseReference>
) : PulseProvider {

    // 不可变列表
    override val allPulses: List<PulseReference> = pulses.toList()

    // 内部构建 Map，加速 getPulseById
    private val pulseMap: Map<String, PulseReference> = allPulses.associateBy { it.id }

    override fun getPulseById(id: String): PulseReference? {
        return pulseMap[id] // O(1) 查找
    }

    constructor() : this(emptyList())
}