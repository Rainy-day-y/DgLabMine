package cn.sweetberry.mcmod.dglab.websocket.common.codes_enum

enum class StrengthSettingMode(val code: String) {
    DECREASE("0"),   // 通道强度减少
    INCREASE("1"),   // 通道强度增加
    SET_TO("2");     // 通道强度变化为指定数值

    companion object {
        fun fromCode(code: String): StrengthSettingMode? =
            entries.firstOrNull { it.code == code }
    }
}
