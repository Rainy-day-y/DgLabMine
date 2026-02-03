package cn.sweetberry.mcmod.dglab

import cn.sweetberry.mcmod.dglab.config.ModConfig
import cn.sweetberry.mcmod.dglab.screen.ConfigScreen
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

object DgLabMineKeyBindings {
    lateinit var OPEN_CONFIG: KeyBinding

    fun register() {
        OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.dglab-minecraft.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KeyBinding.Category(Identifier.of("dglab-minecraft","key_category") )
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (OPEN_CONFIG.wasPressed()) {
                val screen =ConfigScreen.build(client.currentScreen, ModConfig::class.java)
                client.setScreen(screen)
            }
        }
    }
}