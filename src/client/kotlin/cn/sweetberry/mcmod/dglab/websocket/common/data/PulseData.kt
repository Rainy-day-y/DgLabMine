package cn.sweetberry.mcmod.dglab.websocket.common.data

import kotlinx.serialization.json.Json

class PulseData {
    private val data = mutableListOf<String>()

    fun addWaveUnit(hex: String) {
        require(hex.length == 16) {
            "Wave string must be 16 hex characters (8 bytes)"
        }
        require(hex.all { it in "0123456789abcdefABCDEF" }) {
            "Wave string must be a valid hex string"
        }
        data.add(hex.lowercase())
    }

    fun addWaveUnits(hexes: List<String>) {
        for (hex in hexes) {
            addWaveUnit(hex)
        }
    }

    fun toJson(): String =
        Json.encodeToString(data)
}