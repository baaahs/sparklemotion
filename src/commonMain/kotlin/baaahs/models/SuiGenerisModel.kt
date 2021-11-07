package baaahs.models

import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.model.ObjModelLoader

class SuiGenerisModel : Model() {
    override val name: String = "Decom2019" // todo: it's weird that we're reusing Decom2019 here.

    private val objModel = ObjModelLoader("sui-generis.obj") { name, faces, lines ->
        Surface(name, name, PixelArrayDevice, 10 * 60, faces, lines)
    }

    override val allEntities: List<Entity>
        get() = objModel.allEntities
    override val geomVertices: List<Vector3F>
        get() = objModel.geomVertices
}