package baaahs.show.live

import baaahs.camelize
import baaahs.control.OpenButtonGroupControl
import baaahs.getBang
import baaahs.show.Layouts
import baaahs.show.Panel
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.DragNDrop
import baaahs.ui.Draggable
import baaahs.ui.DropTarget
import baaahs.util.Logger

class ControlDisplay(
    internal val show: OpenShow,
    internal val editHandler: EditHandler,
    internal val dragNDrop: DragNDrop
) {
    private val allPanelBuckets = AllPanelBuckets(show.layouts)
    private val placedControls = hashSetOf<OpenControl>()
    val additionalDropTargets = mutableMapOf<OpenControl, OpenButtonGroupControl.ButtonGroupDropTarget>()
    val unplacedControlsDropTarget = UnplacedControlsDropTarget()
    val unplacedControlsDropTargetId = dragNDrop.addDropTarget(unplacedControlsDropTarget)

    init {
        object : OpenShowVisitor() {
            val breadcrumbs = arrayListOf<OpenPatchHolder>()

            override fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
                allPanelBuckets.addTargetPatchHolder(openPatchHolder, breadcrumbs.size)

                breadcrumbs.add(openPatchHolder)
                super.visitPatchHolder(openPatchHolder)
                breadcrumbs.removeLast()
            }

            override fun visitPlacedControl(panel: Panel, openControl: OpenControl) {
                allPanelBuckets.addControl(panel, openControl, breadcrumbs)
                super.visitPlacedControl(panel, openControl)
            }

            override fun visitControl(openControl: OpenControl) {
                placedControls.add(openControl)

                if (openControl.isActive()) {
                    super.visitControl(openControl)
                }
            }
        }.visitShow(show)
    }

    private val suggestedControls: List<OpenControl>
    init {
        val dataSourcesWithoutControls = show.allDataSources.values -
                show.allControls.flatMap { it.controlledDataSources() }
        suggestedControls = dataSourcesWithoutControls.mapNotNull {
            it.buildControl()?.previewOpen(EmptyOpenContext)
        }
    }
    val unplacedControls = (show.allControls + suggestedControls)
        .filter { !placedControls.contains(it) }

    fun render(panel: Panel, renderBucket: RenderBucket) {
        allPanelBuckets.render(panel, renderBucket)
    }

    fun renderUnplacedControls(block: (index: Int, control: OpenControl) -> Unit) {
        unplacedControls.forEachIndexed { index, control -> block(index, control) }
    }

    fun release() {
        allPanelBuckets.release()
        dragNDrop.removeDropTarget(unplacedControlsDropTarget)
        additionalDropTargets.values.forEach { it.release() }
    }

    fun dropTargetFor(buttonGroupControl: OpenButtonGroupControl): DropTarget {
        return additionalDropTargets.getOrPut(buttonGroupControl) {
            buttonGroupControl.createDropTarget(this)
        }
    }

    inner class AllPanelBuckets(layouts: Layouts) {
        private val bucketsByPanel = layouts.panels.map { (_, panel) -> panel to PanelBuckets(panel) }.toMap()
        private val sections = mutableListOf<Section>()
        private var serialInt = 0

        fun addTargetPatchHolder(openPatchHolder: OpenPatchHolder, depth: Int) {
            val section = Section(openPatchHolder, depth, serialInt++)
            sections.add(section)
            bucketsByPanel.forEach { (_, panelBuckets) ->
                panelBuckets.addSection(section)
            }
        }

        fun addControl(panel: Panel, openControl: OpenControl, breadcrumbs: ArrayList<OpenPatchHolder>) {
            val panelBuckets = bucketsByPanel[panel]
            if (panelBuckets != null) {
                panelBuckets.addControl(openControl, breadcrumbs)
            } else {
                // TODO: This should probably show up as a show error.
                logger.warn { "No panel named \"$panel\" exists, so ${openControl.id} won't be visible." }
            }
        }

        fun render(panel: Panel, renderBucket: RenderBucket) {
            val panelBuckets = bucketsByPanel.getBang(panel, "panel")
            panelBuckets.render(sections, renderBucket)
        }

        fun release() {
            bucketsByPanel.values.forEach { it.release() }
        }
    }

    inner class PanelBuckets(
        private val panel: Panel
    ) {
        private val byContainer =
            mapOf<OpenPatchHolder, PanelBucket>().toMutableMap()

        fun addSection(section: Section) {
            byContainer.getOrPut(section.container) { PanelBucket(section) }
        }

        fun addControl(openControl: OpenControl, breadcrumbs: List<OpenPatchHolder>) {
            val container = breadcrumbs.last()
            byContainer[container]!!.add(openControl)
        }

        fun render(sections: List<Section>, renderBucket: RenderBucket) {
            sections.sortedWith(
                compareBy<Section> { it.depth }
                    .thenBy { it.serialInt }
            ).forEach { section ->
                val panelBucket = byContainer[section.container]
                    ?: PanelBucket(section)
                renderBucket(panelBucket)
            }
        }

        fun release() {
            byContainer.values.forEach { it.release() }
            byContainer.clear()
        }

        inner class PanelBucket(val section: Section) : DropTarget {
            val panel: Panel get() = this@PanelBuckets.panel

            val controls = mutableListOf<PlacedControl>()
            override val type: String get() = "ControlContainer"

            override val dropTargetId = dragNDrop.addDropTarget(this)

            fun add(control: OpenControl) {
                val nextIndex = controls.size
                controls.add(PlacedControl(control, nextIndex))
            }

            fun release() {
                dragNDrop.removeDropTarget(this)
            }

            override fun suggestId(): String {
                return "${panel.title} ${section.title} $type".camelize()
            }

            override fun moveDraggable(fromIndex: Int, toIndex: Int) {
                show.edit {
                    val mutablePatchHolder = findPatchHolder(section.container)
                    val mutableControls = mutablePatchHolder.editControlLayout(panel)
                    val mutableControl = mutableControls.removeAt(fromIndex)
                    mutableControls.add(toIndex, mutableControl)
                }.commit(editHandler)
            }

            override fun willAccept(draggable: Draggable): Boolean {
                return draggable is PlaceableControl
            }

            override fun getDraggable(index: Int): Draggable {
                return controls[index]
            }

            override fun insertDraggable(draggable: Draggable, index: Int) {
                draggable as PlaceableControl
                draggable.mutableShow
                    .findPatchHolder(section.container)
                    .editControlLayout(panel)
                    .add(index, draggable.mutableControl)
            }

            override fun removeDraggable(draggable: Draggable) {
                draggable as PlaceableControl
                draggable.remove()
            }

            inner class PlacedControl(val control: OpenControl, val index: Int) : PlaceableControl {
                override val mutableShow: MutableShow by lazy { show.edit() }
                override lateinit var mutableControl: MutableControl

                override fun remove() {
                    val mutablePatchHolder = mutableShow.findPatchHolder(section.container)
                    mutableControl = mutablePatchHolder.removeControl(panel, index)
                }

                override fun onMove() {
                    mutableShow.commit(editHandler)
                }
            }
        }
    }

    inner class UnplacedControlsDropTarget : DropTarget {
        override val type: String get() = "ControlContainer"
        override val dropTargetId: String get() = "not implemented"

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
        override val mutableShow: MutableShow by lazy { show.edit() }
        override val mutableControl: MutableControl by lazy {
            val subject = unplacedControls[index]
            if (suggestedControls.contains(subject)) {
                subject.toNewMutable(mutableShow)
            } else {
                mutableShow.findControl(subject.id)
            }
        }

        override fun remove() {
        }

        override fun onMove() {
            mutableShow.commit(editHandler)
        }
    }

    interface PlaceableControl : Draggable {
        val mutableShow: MutableShow
        val mutableControl: MutableControl

        fun remove()
    }

    class Section(
        val container: OpenPatchHolder,
        val depth: Int,
        val serialInt: Int
    ) {
        val title: String
            get() = container.title
    }

    companion object {
        private val logger = Logger<ControlDisplay>()
    }
}

typealias RenderBucket = (
    panelBucket: ControlDisplay.PanelBuckets.PanelBucket
) -> Unit
