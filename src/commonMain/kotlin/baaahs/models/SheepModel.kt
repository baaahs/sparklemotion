package baaahs.models

import baaahs.device.PixelArrayDevice
import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.getResource
import baaahs.model.MovingHead
import baaahs.model.ObjModel
import kotlin.math.PI

class SheepModel : ObjModel("baaahs-model.obj") {
    override val name: String = "BAAAHS"
    private val pixelsPerPanel = hashMapOf<String, Int>()

    private val wallEyedness = (0.1f * PI / 2).toFloat()

    override val movingHeads: List<MovingHead> = arrayListOf(
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
        val offset = when (name) {
            "Panel 2" -> Vector3F(-12f, 0f, 0f)
            "Panel 3" -> Vector3F(-24f, 0f, 0f)
            else -> Vector3F.origin
        }
        return Surface(
            name, "Panel $name", PixelArrayDevice, expectedPixelCount,
            faces.map {
                Face(it.allVertices.map { it + offset }, it.vertexA, it.vertexB, it.vertexC)
            },
            lines.map {
                Line(it.vertices.map { it + offset })
            }
        )
    }

    companion object {
        fun Panel(name: String) = Surface(name, name, PixelArrayDevice, null, emptyList(), emptyList())
    }
}