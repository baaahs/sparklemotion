package baaahs.show.live

import baaahs.getValue
import baaahs.show.Panel
import baaahs.show.PatchHolder
import baaahs.sm.webapi.Severity
import baaahs.sm.webapi.severity

open class OpenPatchHolder(
    patchHolder: PatchHolder,
    openContext: OpenContext
) {
    val title = patchHolder.title
    val patches = patchHolder.patchIds.map { openContext.getPatch(it) }
    val problems get() = patches.flatMap { it.problems }
    val problemLevel: Severity? by lazy { problems.severity() }

    val controlLayout: Map<Panel, List<OpenControl>> =
        patchHolder.controlLayout.map { (panelId, controlRefs) ->
            openContext.getPanel(panelId) to controlRefs.map { openContext.getControl(it) }
        }.toMap()

    open fun addTo(activePatchSetBuilder: ActivePatchSet.Builder, depth: Int) {
        activePatchSetBuilder.add(this, depth)

//        controlLayout.forEach { (panel, openControls) ->
//            openControls.forEach { openControl -> openControl.addTo(activePatchSetBuilder, panel, depth + 1) }
//        }
    }
}