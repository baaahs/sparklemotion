package baaahs.show.live

import baaahs.app.ui.DragNDrop
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.camelize
import baaahs.getBang
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow

class ControlDisplay(
    show: OpenShow,
    editMode: Boolean,
    private val editHandler: EditHandler,
    private val dragNDrop: DragNDrop
) {
    private val allPanelBuckets: Map<String, PanelBuckets>
    private val mutableShow = if (editMode) show.edit() else null
    private val placedControls = hashSetOf<OpenControl>()
    private var unplacedControlsDropTarget = UnplacedControlsDropTarget(mutableShow)
    val unplacedControlsDropTargetId = dragNDrop.addDropTarget(unplacedControlsDropTarget)

    init {
        allPanelBuckets = show.layouts.panelNames.associateWith { panelTitle -> PanelBuckets(panelTitle) }

        object : OpenShowVisitor() {
            val breadcrumbs = arrayListOf<OpenPatchHolder>()

            override fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
                breadcrumbs.add(openPatchHolder)
                super.visitPatchHolder(openPatchHolder)
                breadcrumbs.removeLast()
            }

            override fun visitPlacedControl(panelName: String, openControl: OpenControl) {
                allPanelBuckets.getBang(panelName, "panel")
                    .addControl(openControl, breadcrumbs)
                placedControls.add(openControl)

                if (openControl.isActive()) {
                    super.visitPlacedControl(panelName, openControl)
                }
            }
        }.visitShow(show)
    }

    private val suggestedControls: List<OpenControl>
    init {
        val dataSourcesWithoutControls = show.allDataSources.values -
                show.allControls.flatMap { it.controlledDataSources() }
        suggestedControls = dataSourcesWithoutControls.mapNotNull { it.buildControl()?.open() }
    }
    val unplacedControls = show.allControls.filter { !placedControls.contains(it) }

    init {
        println(show.fakeRender(this))
    }

    fun render(panelTitle: String, renderBucket: RenderBucket) {
        val panelBuckets = allPanelBuckets.getBang(panelTitle, "panel")
        panelBuckets.render(renderBucket)
    }

    fun renderUnplacedControls(block: (index: Int, control: OpenControl) -> Unit) {
        unplacedControls.forEachIndexed { index, control -> block(index, control) }
    }

    private fun commitEdit() {
        mutableShow!!
        editHandler.onShowEdit(mutableShow)
    }

    fun release() {
        allPanelBuckets.values.forEach { it.release() }
        dragNDrop.removeDropTarget(unplacedControlsDropTarget)
    }

    class AllPanelBuckets

    inner class PanelBuckets(
        private val panelTitle: String
    ) {
        val panelBuckets = arrayListOf<PanelBucket>()

        fun add(section: Section, control: OpenControl) {
            section.getBucket(this).add(control)
        }

        fun render(renderBucket: RenderBucket) {
            panelBuckets.forEach { panelBucket ->
                renderSection(panelBucket.section, renderBucket)
            }
        }

        private fun renderSection(section: Section, renderBucket: RenderBucket) {
            section.getBucket(this).render(renderBucket)
        }

        fun release() {
            panelBuckets.forEach { it.release() }
        }

        fun addControl(openControl: OpenControl, breadcrumbs: List<OpenPatchHolder>) {
            while (panelBuckets.size < breadcrumbs.size) {
                val depth = panelBuckets.size
                panelBuckets.add(PanelBucket(Section(depth, breadcrumbs[depth]) { panelBuckets ->
                    panelBuckets.panelBuckets[depth]
                }, mutableShow))
            }

            panelBuckets[breadcrumbs.size - 1].add(openControl)
        }

        inner class PanelBucket(
            val section: Section,
            private val mutableShow: MutableShow?
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
                val mutablePatchHolder = mutableShow!!.findPatchHolder(section.parent)
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
                val mutablePatchHolder = mutableShow!!.findPatchHolder(section.parent)

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
                    val mutablePatchHolder = mutableShow!!.findPatchHolder(section.parent)
                    mutableControl = mutablePatchHolder.removeControl(panelTitle, index)
                }
            }
        }
    }

    inner class UnplacedControlsDropTarget(
        private val mutableShow: MutableShow?
    ) : DropTarget {
        override val type: String get() = "ControlPanel"

        override fun moveDraggable(fromIndex: Int, toIndex: Int) {
            // No-op.
        }

        override fun willAccept(draggable: Draggable): Boolean {
            return draggable is PlaceableControl
        }

        override fun getDraggable(index: Int): Draggable {
            return UnplacedControl(index, mutableShow)
        }

        override fun insertDraggable(draggable: Draggable, index: Int) {
            // No-op.
        }

        override fun removeDraggable(draggable: Draggable) {
            // No-op.
        }
    }

    inner class UnplacedControl(
        val index: Int,
        private val mutableShow: MutableShow?
    ) : PlaceableControl {
        override val mutableControl: MutableControl
            get() = mutableShow!!.findControl(unplacedControls[index].id)

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

    class Section(
        val depth: Int,
        val parent: OpenPatchHolder,
        val getBucket: (panelBuckets: PanelBuckets) -> PanelBuckets.PanelBucket
    ) {
        val title: String
            get() = parent.title
    }
}

typealias RenderBucket = (
    dropTargetId: String,
    section: ControlDisplay.Section,
    controls: List<ControlDisplay.PanelBuckets.PanelBucket.PlacedControl>
) -> Unit


fun OpenShow.fakeRender(controlDisplay: ControlDisplay): String {
    val buf = StringBuilder()

    layouts.panelNames.forEach { panelName ->
        buf.append("$panelName:\n")
        controlDisplay.render(panelName) { dropTargetId, section, controls ->
            buf.append("  |${section.title}| ${controls.joinToString { it.control.id }}\n")
        }
    }

    return buf.trim().toString()
}
