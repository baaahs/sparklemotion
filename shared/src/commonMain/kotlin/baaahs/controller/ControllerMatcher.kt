package baaahs.controller

import baaahs.fixtures.FixtureInfo
import baaahs.scene.MutableControllerConfig

class ControllerMatcher(val searchString: String) {
    fun matches(
        state: ControllerState?,
        mutableControllerConfig: MutableControllerConfig?,
        fixtureInfos: List<FixtureInfo>?
    ): Boolean {
        if (searchString.isBlank()) return true

        return mutableControllerConfig?.matches(this) ?: false ||
                state?.matches(this) ?: false ||
                fixtureInfos?.any { it.matches(searchString) } ?: false
    }

    fun matches(vararg s: String?): Boolean {
        return s.filterNotNull().any { it.lowercase().contains(searchString) }
    }
}