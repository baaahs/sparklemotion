package baaahs.show.live

import baaahs.control.OpenVacuityControl

interface ControlsInfo {
    val onScreenControls: Set<OpenControl>
    val offScreenControls: Set<OpenControl>
    val unplacedControls: Set<OpenControl>
    val visiblePlacedControls: List<OpenControl>
    val relevantUnplacedControls: List<OpenControl>
    val orderedOnScreenControls: List<OpenControl>

    fun release()
}

class GridLayoutControlsInfo(show: OpenShow, activePatchSet: ActivePatchSet) : ControlsInfo {
    private val placedControls: Set<OpenControl>
    override val onScreenControls: Set<OpenControl>
    override val offScreenControls: Set<OpenControl>
    override val unplacedControls: Set<OpenControl>
    override val visiblePlacedControls: List<OpenControl>
    override val relevantUnplacedControls: List<OpenControl>
    override val orderedOnScreenControls: List<OpenControl>

    init {
        val placedControls = mutableSetOf<OpenControl>()
        val onScreenControls = mutableSetOf<OpenControl>()
        val visiblePlacedControls = mutableListOf<OpenControl>()

        fun OpenIGridLayout.visitItems(id: String, isOnScreen: Boolean) {
            items.forEach { gridItem ->
                placedControls.add(gridItem.control)
                if (isOnScreen)
                    onScreenControls.add(gridItem.control).also { added ->
                        if (added) visiblePlacedControls.add(gridItem.control)
                    }

                gridItem.layout?.visitItems(id + "::" + gridItem.control.id, isOnScreen)
            }
        }
        show.visitTabs { layout, isOnScreen ->
            layout.visitItems("_tab_", isOnScreen)
        }

        this.placedControls = placedControls.toSet()
        this.onScreenControls = onScreenControls.toSet()
        this.visiblePlacedControls = visiblePlacedControls.toList()

        val activeFeeds = activePatchSet.allFeeds

        offScreenControls = show.implicitControls.toSet() - onScreenControls
        this.relevantUnplacedControls = offScreenControls.filter { control ->
            activeFeeds.containsAll(control.controlledFeeds())
        }.sortedBy { control ->
            (control as? FeedOpenControl)?.inUse = true
            control.controlledFeeds().firstOrNull()?.title
                ?: "zzzzz"
        }
        println("relevantUnplacedControls = ${relevantUnplacedControls}")

        placedControls.forEach { control ->
            (control as? FeedOpenControl)?.inUse =
                activeFeeds.containsAll(control.controlledFeeds())
        }

        unplacedControls = show.allControls.toSet() - placedControls

        // TODO: This list isn't rebuilt when current tab changes, fix!
        orderedOnScreenControls = buildList {
            fun OpenIGridLayout.visitItems() {
                items.forEach { gridItem ->
                    add(gridItem.control)
                    if (gridItem.control is OpenVacuityControl) {
                        addAll(relevantUnplacedControls)
                    }
                    gridItem.layout?.visitItems()
                }
            }

            show.visitTabs { layout, isOnScreen ->
                if (isOnScreen) layout.visitItems()
            }
        }
    }

    override fun release() {
    }
}