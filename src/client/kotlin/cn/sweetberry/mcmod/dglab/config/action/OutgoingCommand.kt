package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import cn.sweetberry.mcmod.dglab.websocket.common.codes.StrengthSettingMode

sealed class OutgoingCommand {

    abstract val actionId: String

    data class SendPulse(
        override val actionId: String,
        val channel: Channel,
        val pulseID: String
    ) : OutgoingCommand()

    data class SetStrength(
        override val actionId: String,
        val channel: Channel,
        val mode: StrengthSettingMode,
        val strength: Short
    ) : OutgoingCommand()

    data class ClearPulse(
        override val actionId: String,
        val channel: Channel
    ) : OutgoingCommand()
}
