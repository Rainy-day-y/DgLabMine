package cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider

import cn.sweetberry.mcmod.dglab.websocket.client.pulse.reference.PulseReference
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class MutablePulseProviderImpl(
    pulses: List<PulseReference> = emptyList()
) : MutablePulseProvider {

    private val _pulseList = pulses.toMutableList()
    private val _pulseMap = _pulseList.associateBy { it.id }.toMutableMap()

    // 读写锁
    private val lock = ReentrantReadWriteLock()

    override val allPulses: List<PulseReference>
        get() = lock.read { _pulseList.toList() } // 返回不可变副本

    override fun getPulseById(id: String): PulseReference? = lock.read {
        _pulseMap[id]
    }

    override fun registerPulse(pulse: PulseReference): String = lock.write {
        _pulseMap[pulse.id] = pulse
        val index = _pulseList.indexOfFirst { it.id == pulse.id }
        if (index >= 0) {
            _pulseList[index] = pulse
        } else {
            _pulseList.add(pulse)
        }
        pulse.id
    }

    override fun removePulse(id: String) = lock.write {
        _pulseMap.remove(id)
        _pulseList.removeIf { it.id == id }
    }
}