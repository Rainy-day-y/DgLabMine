package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import me.shedaniel.autoconfig.ConfigData

sealed class DamageCondition: ConfigData {

    abstract fun matches(data: DamageData): Boolean

    enum class Type {
        DAMAGE_RANGE,
        IS_SHIELD_BLOCKED,
        IS_CANCELED
    }

    abstract val type: Type
}
