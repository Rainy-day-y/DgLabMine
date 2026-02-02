package cn.sweetberry.mcmod.dglab.websocket.common.codes_enum

enum class ChannelCode(val code: String) {
    CHANNEL_A("1"),
    CHANNEL_B("2");

    companion object {
        fun fromCode(code: String): ChannelCode? =
            entries.firstOrNull { it.code == code }
    }
}