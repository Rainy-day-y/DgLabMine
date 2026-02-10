package cn.sweetberry.mcmod.dglab.config.action

import cn.sweetberry.mcmod.dglab.jexl.JexlEngine
import cn.sweetberry.mcmod.dglab.jexl.toJexlContext
import cn.sweetberry.mcmod.dglab.websocket.common.codes.Channel
import cn.sweetberry.mcmod.dglab.websocket.common.codes.StrengthSettingMode
import me.shedaniel.autoconfig.annotation.ConfigEntry
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.math.roundToInt

data class SetStrengthAction(
    override var id: String = UUID.randomUUID().toString(),

    var channel: Channel = Channel.CHANNEL_A,

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    var mode: StrengthSettingMode = StrengthSettingMode.SET_TO,

    var strengthJexl: String = "0",
) : Action() {
    @ConfigEntry.Gui.Excluded
    override val type = Type.SET_STRENGTH

    override fun buildCommand(ctx: ActionContext): OutgoingCommand {
        val logger = LoggerFactory.getLogger(javaClass)
        logger.info("---SetStrengthAction start: JEXL: $strengthJexl, id: $id---")
        val jexlContext = ctx.damageData.toJexlContext()
        val limit = when (mode) {
            StrengthSettingMode.SET_TO -> ctx.strengthStatus.limit[channel] ?: 0
            StrengthSettingMode.DECREASE -> ctx.strengthStatus.strength[channel] ?: 0
            StrengthSettingMode.INCREASE -> {
                val limit = ctx.strengthStatus.limit[channel] ?: 0
                val strength = ctx.strengthStatus.strength[channel] ?: 0
                limit - strength
            }
        }
        val runtime = ctx.actionRuntimeStore

        // 上次执行的效果
        val lastEffect = runtime.getLastEffect(id)
        logger.info("lastEffect: $lastEffect")

        // 暴露给 JEXL
        val lastDelta = lastEffect?.firstOrNull { it.channel == channel }?.delta ?: 0
        jexlContext["lastDelta"] = lastDelta

        val strength = try {
            val jexlResult = JexlEngine.getExpression(strengthJexl).evaluate(jexlContext)
            logger.info("jexl result: {}", jexlResult)
            (jexlResult as? Number)
                ?.toDouble()?.roundToInt()
                ?.coerceIn(0, limit.toInt())
                ?.toShort()
                ?: 0
        } catch (e: Exception) {
            logger.warn("A Bad Strength JEXL: ${e.message}")
            0
        }

        // 记录本次对设备的影响
        val delta: Short = when (mode) {
            StrengthSettingMode.SET_TO -> (strength - (ctx.strengthStatus.strength[channel] ?: 0)).toShort()
            StrengthSettingMode.DECREASE -> (-strength).toShort()
            StrengthSettingMode.INCREASE -> strength
        }
        val effect = ActionEffect(
            channel = channel,
            delta,
            mode = mode,
            strengthJexl = strengthJexl
        )
        runtime.updateLastEffect(id, listOf(effect))
        logger.info("this effect: {}", effect)
        logger.info("----END----")
        return OutgoingCommand.SetStrength(id, channel, mode, strength)
    }
}
