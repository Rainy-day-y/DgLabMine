package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.codes.dglab.websocket.common.codes.Channel
import cn.sweetberry.codes.dglab.websocket.common.codes.StrengthSettingMode

sealed class OutgoingCommand {
    data class SendPulse(
        val channel: Channel,
        val pulseID: String
    ) : OutgoingCommand()

    data class SetStrength(
        val channel: Channel,
        val mode: StrengthSettingMode,
        val strength: Short
    ) : OutgoingCommand()

    data class ClearPulse(
        val channel: Channel
    ) : OutgoingCommand()
}
