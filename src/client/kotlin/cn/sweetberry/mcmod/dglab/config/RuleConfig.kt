package cn.sweetberry.mcmod.dglab.config

import cn.sweetberry.mcmod.dglab.config.action.ActionContext
import cn.sweetberry.mcmod.dglab.config.action.ActionWrapper
import cn.sweetberry.mcmod.dglab.config.action.OutgoingCommand
import cn.sweetberry.mcmod.dglab.config.condition.ConditionGroup
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "dg_Lab.rules")
internal class RuleConfig: ConfigData {
    var enabled: Boolean = true

    var name: String = "Default Rule"

    @ConfigEntry.Gui.TransitiveObject
    var conditions: ConditionGroup = ConditionGroup()

    var actions = mutableListOf<ActionWrapper>()

    fun collectCommands(ctx: ActionContext): List<OutgoingCommand> {
        if (!conditions.matches(ctx.damageData)) return emptyList()
        return actions.mapNotNull { it.action.buildCommand(ctx) }
    }
}