package cn.sweetberry.mcmod.dglab.utils.adapter

import cn.sweetberry.mcmod.dglab.config.action.OutgoingCommand
import cn.sweetberry.mcmod.dglab.websocket.client.pulse.provider.PulseProvider
import cn.sweetberry.mcmod.dglab.websocket.common.Payload
import java.util.UUID

fun OutgoingCommand.toPayload(
    clientId: UUID,
    targetId: UUID,
    pulseProvider: PulseProvider
): Payload =
    when (this) {
        is OutgoingCommand.SendPulse ->
            Payload.sendPulse(
                clientId,
                targetId,
                channel,
                pulseProvider.getPulseById(pulseID)!!.data!!
            )

        is OutgoingCommand.SetStrength ->
            Payload.setStrength(
                clientId,
                targetId,
                channel,
                mode,
                strength
            )

        is OutgoingCommand.ClearPulse ->
            Payload.clearPulse(
                clientId,
                targetId,
                channel
            )
    }
