package baaahs.controller

import baaahs.fixtures.FixtureInfo
import baaahs.scene.MutableControllerConfig

class ControllerMatcher(val searchString: String = "") {
    private val searchTerms = searchString.lowercase().split(" ")

    fun matches(
        state: ControllerState?,
        mutableControllerConfig: MutableControllerConfig?,
        fixtureInfos: List<FixtureInfo>?
    ): Boolean {
        if (searchTerms.isEmpty()) return true

        return mutableControllerConfig?.matches(this) == true ||
                state?.matches(this) == true ||
                fixtureInfos?.any { fixtureInfo ->
                    searchTerms.any { fixtureInfo.matches(it) }
                } == true
    }

    fun matches(vararg s: String?): Boolean {
        return s.filterNotNull().any { searchTarget ->
            searchTerms.any { searchTarget.lowercase().contains(it) }
        }
    }
}