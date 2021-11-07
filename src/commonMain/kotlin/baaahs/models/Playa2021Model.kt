package baaahs.models

import baaahs.controller.SacnManager
import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.SacnTransportConfig
import baaahs.model.Model
import baaahs.model.ObjModelLoader
import baaahs.model.PolyLine

class Playa2021Model : Model() {
    override val name: String = "Playa2021"

    private val objModel = ObjModelLoader("playa-2021-panels.obj") { name, faces, lines ->
        Surface(name, name, PixelArrayDevice, 16 * 60, faces, lines)
    }

    val controllerId = ControllerId(
        SacnManager.controllerTypeName,
        "sacn-main"
    )
    val pixelFormat = PixelArrayDevice.PixelFormat.GRB8 // ... could be RGB8 or GRB8.

    private val smallGridBoard = generatePolyLine("grid",
        7, 11, -24f, 0f, 24f, 36f)

    private val allGridBoards = listOf(smallGridBoard)

    private fun generatePolyLine(
        name: String,
        rows: Int, cols: Int, left: Float, bottom: Float, width: Float, height: Float
    ): PolyLine {
        return PolyLine(name, name, (0 until cols).map { yI ->
            val yOff = height * yI.toFloat() / cols
            PolyLine.Segment(
                Vector3F(left, bottom + yOff, 0f),
                Vector3F(left + width, bottom + yOff, 0f),
                rows
            ).let {
                if (yI % 2 == 1) it.reverse() else it
            }
        })
    }

    override val allEntities: List<Entity>
        get() = objModel.allEntities + allGridBoards
    override val geomVertices: List<Vector3F>
        get() = objModel.geomVertices

    override fun generateFixtureMappings(): Map<ControllerId, List<FixtureMapping>> {
        return mapOf(
            controllerId to
                    allGridBoards.mapIndexed { index: Int, entity: PolyLine ->
                        val startChannel = 0
                        val pixelCount = entity.pixelCount
                        val endChannel = startChannel + pixelCount * pixelFormat.channelsPerPixel

                        FixtureMapping(
                            entity,
                            pixelCount,
                            null,
                            PixelArrayDevice.Config(
                                pixelCount,
                                pixelFormat,
                                pixelArrangement = LinearSurfacePixelStrategy()
                            ),
                            SacnTransportConfig(startChannel, endChannel)
                        )
                    }
        )
    }
}
