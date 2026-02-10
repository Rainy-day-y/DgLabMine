package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class ClearPulseAction(
    var channel: Channel = Channel.CHANNEL_A
) : Action() {

    @ConfigEntry.Gui.Excluded
    override val type = Type.CLEAR_PULSE

    override fun buildCommand(ctx: ActionContext): OutgoingCommand {
        return OutgoingCommand.ClearPulse(channel)
    }
}