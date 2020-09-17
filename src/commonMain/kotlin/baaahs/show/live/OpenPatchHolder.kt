package baaahs.show.live

import baaahs.show.PatchHolder

open class OpenPatchHolder(
    patchHolder: PatchHolder,
    openContext: OpenContext
) {
    val title = patchHolder.title
    val patches = patchHolder.patches.map { OpenPatch(it, openContext) }

    val controlLayout: Map<String, List<OpenControl>> =
        patchHolder.controlLayout.mapValues { (_, controlRefs) ->
            controlRefs.map { openContext.getControl(it) }
        }

    fun addTo(activeSetBuilder: ActiveSetBuilder, depth: Int) {
        activeSetBuilder.add(this, depth)

        controlLayout.forEach { (panelId, openControls) ->
            openControls.forEach { openControl -> openControl.addTo(activeSetBuilder, panelId, depth + 1) }
        }
    }
}