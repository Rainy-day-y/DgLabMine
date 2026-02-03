package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class ShieldBlockedCondition(
    var value: Boolean = true
) : DamageCondition() {

    @ConfigEntry.Gui.Excluded
    override val type = Type.IS_SHIELD_BLOCKED

    override fun matches(data: DamageData): Boolean =
        data.shieldBlocked > 0 == value
}