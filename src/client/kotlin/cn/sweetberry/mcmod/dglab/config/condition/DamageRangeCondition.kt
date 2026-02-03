package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class DamageRangeCondition(
    var min: Float = 0f,
    var max: Float = Float.MAX_VALUE
) : DamageCondition() {

    @ConfigEntry.Gui.Excluded
    override val type = Type.DAMAGE_RANGE

    override fun matches(data: DamageData): Boolean =
        data.damageAmount in min..max
}