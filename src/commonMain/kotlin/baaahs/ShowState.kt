package baaahs

import baaahs.show.Show
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class ShowState(
    val selectedScene: Int,
    val patchSetSelections: List<Int>
) {
    val selectedPatchSet: Int get() = patchSetSelections[selectedScene]

    fun findScene(show: OpenShow): OpenShow.OpenScene? {
        if (selectedScene == -1) return null

        if (selectedScene >= show.scenes.size) {
            error("scene $selectedScene out of bounds (have ${show.scenes.size})")
        }

        return show.scenes[selectedScene]
    }

    fun findPatchSet(show: OpenShow): OpenShow.OpenScene.OpenPatchSet? {
        if (selectedPatchSet == -1) return null

        val scene = findScene(show) ?: return null

        if (selectedScene >= patchSetSelections.size) {
            error("scene $selectedScene patch set out of bounds (have ${patchSetSelections.size})")
        }

        if (selectedPatchSet >= scene.patchSets.size) {
            error(
                "patch set $selectedPatchSet out of bounds " +
                        "(have ${patchSetSelections.size} for scene $selectedScene)"
            )
        }

        return scene.patchSets[selectedPatchSet]
    }

    fun selectScene(i: Int) = copy(selectedScene = i)
    fun selectPatchSet(i: Int) = copy(patchSetSelections = patchSetSelections.replacing(selectedScene, i))
    fun withPatchSetSelections(selections: List<Int>) = copy(patchSetSelections = selections)

    /**
     * Returns a ShowState whose parameters fit within the specified [Show].
     */
    fun boundedBy(show: Show): ShowState {
        return ShowState(
            selectedScene = min(selectedScene, show.scenes.size - 1),
            patchSetSelections = show.scenes.mapIndexed { index, scene ->
                min(
                    patchSetSelections.getOrNull(index) ?: 0,
                    scene.patchSets.size - 1
                )
            }
        )
    }

    companion object {
        val Empty: ShowState = ShowState(0, emptyList())

        fun forShow(show: Show): ShowState =
            ShowState(0, show.scenes.map { 0 })
    }
}

@Serializable
data class ShowWithState(val show: Show, val showState: ShowState)

@Serializable
data class NullableShowWithState(val showWithState: ShowWithState?)

fun Show.withState(showState: ShowState): ShowWithState {
    return ShowWithState(this, showState.boundedBy(this))
}