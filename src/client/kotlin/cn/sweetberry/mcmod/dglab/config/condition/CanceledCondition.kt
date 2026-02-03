package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class CanceledCondition(
    var value: Boolean = true
) : DamageCondition() {

    @ConfigEntry.Gui.Excluded
    override val type = Type.IS_CANCELED

    override fun matches(data: DamageData): Boolean =
        data.isCancelled == value
}