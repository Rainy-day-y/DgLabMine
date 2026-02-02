package cn.sweetberry.mcmod.dglab.websocket.common

import cn.sweetberry.mcmod.dglab.websocket.common.codes_enum.ChannelCode
import cn.sweetberry.mcmod.dglab.websocket.common.codes_enum.ErrorCode
import cn.sweetberry.mcmod.dglab.websocket.common.codes_enum.FeedbackIndexCode
import cn.sweetberry.mcmod.dglab.websocket.common.codes_enum.StrengthSettingMode
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Payload(
    val type: String, val clientId: String, val targetId: String, val message: String
) {

    fun connect(uuid: UUID) = Payload(
        type = "bind", clientId = uuid.toString(), message = "targetId", targetId = ""
    )

    fun bind(clientId: UUID, targetId: UUID, msg: String) = Payload(
        type = "bind", clientId = clientId.toString(), // 终端ID
        targetId = targetId.toString(), // APP ID
        message = msg
    )

    fun bindAttempt(clientId: UUID, targetId: UUID) = bind(clientId, targetId, "DGLAB")

    fun bindResult(clientId: UUID, targetId: UUID, errorCode: ErrorCode) = bind(
        clientId, // 终端ID
        targetId, // APP ID
        errorCode.code
    )

    fun command(clientId: UUID, targetId: UUID, message: String) = Payload(
        type = "msg", clientId = clientId.toString(), targetId = targetId.toString(), message = message
    )

    fun syncStrength(
        clientId: UUID, targetId: UUID, aStrength: Short, aLimit: Short, bStrength: Short, bLimit: Short
    ): Payload {
        val message = "strength-$aStrength+$bStrength+$aLimit+$bLimit"
        return command(clientId, targetId, message)
    }

    fun toStrengthDataList(): List<StrengthData>? {
        val splitStrings = message.split("-")

        val commandType = splitStrings[0]
        val dataStrings = splitStrings[1].split("+")

        if(commandType != "strength" || dataStrings.size != 4) return null

        val aStrength = dataStrings[0].toShortOrNull() ?: 0
        val bStrength = dataStrings[1].toShortOrNull() ?: 0
        val aLimit = dataStrings[2].toShortOrNull() ?: 0
        val bLimit = dataStrings[3].toShortOrNull() ?: 0

        return listOf(
            StrengthData(ChannelCode.CHANNEL_A,aStrength,aLimit),
            StrengthData(ChannelCode.CHANNEL_B,bStrength,bLimit)
        )
    }

    fun setStrength(
        clientId: UUID,
        targetId: UUID,
        targetChannelCode: ChannelCode,
        settingMode: StrengthSettingMode,
        targetStrength: Short
    ): Payload {
        val message = "strength-${targetChannelCode.code}+${settingMode.code}+$targetStrength"
        return command(clientId, targetId, message)
    }

    fun sendPulse(
        clientId: UUID, targetId: UUID, channelCode: ChannelCode, pulseData: PulseData
    ): Payload {
        val message = "pulse-${channelCode.code}:${pulseData.toJson()}"
        return command(clientId, targetId, message)
    }

    fun clearPulse(clientId: UUID, targetId: UUID, channelCode: ChannelCode) = command(
        clientId, targetId, "clear-${channelCode.code}"
    )

    fun feedback(clientId: UUID, targetId: UUID, feedbackIndexCode: FeedbackIndexCode) = command(
        clientId, targetId, "feedback-${feedbackIndexCode.code}"
    )

    fun toFeedbackIndexCode(): FeedbackIndexCode? {
        val commandType = message.split("-")[0]
        val codeString = message.split("-")[1]
        if( commandType != "feedback") return null
        return FeedbackIndexCode.fromCode(codeString)
    }
}