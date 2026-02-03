package cn.sweetberry.mcmod.dglab.config

import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData


@Config(name = "dglab-minecraft")
internal class ModConfig : GlobalData() {
    @ConfigEntry.Category("server_config")
    @ConfigEntry.Gui.TransitiveObject
    var serverConfig: ServerConfig = ServerConfig()

    @ConfigEntry.Category("dg_lab_config")
    @ConfigEntry.Gui.TransitiveObject
    var dgLabConfig: DgLabConfig = DgLabConfig()
}