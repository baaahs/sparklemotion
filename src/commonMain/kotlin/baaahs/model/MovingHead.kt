package baaahs.model

import baaahs.Color
import baaahs.device.DeviceType
import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.fixtures.MovingHeadDevice
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.sim.FixtureSimulation
import baaahs.sim.MovingHeadSimulation
import baaahs.sim.SimulationEnv
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.visualizerBuilder
import kotlinx.serialization.Serializable

interface MovingHeadAdapter {
    val dmxChannelCount: Int

    val colorModel: MovingHead.ColorModel
    val colorWheelColors: List<Shenzarpy.WheelColor>
    /** Seconds required to rotate through full color wheel range. */
    val colorWheelMotorSpeed: Float

    val dimmerChannel: Dmx.Channel
    val shutterChannel: Dmx.Channel

    val panChannel: Dmx.Channel
    val panFineChannel: Dmx.Channel?
    /** In radians. */
    val panRange: ClosedRange<Float>
    /** Seconds required to rotate through full pan range. */
    val panMotorSpeed: Float

    val tiltChannel: Dmx.Channel
    val tiltFineChannel: Dmx.Channel?
    /** In radians. */
    val tiltRange: ClosedRange<Float>
    /** Seconds required to rotate through full tilt range. */
    val tiltMotorSpeed: Float

    fun newBuffer(dmxBuffer: Dmx.Buffer): MovingHead.Buffer

    fun newBuffer(universe: Dmx.Universe, baseDmxChannel: Int): MovingHead.Buffer {
        return newBuffer(universe.writer(baseDmxChannel, dmxChannelCount))
    }
}

class MovingHead(
    override val name: String,
    override val description: String?,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val baseDmxChannel: Int,
    val adapter: MovingHeadAdapter
) : Model.BaseEntity(), Model.FixtureInfo {
    override val bounds: Pair<Vector3F, Vector3F>
        get() = transformation.position.let { it to it }

    enum class ColorModel {
        ColorWheel,
        RGB,
        RGBW
    }

    abstract class BaseBuffer(private val adapter: MovingHeadAdapter) : Buffer {
        override var pan: Float
            get() = getFloat(adapter.panChannel, adapter.panFineChannel)
            set(value) = setFloat(adapter.panChannel, adapter.panFineChannel, value)

        override var tilt: Float
            get() = getFloat(adapter.tiltChannel, adapter.tiltFineChannel)
            set(value) = setFloat(adapter.tiltChannel, adapter.tiltFineChannel, value)

        override var dimmer: Float
            get() = getFloat(adapter.dimmerChannel)
            set(value) = setFloat(adapter.dimmerChannel, value)
        override var shutter: Int
            get() = dmxBuffer[adapter.shutterChannel].toInt()
            set(value) { dmxBuffer[adapter.shutterChannel] = value.toByte() }
    }

    interface Buffer {
        val dmxBuffer: Dmx.Buffer

        /** In radians. */
        var pan: Float
        /** In radians. */
        var tilt: Float
        /** `0` is completely dimmed, `1` is completely open. */
        var dimmer: Float
        /** Rotation of color wheel in `(0..1]`. */
        var colorWheelPosition: Float
        var shutter: Int

        fun List<Shenzarpy.WheelColor>.closestColorFor(color: Color): Byte {
            var bestMatch = Shenzarpy.WheelColor.WHITE
            var bestDistance = 1f

            forEach { wheelColor ->
                val distance = wheelColor.color.distanceTo(color)
                if (distance < bestDistance) {
                    bestMatch = wheelColor
                    bestDistance = distance
                }
            }

            return bestMatch.ordinal.toByte()
        }

        fun getFloat(channel: Dmx.Channel): Float {
            val byteVal = dmxBuffer[channel].toInt() and 0xff
            return ((byteVal shl 8) + byteVal) / 65535f
        }

        fun getFloat(coarseChannel: Dmx.Channel, fineChannel: Dmx.Channel?): Float {
            if (fineChannel == null) {
                return getFloat(coarseChannel)
            }

            val firstByte = dmxBuffer[coarseChannel].toInt() and 0xff
            val secondByte = dmxBuffer[fineChannel].toInt() and 0xff
            val scaled = firstByte * 256 + secondByte
            return scaled / 65535f
        }

        fun setFloat(channel: Dmx.Channel, value: Float) {
            val scaled = (value * 65535).toInt()
            dmxBuffer[channel] = (scaled shr 8).toByte()
        }

        fun setFloat(coarseChannel: Dmx.Channel, fineChannel: Dmx.Channel?, value: Float) {
            if (fineChannel == null) {
                return setFloat(coarseChannel, value)
            }

            val scaled = (value * 65535).toInt()
            dmxBuffer[coarseChannel] = (scaled shr 8).toByte()
            dmxBuffer[fineChannel] = (scaled and 0xff).toByte()
        }
    }

    @Serializable
    data class MovingHeadPosition(
        val x: Int,
        val y: Int
    )

    override val deviceType: DeviceType
        get() = MovingHeadDevice

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        MovingHeadSimulation(this, simulationEnv)

    override fun createVisualizer(simulationEnv: SimulationEnv): EntityVisualizer<*> =
        visualizerBuilder.createMovingHeadVisualizer(this, simulationEnv)
}