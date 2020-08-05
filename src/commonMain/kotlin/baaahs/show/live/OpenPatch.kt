package baaahs.show.live

import baaahs.getBang
import baaahs.show.Patch
import baaahs.show.Surfaces

class OpenPatch(
    val shaderInstances: List<LiveShaderInstance>,
    val surfaces: Surfaces
) {
    constructor(patch: Patch, allShaderInstances: Map<String, LiveShaderInstance>): this(
        patch.shaderInstanceIds.map {
            allShaderInstances.getBang(it, "shader instance")
        },
        patch.surfaces
    )
}