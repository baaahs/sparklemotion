package baaahs.models

import baaahs.fixtures.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.getResource
import baaahs.model.MovingHead
import baaahs.model.ObjModel

class SheepModel : ObjModel("baaahs-model.obj") {
    override val name: String = "BAAAHS"
    private val pixelsPerPanel = hashMapOf<String, Int>()

    override val movingHeads: List<MovingHead> = arrayListOf(
            MovingHead(
                "leftEye",
                "Left Eye",
                Vector3F(0f, 204.361f, 48.738f)
            ),
            MovingHead(
                "rightEye",
                "Right Eye",
                Vector3F(0f, 204.361f, -153.738f)
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
        return Surface(name, "Panel $name", PixelArrayDevice, expectedPixelCount, faces, lines)
    }

    companion object {
        fun Panel(name: String) = Surface(name, name, PixelArrayDevice, null, emptyList(), emptyList())
    }
}