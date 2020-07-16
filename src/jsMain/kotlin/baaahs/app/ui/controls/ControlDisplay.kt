package baaahs.app.ui.controls

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.camelize
import baaahs.getBang
import baaahs.show.*

class ControlDisplay(
    show: OpenShow,
    showState: ShowState,
    private val editMode: Boolean,
    private val onEdit: (Show, ShowState) -> Unit,
    private val dragNDrop: DragNDrop
) {
    private val allPanelBuckets: Map<String, PanelBuckets>
    private val showEditor = if (editMode) show.edit(showState) else null
    private val showBuilder = ShowBuilder()

    init {
        val scene = showState.findScene(show)
        val sceneEditor = showEditor?.getSceneEditor(showState.selectedScene)

        val patchSet = showState.findPatchSet(show)
        val patchSetEditor = sceneEditor?.getPatchSetEditor(showState.selectedPatchSet)

        allPanelBuckets = show.layouts.panelNames.associateWith { panelTitle ->
            PanelBuckets(panelTitle, showEditor, sceneEditor, patchSetEditor)
        }

        addControlsToBuckets(show.controlLayout, Section.Show)
        scene?.let { addControlsToBuckets(scene.controlLayout, Section.Scene) }
        patchSet?.let { addControlsToBuckets(patchSet.controlLayout, Section.Patch) }
    }

    private fun addControlsToBuckets(
        layoutControls: Map<String, List<Control>>,
        section: Section
    ) {
        layoutControls.forEach { (panelName, controls) ->
            controls.forEach { control ->
                val panelBuckets = allPanelBuckets.getBang(panelName, "layout panel")
                panelBuckets.add(section, control)
            }
        }
    }

    fun render(panelTitle: String, renderBucket: RenderBucket) {
        val panelBuckets = allPanelBuckets.getBang(panelTitle, "layout panel")
        panelBuckets.render(renderBucket)
    }

    private fun commitEdit() {
        showEditor!!
        onEdit(showEditor.getShow(), showEditor.getShowState())
    }

    fun release() {
        allPanelBuckets.values.forEach { it.release() }
    }


    inner class PanelBuckets(
        private val panelTitle: String,
        showEditor: PatchyEditor?,
        sceneEditor: PatchyEditor?,
        patchEditor: PatchyEditor?
    ) {
        val showBucket = PanelBucket(Section.Show, showEditor)
        val sceneBucket = PanelBucket(Section.Scene, sceneEditor)
        val patchBucket = PanelBucket(Section.Patch, patchEditor)

        fun add(section: Section, control: Control) {
            section.getBucket(this).add(control)
        }

        fun render(renderBucket: RenderBucket) {
            renderSection(Section.Show, renderBucket)
            renderSection(Section.Scene, renderBucket)
            renderSection(Section.Patch, renderBucket)
        }

        private fun renderSection(section: Section, renderBucket: RenderBucket) {
            section.getBucket(this).render(renderBucket)
        }

        fun release() {
            showBucket.release()
            sceneBucket.release()
            patchBucket.release()
        }

        inner class PanelBucket(
            private val section: Section,
            private val patchyEditor: PatchyEditor?
        ) : DropTarget {
            val controls = mutableListOf<PlacedControl>()
            override val type: String get() = "ControlPanel"

            private val dropTargetId = if (editMode) {
                dragNDrop.addDropTarget(this)
            } else ""

            fun add(control: Control) {
                val nextIndex = controls.size
                controls.add(PlacedControl(control, nextIndex))
            }

            fun render(renderBucket: RenderBucket) {
                renderBucket(dropTargetId, section, controls)
            }

            fun release() {
                if (editMode) {
                    dragNDrop.removeDropTarget(this)
                }
            }

            override fun suggestId(): String {
                return "$panelTitle ${section.title} $type".camelize()
            }

            override fun moveDraggable(fromIndex: Int, toIndex: Int) {
                patchyEditor!!

                val controlLayoutEditor = patchyEditor.editControlLayout(panelTitle)
                val controlEditor = controlLayoutEditor.removeAt(fromIndex)
                controlLayoutEditor.add(toIndex, controlEditor)
                commitEdit()
            }

            override fun willAccept(draggable: Draggable): Boolean {
                return true
            }

            override fun getDraggable(index: Int): Draggable {
                return controls[index]
            }

            override fun insertDraggable(draggable: Draggable, index: Int) {
                patchyEditor!!
                draggable as PlacedControl
                val controlLayoutEditor = patchyEditor.editControlLayout(panelTitle)
                controlLayoutEditor.add(index, draggable.controlEditor!!)
            }

            override fun removeDraggable(draggable: Draggable) {
                draggable as PlacedControl
                draggable.remove()
            }

            inner class PlacedControl(val control: Control, val index: Int) : Draggable {
                val id = control.toControlRef(showBuilder).toShortString()
                var controlEditor : ControlEditor? = null

                override fun onMove() {
                    commitEdit()
                }

                fun remove() {
                    patchyEditor!!
                    controlEditor = patchyEditor.removeControl(panelTitle, index)
                }
            }
        }
    }

    enum class Section(
        val title: String,
        val getBucket: (panelBuckets: PanelBuckets) -> PanelBuckets.PanelBucket
    ) {
        Show("Show Controls", { it.showBucket }),
        Scene("Scene Controls", { it.sceneBucket }),
        Patch("Patch Controls", { it.patchBucket })
    }
}

typealias RenderBucket = (
    dropTargetId: String,
    section: ControlDisplay.Section,
    controls: List<ControlDisplay.PanelBuckets.PanelBucket.PlacedControl>
) -> Unit