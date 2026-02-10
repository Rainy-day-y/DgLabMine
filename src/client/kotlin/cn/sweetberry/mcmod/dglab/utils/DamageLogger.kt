package cn.sweetberry.mcmod.dglab.utils

import cn.sweetberry.mcmod.dglab.DgLabMineClient
import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import net.minecraft.client.MinecraftClient

object DamageLogger {
    fun logDamageEvent(data: DamageData, client: MinecraftClient) {
        val type = client.world!!.registryManager!!
            .getOrThrow(net.minecraft.registry.RegistryKeys.DAMAGE_TYPE)!!
            .getEntry(data.typeId!!)
            .orElseThrow()
        val longMessage = """[DgLab] Received Damage Event:
            | - Type: ${type.getKey().orElseThrow().value}
            | - Direct: ${data.isDirect}
            | - Phase: ${data.phase}
            | - Cancelled: ${data.isCancelled}
            | - Damage Amount: ${data.damageAmount}
            | - Raw Damage Amount: ${data.rawDamageAmount}
            | - Difficulty Reduced: ${data.difficultyReduced}
            | - Ice Reduced: ${data.iceReduced}
            | - Helmet Reduced: ${data.helmetReduced}
            | - Shield Blocked: ${data.shieldBlocked}
            | - Armor Reduced: ${data.armorReduced}
            | - Effect & Enchantment Reduced: ${data.effectAndEnchantmentReduced}
            | - Absorbed: ${data.absorbed}
            | - Other Reduced: ${data.otherReduced}
        """.trimIndent()
        val shortMessage = "Damage Event: Type: ${type.getKey().orElseThrow().value}, Damage Amount=${data.damageAmount}"

        DgLabMineClient.debugLog(longMessage)
        client.inGameHud.setOverlayMessage(net.minecraft.text.Text.of(shortMessage), true)
    }

    fun logDamageEvent(data: DamageData) {
        val client = MinecraftClient.getInstance()
        logDamageEvent(data, client)
    }
}