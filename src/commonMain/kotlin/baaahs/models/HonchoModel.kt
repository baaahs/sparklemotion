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
import kotlin.math.PI

class HonchoModel : Model() {
    override val name: String = "Honcho"

    val controllerId = ControllerId(
        SacnManager.controllerTypeName,
        "sacn-main"
    )
    val pixelFormat = PixelArrayDevice.PixelFormat.GRB8 // ... could be RGB8 or GRB8.

    val lightRings = listOf(
//        12x four meter circumference (240 pixels)
        LightRingConfig("ring 4.01", 1.m * 2, 1.m, 4.m, 240, 1),
        LightRingConfig("ring 4.02", 2.m * 2, 1.m, 4.m, 240, 3),
        LightRingConfig("ring 4.03", 3.m * 2, 1.m, 4.m, 240, 5),
        LightRingConfig("ring 4.04", 4.m * 2, 1.m, 4.m, 240, 7),
        LightRingConfig("ring 4.05", 5.m * 2, 1.m, 4.m, 240, 9),
        LightRingConfig("ring 4.06", 6.m * 2, 1.m, 4.m, 240, 11),
        LightRingConfig("ring 4.07", 7.m * 2, 3.m, 4.m, 240, 13),
        LightRingConfig("ring 4.08", 8.m * 2, 1.m, 4.m, 240, 15),
        LightRingConfig("ring 4.09", 9.m * 2, 1.m, 4.m, 240, 17),
        LightRingConfig("ring 4.10", 10.m * 2, 1.m, 4.m, 240, 19),
        LightRingConfig("ring 4.11", 11.m * 2, 1.m, 4.m, 240, 21),
        LightRingConfig("ring 4.12", 12.m * 2, 1.m, 4.m, 240, 23),

//        12x five meter circumference (300 pixels)
        LightRingConfig("ring 5.01", 1.m * 2, 3.m, 5.m, 300, 25),
        LightRingConfig("ring 5.02", 2.m * 2, 3.m, 5.m, 300, 27),
        LightRingConfig("ring 5.03", 3.m * 2, 3.m, 5.m, 300, 29),
        LightRingConfig("ring 5.04", 4.m * 2, 3.m, 5.m, 300, 31),
        LightRingConfig("ring 5.05", 5.m * 2, 3.m, 5.m, 300, 33),
        LightRingConfig("ring 5.06", 6.m * 2, 3.m, 5.m, 300, 35),
        LightRingConfig("ring 5.07", 7.m * 2, 3.m, 5.m, 300, 37),
        LightRingConfig("ring 5.08", 8.m * 2, 3.m, 5.m, 300, 39),
        LightRingConfig("ring 5.09", 9.m * 2, 3.m, 5.m, 300, 41),
        LightRingConfig("ring 5.10", 10.m * 2, 3.m, 5.m, 300, 43),
        LightRingConfig("ring 5.11", 11.m * 2, 3.m, 5.m, 300, 45),
        LightRingConfig("ring 5.12", 12.m * 2, 3.m, 5.m, 300, 47),

//        1x eight meter circumference (480 pixels)
        LightRingConfig("ring 8.01", 7.m * 2, 3.m, 8.m, 480, 49),
    )

    override val allEntities: List<Entity> = lightRings.map { it.entity }

    override fun generateFixtureMappings(): Map<ControllerId, List<FixtureMapping>> {
        return mapOf(
            controllerId to
                    lightRings.map { config ->
                        val startChannel = (config.startingUniverse - 1) * 512
                        val endChannel = startChannel + config.pixelCount * pixelFormat.channelsPerPixel

                        FixtureMapping(
                            config.entity,
                            config.pixelCount,
                            null,
                            PixelArrayDevice.Config(
                                config.pixelCount,
                                pixelFormat,
                                pixelArrangement = LinearSurfacePixelStrategy()
                            ),
                            SacnTransportConfig(startChannel, endChannel)
                        )
                    }
        )
    }

    data class LightRingConfig(
        val name: String,
        val centerX: Float,
        val centerY: Float,
        val circumference: Float,
        val pixelCount: Int,
        val startingUniverse: Int,
        val orientation: Vector3F = facingForward
    ) {
        val entity =
            LightRing(
                name, name,
                Vector3F(centerX - 7.m, centerY, 0f),
                (circumference / PI).toFloat(),
                facingForward
            )
    }

    companion object {
        private val Number.m: Float get() = toFloat() * 100f / 2.54f
    }
}
