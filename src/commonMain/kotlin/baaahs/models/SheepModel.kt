package baaahs.models

import baaahs.device.PixelArrayDevice
import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.MovingHead
import baaahs.model.ObjModel
import kotlin.math.PI

class SheepModel : ObjModel("baaahs-model.obj") {
    override val name: String = "BAAAHS"
    private val pixelsPerPanel = hashMapOf<String, Int>()

    private val wallEyedness = (0.1f * PI / 2).toFloat()

    private val movingHeads: List<MovingHead> = arrayListOf(
        MovingHead(
            "leftEye",
            "Left Eye",
            1,
            Shenzarpy,
            origin = Vector3F(-11f, 202.361f, -24.5f),
            heading = Vector3F(0f, wallEyedness, (PI / 2).toFloat())
        ),
        MovingHead(
            "rightEye",
            "Right Eye",
            17,
            Shenzarpy,
            origin = Vector3F(-11f, 202.361f, 27.5f),
            heading = Vector3F(0f, -wallEyedness, (PI / 2).toFloat())
        )
    )

    override val allEntities: List<Entity>
        get() = super.allEntities + movingHeads

    override fun load() {
        getResource("baaahs-panel-info.txt")
            .split("\n")
            .map { it.split(Regex("\\s+")) }
            .forEach { pixelsPerPanel[it[0]] = it[1].toInt() * 60 }

        super.load()
    }

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface {
        val expectedPixelCount = pixelsPerPanel[name]
        if (expectedPixelCount == null) {
            logger.debug { "No pixel count found for $name" }
        }

        return Surface(
            name, "Panel $name", PixelArrayDevice, expectedPixelCount,
            faces,
            lines
        )
    }

    companion object {
        fun Panel(name: String) =
            Surface(name, name, PixelArrayDevice, null, emptyList(), emptyList())
    }
}