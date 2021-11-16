package baaahs.models

import baaahs.dmx.Shenzarpy
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.ObjModelLoader
import baaahs.util.Logger
import kotlin.math.PI

class SheepModel : Model() {
    override val name: String = "BAAAHS"
    private val pixelsPerPanel = run {
        getResource("baaahs-panel-info.txt")
            .split("\n")
            .map { it.split(Regex("\\s+")) }
            .associate { it[0] to it[1].toInt() * 60 }
    }

    private val objModel = ObjModelLoader.load("baaahs-model.obj") { name ->
        val expectedPixelCount = pixelsPerPanel[name]
        if (expectedPixelCount == null) {
            logger.debug { "No pixel count found for $name" }
        }
        expectedPixelCount
    }
    private val wallEyedness = 0.1f * PI / 2

    private val movingHeads: List<MovingHead> = arrayListOf(
        MovingHead(
            "leftEye",
            "Left Eye",
            1,
            Shenzarpy,
            position = Vector3F(-11f, 202.361f, -24.5f),
            rotation = EulerAngle(0.0, wallEyedness, PI / 2)
        ),
        MovingHead(
            "rightEye",
            "Right Eye",
            17,
            Shenzarpy,
            position = Vector3F(-11f, 202.361f, 27.5f),
            rotation = EulerAngle(0.0, -wallEyedness, PI / 2)
        )
    )

    override val allEntities: List<Entity>
        get() = objModel.allEntities + movingHeads

    companion object {
        private val logger = Logger<SheepModel>()

        fun Panel(name: String) =
            Surface(name, name, null, emptyList(), emptyList(), Geometry(emptyList()))
    }
}