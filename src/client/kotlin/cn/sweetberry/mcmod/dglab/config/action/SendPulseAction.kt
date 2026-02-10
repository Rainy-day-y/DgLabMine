package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class SendPulseAction(
    var channel: Channel = Channel.CHANNEL_A,
    var pulseID: String = "",
) : Action() {

    @ConfigEntry.Gui.Excluded
    override val type = Type.SEND_PULSE

    override fun buildCommand(ctx: ActionContext): OutgoingCommand {
        return OutgoingCommand.SendPulse(channel, pulseID)
    }
}
