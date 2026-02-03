package cn.sweetberry.mcmod.dglab.config.condition

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.ConfigEntry

class ConditionWrapper: ConfigData {

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    var type: DamageCondition.Type = DamageCondition.Type.DAMAGE_RANGE

    @ConfigEntry.Gui.CollapsibleObject
    var damageRange: DamageRangeCondition = DamageRangeCondition()

    @ConfigEntry.Gui.CollapsibleObject
    var isCanceled: CanceledCondition = CanceledCondition()

    @ConfigEntry.Gui.CollapsibleObject
    var shieldBlocked: ShieldBlockedCondition = ShieldBlockedCondition()

    val condition: DamageCondition
        get() = when (type) {
            DamageCondition.Type.DAMAGE_RANGE -> damageRange
            DamageCondition.Type.IS_SHIELD_BLOCKED -> shieldBlocked
            DamageCondition.Type.IS_CANCELED -> isCanceled
        }
}