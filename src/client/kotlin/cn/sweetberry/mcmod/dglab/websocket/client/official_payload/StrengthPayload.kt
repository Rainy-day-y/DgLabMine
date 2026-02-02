package cn.sweetberry.mcmod.dglab.websocket.client.official_payload

import kotlinx.serialization.Serializable

@Serializable
data class StrengthPayload(
    val clientId: String,
    val targetId: String,
    val message: String, // 应该是和GitHub一样
    val type: String = "4" // 没研究明白，DG-Lab的API版本管理有待加强啊，怎么三个版本文档还乱七八糟
) {
    // TODO: 官方 wss://ws.dungeon-lab.cn/ 的强度Payload
}