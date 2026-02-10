package cn.sweetberry.mcmod.dglab.config.condition

import cn.sweetberry.mcmod.dglab.jexl.JexlEngine
import cn.sweetberry.mcmod.dglab.jexl.toJexlContext
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import me.shedaniel.autoconfig.annotation.ConfigEntry

data class JexlCondition(
    var expression: String = ""
) : DamageCondition() {
    @ConfigEntry.Gui.Excluded
    override val type = Type.JEXL

    override fun matches(data: DamageData): Boolean {
        val context = data.toJexlContext()
        return JexlEngine.getExpression(expression).evaluate(context) as Boolean
    }
}