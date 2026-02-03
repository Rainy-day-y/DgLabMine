package cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference

// 可选的可变接口
interface MutablePulseProvider : PulseProvider {
    fun registerPulse(pulse: PulseReference): String
    fun removePulse(id: String): Boolean
}