package cn.sweetberry.mcmod.dglab.config.action

import me.shedaniel.autoconfig.annotation.ConfigEntry

class ActionWrapper {

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    var type: Action.Type = Action.Type.SEND_PULSE

    @ConfigEntry.Gui.CollapsibleObject
    var sendPulse: SendPulseAction = SendPulseAction()

    @ConfigEntry.Gui.CollapsibleObject
    var setStrength: SetStrengthAction = SetStrengthAction()

    @ConfigEntry.Gui.CollapsibleObject
    var clearPulse: ClearPulseAction = ClearPulseAction()

    val action: Action
        get() = when (type) {
            Action.Type.SEND_PULSE -> sendPulse
            Action.Type.SET_STRENGTH -> setStrength
            Action.Type.CLEAR_PULSE -> clearPulse
        }
}