package baaahs.show.live

interface ControlsInfo {
    val unplacedControls: Set<OpenControl>
    val relevantUnplacedControls: List<OpenControl>

    fun release()
}

class GridLayoutControlsInfo(show: OpenShow, activePatchSet: ActivePatchSet) : ControlsInfo {
    private val placedControls: Set<OpenControl>
    private val onScreenControls: Set<OpenControl>
    override val unplacedControls: Set<OpenControl>
    override val relevantUnplacedControls: List<OpenControl>

    init {
        val placedControls = mutableSetOf<OpenControl>()
        val onScreenControls = mutableSetOf<OpenControl>()

        fun OpenIGridLayout.visitItems(id: String, isOnScreen: Boolean) {
            items.forEach { gridItem ->
                placedControls.add(gridItem.control)
                if (isOnScreen)
                    onScreenControls.add(gridItem.control)

                gridItem.layout?.visitItems(id + "::" + gridItem.control.id, isOnScreen)
            }
        }

        val currentTab = show.openLayouts.currentFormat?.currentTab as? OpenGridTab
        show.openLayouts.currentFormat?.tabs?.forEach { tab ->
            if (tab is OpenGridTab) {
                val isOnScreen = tab === currentTab


                currentTab?.visitItems("_tab_", isOnScreen)
            }
        }
        this.placedControls = placedControls.toSet()
        this.onScreenControls = onScreenControls.toSet()

        val activeFeeds = activePatchSet.allFeeds

        val offScreenControls = show.implicitControls.toSet() - onScreenControls
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
    }

    override fun release() {
    }
}