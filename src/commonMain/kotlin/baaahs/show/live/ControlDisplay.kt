package baaahs.show.live

import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.camelize
import baaahs.getBang
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutablePatchHolder

class ControlDisplay(
    show: OpenShow,
    showState: ShowState,
    editMode: Boolean,
    private val editHandler: EditHandler,
    private val dragNDrop: DragNDrop
) {
    private val allPanelBuckets: Map<String, PanelBuckets>
    private val mutableShow = if (editMode) show.edit(showState) else null
    private val placedControls = hashSetOf<OpenControl>()
    private var unplacedControlsDropTarget = UnplacedControlsDropTarget()
    val unplacedControlsDropTargetId = dragNDrop.addDropTarget(unplacedControlsDropTarget)

    init {
        val scene = showState.findScene(show)
        val mutableScene = showState.findMutableScene(mutableShow)

        val patchSet = showState.findPatchSet(show)
        val mutablePatchSet = showState.findMutablePatchSet(mutableShow)

        allPanelBuckets = show.layouts.panelNames.associateWith { panelTitle ->
            PanelBuckets(panelTitle, mutableShow, mutableScene, mutablePatchSet)
        }

        addControlsToBuckets(show.controlLayout, Section.Show)
        scene?.let { addControlsToBuckets(scene.controlLayout, Section.Scene) }
        patchSet?.let { addControlsToBuckets(patchSet.controlLayout, Section.Patch) }
    }

    private val suggestedControls: List<OpenControl>
    init {
        val dataSourcesWithoutControls = show.allDataSources.values -
                show.allControls.flatMap { it.controlledDataSources() }
        suggestedControls = dataSourcesWithoutControls.mapNotNull { it.buildControl()?.open() }
    }
    private val unplacedControls = show.allControls.filter { !placedControls.contains(it) }

    private fun addControlsToBuckets(
        layoutControls: Map<String, List<OpenControl>>,
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

    fun renderUnplacedControls(block: (index: Int, control: OpenControl) -> Unit) {
        unplacedControls.forEachIndexed { index, control -> block(index, control) }
    }

    fun allPlacedControls(): Set<OpenControl> {
        return placedControls
    }

    private fun commitEdit() {
        mutableShow!!
        editHandler.onShowEdit(mutableShow)
    }

    fun release() {
        allPanelBuckets.values.forEach { it.release() }
        dragNDrop.removeDropTarget(unplacedControlsDropTarget)
    }


    inner class PanelBuckets(
        private val panelTitle: String,
        showPatchHolder: MutablePatchHolder?,
        scenePatchHolder: MutablePatchHolder?,
        mutablePatchHolder: MutablePatchHolder?
    ) {
        val showBucket = PanelBucket(Section.Show, showPatchHolder)
        val sceneBucket = PanelBucket(Section.Scene, scenePatchHolder)
        val patchBucket = PanelBucket(Section.Patch, mutablePatchHolder)

        fun add(section: Section, control: OpenControl) {
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
            private val mutablePatchHolder: MutablePatchHolder?
        ) : DropTarget {
            val controls = mutableListOf<PlacedControl>()
            override val type: String get() = "ControlPanel"

            private val dropTargetId = dragNDrop.addDropTarget(this)

            fun add(control: OpenControl) {
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
                mutablePatchHolder!!

                val mutableControls = mutablePatchHolder.editControlLayout(panelTitle)
                val mutableControl = mutableControls.removeAt(fromIndex)
                mutableControls.add(toIndex, mutableControl)
                commitEdit()
            }

            override fun willAccept(draggable: Draggable): Boolean {
                return draggable is PlaceableControl
            }

            override fun getDraggable(index: Int): Draggable {
                return controls[index]
            }

            override fun insertDraggable(draggable: Draggable, index: Int) {
                mutablePatchHolder!!
                draggable as PlaceableControl
                val mutableControls = mutablePatchHolder.editControlLayout(panelTitle)
                mutableControls.add(index, draggable.mutableControl)
            }

            override fun removeDraggable(draggable: Draggable) {
                draggable as PlaceableControl
                draggable.remove()
            }

            inner class PlacedControl(val control: OpenControl, val index: Int) : PlaceableControl {
                override lateinit var mutableControl: MutableControl

                override fun onMove() {
                    commitEdit()
                }

                override fun remove() {
                    mutablePatchHolder!!
                    mutableControl = mutablePatchHolder.removeControl(panelTitle, index)
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
        override val mutableControl: MutableControl
            get() = unplacedControls[index].edit()

        override fun remove() {
            // No-op.
        }

        override fun onMove() {
            commitEdit()
        }
    }

    interface PlaceableControl : Draggable {
        val mutableControl: MutableControl

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