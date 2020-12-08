package baaahs.show.live

import baaahs.fixtures.Fixture
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

    val serial = nextSerial++

    val problems get() = shaderInstances.flatMap { it.problems }

    override fun toString(): String {
        return "OpenPatch(shaderInstances=$shaderInstances, surfaces=$surfaces)"
    }

    fun matches(fixture: Fixture) = surfaces.matches(fixture)

    companion object {
        private var nextSerial = 0
    }
}