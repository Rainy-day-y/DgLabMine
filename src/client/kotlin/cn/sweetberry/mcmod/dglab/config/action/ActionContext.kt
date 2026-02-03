package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.websocket.common.data.StrengthData
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData

data class ActionContext (
    val strengthStatus: List<StrengthData>,
    val damageData: DamageData,
)