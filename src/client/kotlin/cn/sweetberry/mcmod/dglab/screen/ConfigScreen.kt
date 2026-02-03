package cn.sweetberry.mcmod.dglab.screen

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import net.minecraft.client.gui.screen.Screen


object ConfigScreen {
    /**
     * 构建一个 Cloth Config 配置界面 Screen
     *
     * 说明：
     * - 目前必须使用 AutoConfig.getConfigScreen
     * - 被标记 @Deprecated(forRemoval = true)，所以封装在这里
     */
    fun <T : ConfigData> build(parentScreen: Screen?, configClass: Class<T>): Screen {
        // 返回 Optional<Screen> 的值
        @Suppress("DEPRECATION", "removal") // 没有找到替代API，目前暂时使用该API
        return AutoConfig.getConfigScreen(configClass, parentScreen).get()
    }
}
