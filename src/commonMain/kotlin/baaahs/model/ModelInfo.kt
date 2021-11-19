package baaahs.model

import baaahs.geom.Vector3F

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

    object EmptyModel : Model() {
        override val name: String get() = TODO("not implemented")
        override val allEntities: List<Entity> get() = TODO("not implemented")
        override val center: Vector3F get() = Empty.center
        override val extents: Vector3F get() = Empty.extents
    }

}
