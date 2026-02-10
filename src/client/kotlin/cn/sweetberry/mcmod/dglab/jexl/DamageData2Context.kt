package cn.sweetberry.mcmod.dglab.jexl

import cn.sweetberry.mcmod.vitalsignals.network.damage.DamageData
import net.minecraft.client.MinecraftClient
import org.apache.commons.jexl3.MapContext
import org.apache.commons.jexl3.JexlContext
import org.slf4j.LoggerFactory

fun DamageData.toJexlContext(): JexlContext {
    val client = MinecraftClient.getInstance()
    val type: String = try {
        client.world!!.registryManager!!
            .getOrThrow(net.minecraft.registry.RegistryKeys.DAMAGE_TYPE)!!
            .getEntry(this.typeId!!)
            .orElseThrow()
            .getKey()
            .orElseThrow()
            .value
            .toString()
    }catch (e: Exception){
        LoggerFactory.getLogger(this::class.java)
            .warn("An error occurred when convert a DamageData to JexlContent: ${e.message}")
        ""
    }

    val context = MapContext()

    context["gameVersion"] = this.gameVersion
    context["version"] = this.version

    context["type"] = type
    context["isDirect"] = this.isDirect
    context["isCancelled"] = this.isCancelled

    context["damageAmount"] = this.damageAmount
    context["rawDamageAmount"] = this.rawDamageAmount

    context["difficultyReduced"] = this.difficultyReduced
    context["shieldBlocked"] = this.shieldBlocked
    context["iceReduced"] = this.iceReduced
    context["helmetReduced"] = this.helmetReduced
    context["armorReduced"] = this.armorReduced
    context["effectAndEnchantmentReduced"] = this.effectAndEnchantmentReduced
    context["absorbed"] = this.absorbed
    context["otherReduced"] = this.otherReduced

    return context
}
