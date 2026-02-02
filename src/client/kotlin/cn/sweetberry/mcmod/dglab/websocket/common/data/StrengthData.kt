package cn.sweetberry.mcmod.dglab.websocket.common.data

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel

data class StrengthData (
    val channel: Channel,
    val strength: Short,
    val limit: Short,
)