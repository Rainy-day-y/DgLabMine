package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import me.shedaniel.autoconfig.ConfigData

class ConditionGroup: ConfigData {

    var conditions: MutableList<ConditionWrapper> = mutableListOf()

    fun matches(data: DamageData): Boolean =
        conditions.all { it.condition.matches(data) }
}