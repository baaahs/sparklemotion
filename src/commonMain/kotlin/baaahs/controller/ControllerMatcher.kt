package baaahs.controller

import baaahs.scene.MutableControllerConfig

class ControllerMatcher(val searchString: String) {
    fun matches(state: ControllerState?, mutableControllerConfig: MutableControllerConfig?): Boolean {
        if (searchString.isBlank()) return true

        return mutableControllerConfig?.matches(this) ?: false ||
                state?.matches(this) ?: false
    }

    fun matches(vararg s: String?): Boolean {
        return s.filterNotNull().any { it.lowercase().contains(searchString) }
    }
}