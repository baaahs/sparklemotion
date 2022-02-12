package baaahs.controller

import kotlinx.serialization.Serializable

@Serializable
data class ControllerId(val controllerType: String, val id: String) {
    fun name(): String = "$controllerType:$id"

    companion object {
        fun fromName(name: String) =
            name.split(":", limit = 2).let {
                if (it.size != 2) error("Can't create ControllerId from $name.")
                ControllerId(it[0], it[1])
            }
    }
}