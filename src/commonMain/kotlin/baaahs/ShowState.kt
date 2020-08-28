package baaahs

import baaahs.show.PatchHolder
import baaahs.show.PatchSet
import baaahs.show.Scene
import baaahs.show.Show
import baaahs.show.mutable.MutableShow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.min

@Serializable
data class ShowState(
    val selectedScene: Int,
    val patchSetSelections: List<Int>
) {
    @Transient
    val selectedPatchSet: Int =
        if (selectedScene == -1) -1 else patchSetSelections[selectedScene]

    fun findScene(show: Show): Scene? {
        if (selectedScene == -1) return null

        if (selectedScene >= show.scenes.size) {
            error("scene $selectedScene out of bounds (have ${show.scenes.size})")
        }

        return show.scenes[selectedScene]
    }

    fun findMutableScene(mutableShow: MutableShow?): MutableShow.MutableScene? {
        if (selectedScene == -1) return null
        return mutableShow?.getMutableScene(selectedScene)
    }

    fun findPatchSet(show: Show): PatchSet? {
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

    fun findMutablePatchSet(mutableShow: MutableShow?): MutableShow.MutableScene.MutablePatchSet? {
        if (selectedPatchSet == -1) return null

        val mutableScene = findMutableScene(mutableShow)
        return mutableScene?.getMutablePatchSet(selectedPatchSet)
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

    fun getActivePatchHolders(show: Show): List<PatchHolder> {
        val patchHolders = arrayListOf<PatchHolder>(show)
        findScene(show)?.let { patchHolders.add(it) }
        findPatchSet(show)?.let { patchHolders.add(it) }
        return patchHolders
    }

    companion object {
        val Empty: ShowState = ShowState(-1, emptyList())

        fun forShow(show: Show): ShowState =
            ShowState(0, show.scenes.map { 0 })
    }
}