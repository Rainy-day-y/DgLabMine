package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.dglab.config.condition.DamageCondition.Type.*
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

    @ConfigEntry.Gui.CollapsibleObject
    var jexl: JexlCondition = JexlCondition()

    val condition: DamageCondition
        get() = when (type) {
            DAMAGE_RANGE -> damageRange
            IS_SHIELD_BLOCKED -> shieldBlocked
            IS_CANCELED -> isCanceled
            JEXL -> jexl
        }
}