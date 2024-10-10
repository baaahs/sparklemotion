package baaahs.model

import baaahs.geom.Vector3F

interface Importer {
    interface Results {
        val entities: List<Model.Entity>
        val vertices: List<Vector3F>
        val errors: List<Error>
    }

    data class Error(
        val message: String,
        val lineNumber: Int?
    )
}