package baaahs.show.live

import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.PatchHolder

open class OpenPatchHolder(
    patchHolder: PatchHolder,
    allShaderInstances: Map<String, LiveShaderInstance>,
    allDataSources: Map<String, DataSource>
) {
    val title = patchHolder.title
    val patches = patchHolder.patches.map { OpenPatch(it, allShaderInstances) }

    val controlLayout: Map<String, List<Control>> = patchHolder.controlLayout.mapValues { (_, controlRefs) ->
        controlRefs.map { it.dereference(allDataSources) }
    }
}