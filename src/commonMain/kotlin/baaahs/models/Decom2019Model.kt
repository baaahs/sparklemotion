package baaahs.models

import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.model.ObjModelLoader

class Decom2019Model : Model() {
    override val name: String = "Decom2019"

    private val objModel = ObjModelLoader("decom-2019-panels.obj") { name, faces, lines ->
        Surface(name, name, PixelArrayDevice, 16 * 60, faces, lines)
    }

    private val lightBars: List<LightBar> = listOf(
        // Vertical between Panel 1 and 2:
        lightBar("bar 1", Vector3F(54f, 66f, 0f), Vector3F(54f, 102f, 0f)),

        // Vertical between Panel 2 and 3:
        lightBar("bar 2", Vector3F(114f, 66f, 0f), Vector3F(114f, 102f, 0f)),

        // Horizontal below Panel 2:
        lightBar("bar 3", Vector3F(66f, 47f, 0f), Vector3F(102f, 47f, 0f)),
    )

    override val allEntities: List<Entity>
        get() = objModel.allEntities + lightBars
    override val geomVertices: List<Vector3F>
        get() = objModel.geomVertices

    fun lightBar(name: String, startVertex: Vector3F, endVertex: Vector3F) =
        LightBar(name, name, startVertex, endVertex)

}