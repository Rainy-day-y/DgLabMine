package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import cn.sweetberry.mcmod.dglab.websocket.common.codes.StrengthSettingMode

data class ActionEffect(
    val channel: Channel,
    val delta: Short,                // 上次执行对该通道的增量
    val mode: StrengthSettingMode,   // 当时 mode
    val strengthJexl: String         // 当时 JEXL
)
