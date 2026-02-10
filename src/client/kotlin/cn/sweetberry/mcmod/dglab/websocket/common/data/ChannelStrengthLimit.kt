package cn.sweetberry.mcmod.dglab.websocket.common.data

import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel

data class ChannelStrengthLimit(
    val strength: Map<Channel, Short>,
    val limit: Map<Channel, Short>
)