package baaahs.model

import baaahs.geom.Vector3F

interface ModelInfo {
    val center: Vector3F
    val extents: Vector3F

    object Empty : ModelInfo {
        override val center: Vector3F
            get() = Vector3F.origin
        override val extents: Vector3F
            get() = Vector3F.origin
    }
}
