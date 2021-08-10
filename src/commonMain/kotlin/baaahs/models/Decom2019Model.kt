package baaahs.models

import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.model.LightBar
import baaahs.model.ObjModel

class Decom2019Model : ObjModel("decom-2019-panels.obj") {
    override val name: String = "Decom2019"

    override val allEntities: List<Entity>
        get() = super.allEntities + lightBars

    val lightBars: List<LightBar> = listOf(
        // Vertical between Panel 1 and 2:
        lightBar("bar 1", Vector3F(54f, 66f, 0f), Vector3F(54f, 102f, 0f)),

        // Vertical between Panel 2 and 3:
        lightBar("bar 2", Vector3F(114f, 66f, 0f), Vector3F(114f, 102f, 0f)),

        // Horizontal below Panel 2:
        lightBar("bar 3", Vector3F(66f, 47f, 0f), Vector3F(102f, 47f, 0f)),
    )

    fun lightBar(name: String, startVertex: Vector3F, endVertex: Vector3F) =
        LightBar(name, name, startVertex, endVertex)

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>) =
        Surface(name, name, PixelArrayDevice, 16 * 60, faces, lines)
}