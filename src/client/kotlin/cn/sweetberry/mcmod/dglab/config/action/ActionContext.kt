package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.codes.dglab.websocket.common.data.ChannelStrengthLimit
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData

data class ActionContext (
    val actionRuntimeStore: ActionRuntimeStore,
    val strengthStatus: ChannelStrengthLimit,
    val damageData: DamageData,
)