package cn.sweetberry.mcmod.dglab.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

@Config(name = "dg_lab_config")
internal class DgLabConfig : ConfigData{
    var enabled = true
    var rules = mutableListOf<RuleConfig>()
}