package baaahs.show.live

import baaahs.getBang
import baaahs.show.Control
import baaahs.show.PatchHolder

open class OpenPatchHolder(
    patchHolder: PatchHolder,
    allShaderInstances: Map<String, LiveShaderInstance>,
    allControls: Map<String, Control>
) {
    val title = patchHolder.title
    val patches = patchHolder.patches.map { OpenPatch(it, allShaderInstances) }

    val controlLayout: Map<String, List<Control>> = patchHolder.controlLayout.mapValues { (_, controlRefs) ->
        controlRefs.map { allControls.getBang(it, "control") }
    }
}