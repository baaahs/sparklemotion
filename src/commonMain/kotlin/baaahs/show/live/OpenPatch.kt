package baaahs.show.live

import baaahs.show.Patch
import baaahs.show.Surfaces

class OpenPatch(
    val shaderInstances: List<LiveShaderInstance>,
    val surfaces: Surfaces
) {
    constructor(patch: Patch, openContext: OpenContext): this(
        patch.shaderInstanceIds.map {
            openContext.getShaderInstance(it)
        },
        patch.surfaces
    )
}