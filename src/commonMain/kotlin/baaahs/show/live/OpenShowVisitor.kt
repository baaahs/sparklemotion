package baaahs.show.live

import baaahs.show.Panel

abstract class OpenShowVisitor {
    open fun visitShow(openShow: OpenShow) {
        visitPatchHolder(openShow)

        openShow.implicitControls.forEach { visitUnplacedControl(it) }
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
        visitControl(openControl)
    }

    open fun visitUnplacedControl(openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitButtonGroupButton(controlContainer: ControlContainer, openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitControl(openControl: OpenControl) {
        if (openControl is OpenPatchHolder) {
            visitPatchHolder(openControl)
        }

        if (openControl is ControlContainer) {
            visitControlContainer(openControl)
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