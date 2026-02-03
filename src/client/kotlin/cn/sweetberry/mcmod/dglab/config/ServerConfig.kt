package cn.sweetberry.mcmod.dglab.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

@Config(name = "server_config")
internal class ServerConfig : ConfigData{
    var serverType = ServerType.LOCAL
    var serverPort: Int = 17479
    var serverAddress: String = "localhost"

    enum class ServerType{
        LOCAL, REMOTE, OFFICIAL
    }
}