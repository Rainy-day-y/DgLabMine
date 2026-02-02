package cn.sweetberry.mcmod.dglab.websocket.common.codes

enum class Channel(val code: String) {
    CHANNEL_A("1"),
    CHANNEL_B("2");

    companion object {
        fun fromCode(code: String): Channel? =
            entries.firstOrNull { it.code == code }
    }
}