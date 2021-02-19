package baaahs.show.live

import baaahs.Severity
import baaahs.getValue
import baaahs.severity
import baaahs.show.Panel
import baaahs.show.PatchHolder

open class OpenPatchHolder(
    patchHolder: PatchHolder,
    openContext: OpenContext
) {
    val title = patchHolder.title
    val patches = patchHolder.patches.map { OpenPatch(it, openContext) }
    val problems get() = patches.flatMap { it.problems }
    val problemLevel: Severity? by lazy { problems.severity() }

    val controlLayout: Map<Panel, List<OpenControl>> =
        patchHolder.controlLayout.map { (panelId, controlRefs) ->
            openContext.getPanel(panelId) to controlRefs.map { openContext.getControl(it) }
        }.toMap()

    fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, depth: Int) {
        activePatchSetBuilder.add(this, depth)

        controlLayout.forEach { (panel, openControls) ->
            openControls.forEach { openControl -> openControl.addTo(activePatchSetBuilder, panel, depth + 1) }
        }
    }
}