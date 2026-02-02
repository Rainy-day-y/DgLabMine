package cn.sweetberry.mcmod.dglab.websocket.common.data

import kotlinx.serialization.json.Json

class PulseData {
    private val data = mutableListOf<ULong>()

    fun addWaveUnit(wave: ULong) {
        data.add(wave)
    }

    fun addWaveUnit(wave: String) {
        if (wave.length != 16) throw RuntimeException("Wave string must have 8 bytes")
        data += wave.toULong(16)
    }

    fun toJson(): String {
        return Json.encodeToString(data.toList())
    }
}