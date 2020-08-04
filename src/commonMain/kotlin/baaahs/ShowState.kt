package baaahs

import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditor
import baaahs.show.mutable.ShowEditor
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

    fun findScene(show: OpenShow): OpenShow.OpenScene? {
        if (selectedScene == -1) return null

        if (selectedScene >= show.scenes.size) {
            error("scene $selectedScene out of bounds (have ${show.scenes.size})")
        }

        return show.scenes[selectedScene]
    }

    fun findSceneEditor(showEditor: ShowEditor?): ShowEditor.SceneEditor? {
        if (selectedScene == -1) return null
        return showEditor?.getSceneEditor(selectedScene)
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

    fun findPatchSetEditor(showEditor: ShowEditor?): PatchHolderEditor? {
        if (selectedPatchSet == -1) return null

        val sceneEditor = findSceneEditor(showEditor)
        return sceneEditor?.getPatchSetEditor(selectedPatchSet)
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
        val Empty: ShowState = ShowState(-1, emptyList())

        fun forShow(show: Show): ShowState =
            ShowState(0, show.scenes.map { 0 })
    }
}