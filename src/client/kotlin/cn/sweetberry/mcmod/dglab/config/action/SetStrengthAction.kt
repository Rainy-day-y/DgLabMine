package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import cn.sweetberry.mcmod.dglab.websocket.common.codes.StrengthSettingMode
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class SetStrengthAction(
    var channel: Channel = Channel.CHANNEL_A,

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    var mode: StrengthSettingMode = StrengthSettingMode.SET_TO,

    var strength: Int = 0
) : Action() {

    @ConfigEntry.Gui.Excluded
    override val type = Type.SET_STRENGTH

    override fun buildCommand(ctx: ActionContext): OutgoingCommand {
        return OutgoingCommand.SetStrength(channel, mode, strength.toShort())
    }
}
