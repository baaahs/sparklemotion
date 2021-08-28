package baaahs.models

import baaahs.controller.SacnManager
import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.SacnTransportConfig
import baaahs.model.LightRing
import baaahs.model.LightRing.Companion.facingForward
import baaahs.model.Model
import baaahs.model.PolyLine
import kotlin.math.PI

class Playa2021Model : Model() {
    override val name: String = "Playa2021"

    val controllerId = ControllerId(
        SacnManager.controllerTypeName,
        "sacn-main"
    )
    val pixelFormat = PixelArrayDevice.PixelFormat.GRB8 // ... could be RGB8 or GRB8.

    private val smallGridBoard = generatePolyLine("grid",
        7, 11, 0f, 0f, 24f, 36f)

    private val allGridBoards = listOf(smallGridBoard)

    private fun generatePolyLine(
        name: String,
        xPixels: Int, yPixels: Int, left: Float, bottom: Float, width: Float, height: Float
    ): PolyLine {
        return PolyLine(name, name, (0 until yPixels).map { yI ->
            val yOff = height * yI.toFloat() / yPixels
            PolyLine.Segment(
                Vector3F(left, bottom + yOff, 0f),
                Vector3F(left + width, bottom + yOff, 0f),
                xPixels
            ).let {
                if (yI % 2 == 1) it.reverse() else it
            }
        })
    }

    override val allEntities: List<Entity> = allGridBoards

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
