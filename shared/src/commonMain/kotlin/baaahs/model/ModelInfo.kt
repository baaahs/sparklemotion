package baaahs.model

import baaahs.geom.Vector3F
import baaahs.scene.OpenScene

interface ModelInfo {
    val center: Vector3F
    val extents: Vector3F

    val boundsMin get() = center - extents / 2f
    val boundsMax get() = center + extents / 2f

    object Empty : ModelInfo {
        override val center: Vector3F
            get() = Vector3F.origin
        override val extents: Vector3F
            get() = Vector3F.origin
    }

    companion object {
        val EmptyModel = Model("Empty", emptyList())
        val EmptyScene = OpenScene(EmptyModel)
    }
}
