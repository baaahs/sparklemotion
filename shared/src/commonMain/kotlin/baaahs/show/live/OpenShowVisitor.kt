package baaahs.show.live

import baaahs.show.LegacyTab
import baaahs.show.Panel

abstract class OpenShowVisitor {
    open fun visitShow(openShow: OpenShow) {
        visitPatchHolder(openShow)
        openShow.openLayouts.formats.values.forEach { layout ->
            visitLayout(layout)
        }

        openShow.implicitControls.forEach { visitUnplacedControl(it) }
    }

    open fun visitLayout(layout: OpenLayout) {
        layout.tabs.forEach { visitLayoutTab(layout, it) }
    }

    open fun visitLayoutTab(layout: OpenLayout, tab: OpenTab) {
        when (tab) {
            is OpenGridTab -> visitGridLayout(tab)
            is LegacyTab -> {}
        }
    }

    open fun visitGridLayout(tab: OpenIGridLayout) {
        tab.items.forEach { item ->
            visitGridItem(item)
        }
    }

    open fun visitGridItem(item: OpenGridItem) {
        visitControl(item.control, item.layout)
    }

    open fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
        openPatchHolder.patches.forEach {
            visitPatch(it)
        }

        openPatchHolder.controlLayout.forEach { (panel, openControls) ->
            openControls.forEach { openControl ->
                visitPlacedControl(panel, openControl)
            }
        }
    }

    open fun visitPlacedControl(panel: Panel, openControl: OpenControl) {
        visitControl(openControl, null)
    }

    open fun visitUnplacedControl(openControl: OpenControl) {
        visitControl(openControl, null)
    }

    open fun visitButtonGroupButton(controlContainer: ControlContainer, openControl: OpenControl) {
        visitControl(openControl, null)
    }

    open fun visitControl(openControl: OpenControl, layout: OpenGridLayout?) {
        if (openControl is OpenPatchHolder) {
            visitPatchHolder(openControl)
        }

        if (openControl is ControlContainer) {
            visitControlContainer(openControl)
        }

        if (layout != null) {
            visitGridLayout(layout)
        }
    }

    open fun visitControlContainer(controlContainer: ControlContainer) {
        controlContainer.containedControls().forEach { containedControl ->
            visitButtonGroupButton(controlContainer, containedControl)
        }
    }

    open fun visitPatch(openPatch: OpenPatch) {
    }
}