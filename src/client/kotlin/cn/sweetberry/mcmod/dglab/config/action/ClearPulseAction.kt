package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel

data class ClearPulseAction(
    var channel: Channel = Channel.CHANNEL_A
) : Action() {

    override val type = Type.CLEAR_PULSE

    override fun buildCommand(ctx: ActionContext): OutgoingCommand {
        return OutgoingCommand.ClearPulse(channel)
    }
}