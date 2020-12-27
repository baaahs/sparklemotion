package baaahs.show.live

import baaahs.Severity
import baaahs.getValue
import baaahs.show.PatchHolder

open class OpenPatchHolder(
    patchHolder: PatchHolder,
    openContext: OpenContext
) {
    val title = patchHolder.title
    val patches = patchHolder.patches.map { OpenPatch(it, openContext) }
    val problems get() = patches.flatMap { it.problems }
    val problemLevel: Severity? by lazy { problems.maxOfOrNull { it.severity } }

    val controlLayout: Map<String, List<OpenControl>> =
        patchHolder.controlLayout.mapValues { (_, controlRefs) ->
            controlRefs.map { openContext.getControl(it) }
        }

    fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, depth: Int) {
        activePatchSetBuilder.add(this, depth)

        controlLayout.forEach { (panelId, openControls) ->
            openControls.forEach { openControl -> openControl.addTo(activePatchSetBuilder, panelId, depth + 1) }
        }
    }
}