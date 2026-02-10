package cn.sweetberry.mcmod.dglab.config.action

class ActionRuntimeStore {
    private val lastEffectByAction = mutableMapOf<String, List<ActionEffect>>()

    fun getLastEffect(actionId: String): List<ActionEffect>? =
        lastEffectByAction[actionId]

    fun updateLastEffect(actionId: String, effects: List<ActionEffect>) {
        lastEffectByAction[actionId] = effects.map { it.copy() }
    }
}