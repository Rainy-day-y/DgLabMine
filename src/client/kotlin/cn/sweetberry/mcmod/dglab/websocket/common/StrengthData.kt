package cn.sweetberry.mcmod.dglab.websocket.common

import cn.sweetberry.mcmod.dglab.websocket.common.codes_enum.ChannelCode

data class StrengthData (
    val channelCode: ChannelCode,
    val strength: Short,
    val limit: Short,
)