package cn.sweetberry.mcmod.dglab.config.action


sealed class Action {

    enum class Type {
        SEND_PULSE,
        SET_STRENGTH,
        CLEAR_PULSE
    }

    abstract val id: String

    abstract val type: Type

    abstract fun buildCommand(ctx: ActionContext): OutgoingCommand?
}
