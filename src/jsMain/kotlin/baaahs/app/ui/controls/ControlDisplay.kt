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
    private val placedControls = hashSetOf<Control>()
    private var unplacedControlsDropTarget = UnplacedControlsDropTarget()
    val unplacedControlsDropTargetId = dragNDrop.addDropTarget(unplacedControlsDropTarget)

    init {
        val scene = showState.findScene(show)
        val sceneEditor = showState.findSceneEditor(showEditor)

        val patchSet = showState.findPatchSet(show)
        val patchSetEditor = showState.findPatchSetEditor(showEditor)

        allPanelBuckets = show.layouts.panelNames.associateWith { panelTitle ->
            PanelBuckets(panelTitle, showEditor, sceneEditor, patchSetEditor)
        }

        addControlsToBuckets(show.controlLayout, Section.Show)
        scene?.let { addControlsToBuckets(scene.controlLayout, Section.Scene) }
        patchSet?.let { addControlsToBuckets(patchSet.controlLayout, Section.Patch) }
    }

    private val unplacedControls = show.allDataSources.values.filter { !placedControls.contains(it) }

    private fun addControlsToBuckets(
        layoutControls: Map<String, List<Control>>,
        section: Section
    ) {
        layoutControls.forEach { (panelName, controls) ->
            controls.forEach { control ->
                val panelBuckets = allPanelBuckets.getBang(panelName, "layout panel")
                panelBuckets.add(section, control)
                placedControls.add(control)
            }
        }
    }

    fun render(panelTitle: String, renderBucket: RenderBucket) {
        val panelBuckets = allPanelBuckets.getBang(panelTitle, "layout panel")
        panelBuckets.render(renderBucket)
    }

    fun renderUnplacedControls(block: (index: Int, control: Control) -> Unit) {
        unplacedControls.forEachIndexed { index, dataSource ->
            block(index, dataSource)
        }
    }

    fun allPlacedControls(): Set<Control> {
        return placedControls.toSet()
    }

    private fun commitEdit() {
        showEditor!!
        onEdit(showEditor.getShow(), showEditor.getShowState())
    }

    fun release() {
        allPanelBuckets.values.forEach { it.release() }
        dragNDrop.removeDropTarget(unplacedControlsDropTarget)
    }


    inner class PanelBuckets(
        private val panelTitle: String,
        showEditor: PatchHolderEditor?,
        sceneEditor: PatchHolderEditor?,
        patchEditor: PatchHolderEditor?
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
            private val patchHolderEditor: PatchHolderEditor?
        ) : DropTarget {
            val controls = mutableListOf<PlacedControl>()
            override val type: String get() = "ControlPanel"

            private val dropTargetId = dragNDrop.addDropTarget(this)

            fun add(control: Control) {
                val nextIndex = controls.size
                controls.add(PlacedControl(control, nextIndex))
            }

            fun render(renderBucket: RenderBucket) {
                renderBucket(dropTargetId, section, controls)
            }

            fun release() {
                dragNDrop.removeDropTarget(this)
            }

            override fun suggestId(): String {
                return "$panelTitle ${section.title} $type".camelize()
            }

            override fun moveDraggable(fromIndex: Int, toIndex: Int) {
                patchHolderEditor!!

                val controlLayoutEditor = patchHolderEditor.editControlLayout(panelTitle)
                val controlEditor = controlLayoutEditor.removeAt(fromIndex)
                controlLayoutEditor.add(toIndex, controlEditor)
                commitEdit()
            }

            override fun willAccept(draggable: Draggable): Boolean {
                return draggable is PlaceableControl
            }

            override fun getDraggable(index: Int): Draggable {
                return controls[index]
            }

            override fun insertDraggable(draggable: Draggable, index: Int) {
                patchHolderEditor!!
                draggable as PlaceableControl
                val controlLayoutEditor = patchHolderEditor.editControlLayout(panelTitle)
                controlLayoutEditor.add(index, draggable.controlEditor)
            }

            override fun removeDraggable(draggable: Draggable) {
                draggable as PlaceableControl
                draggable.remove()
            }

            inner class PlacedControl(val control: Control, val index: Int) : PlaceableControl {
                override lateinit var controlEditor: ControlEditor

                override fun onMove() {
                    commitEdit()
                }

                override fun remove() {
                    patchHolderEditor!!
                    controlEditor = patchHolderEditor.removeControl(panelTitle, index)
                }
            }
        }
    }

    inner class UnplacedControlsDropTarget : DropTarget {
        override val type: String get() = "ControlPanel"

        override fun moveDraggable(fromIndex: Int, toIndex: Int) {
            // No-op.
        }

        override fun willAccept(draggable: Draggable): Boolean {
            return draggable is PlaceableControl
        }

        override fun getDraggable(index: Int): Draggable {
            return UnplacedControl(index)
        }

        override fun insertDraggable(draggable: Draggable, index: Int) {
            // No-op.
        }

        override fun removeDraggable(draggable: Draggable) {
            // No-op.
        }
    }

    inner class UnplacedControl(val index: Int) : PlaceableControl {
        override val controlEditor: ControlEditor
            get() = ControlEditor(unplacedControls[index])

        override fun remove() {
            // No-op.
        }

        override fun onMove() {
            commitEdit()
        }
    }

    interface PlaceableControl : Draggable {
        val controlEditor: ControlEditor

        fun remove()
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