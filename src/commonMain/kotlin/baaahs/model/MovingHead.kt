package baaahs.model

import baaahs.Color
import baaahs.clamp
import baaahs.device.FixtureType
import baaahs.device.MovingHeadDevice
import baaahs.dmx.Boryli
import baaahs.dmx.Dmx
import baaahs.dmx.LixadaMiniMovingHead
import baaahs.dmx.Shenzarpy
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.scale
import baaahs.sim.FixtureSimulation
import baaahs.sim.simulations
import baaahs.unscale
import baaahs.visualizer.EntityAdapter
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

interface MovingHeadAdapter {
    val id: String
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

    val prismChannel: Dmx.Channel
    val prismRotationChannel: Dmx.Channel

    val visualizerInfo: VisualizerInfo

    fun newBuffer(dmxBuffer: Dmx.Buffer): MovingHead.Buffer

    fun newBuffer(universe: Dmx.Universe, baseDmxChannel: Int): MovingHead.Buffer {
        return newBuffer(universe.writer(baseDmxChannel, dmxChannelCount))
    }

    companion object {
        // TODO: Move this to plugins.
        val all: List<MovingHeadAdapter> = listOf(Shenzarpy, Boryli, LixadaMiniMovingHead)
        val map = all.associateBy { it.id }
    }

    /** All dimensions in centimeters. */
    data class VisualizerInfo(
        val canRadius: Float,
        val lensRadius: Float,
        val canLengthInFrontOfLight: Float,
        val canLengthBehindLight: Float,
    ) {
        val canLength: Float get() = canLengthBehindLight + canLengthInFrontOfLight
    }
}

class MovingHead(
    override val name: String,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val baseDmxChannel: Int,
    val adapter: MovingHeadAdapter,
    @Transient override val id: EntityId = Model.Entity.nextId(),
) : Model.BaseEntity() {
    override val bounds: Pair<Vector3F, Vector3F>
        get() = transformation.position.let { it to it }

    enum class ColorModel {
        ColorWheel,
        RGB,
        RGBW
    }

    abstract class BaseBuffer(private val adapter: MovingHeadAdapter) : Buffer {
        override var pan: Float
            get() = with(adapter) {
                panRange.scale(getFloat(panChannel, panFineChannel))
            }
            set(value) = with(adapter) {
                setFloat(panChannel, panFineChannel, panRange.unscale(panRange.clamp(value)))
            }

        override var tilt: Float
            get() = with(adapter) {
                tiltRange.scale(getFloat(tiltChannel, tiltFineChannel))
            }
            set(value) = with(adapter) {
                setFloat(tiltChannel, tiltFineChannel, tiltRange.unscale(tiltRange.clamp(value)))
            }

        override var dimmer: Float
            get() = getFloat(adapter.dimmerChannel)
            set(value) = setFloat(adapter.dimmerChannel, (0f..1f).clamp(value))

        override var shutter: Int
            get() = dmxBuffer[adapter.shutterChannel].toInt()
            set(value) { dmxBuffer[adapter.shutterChannel] = value.toByte() }

        override var prism: Boolean
            get() = getFloat(adapter.prismChannel) >= 0.5
            set(value) = setFloat(adapter.prismChannel, (if (value) {
                1f
            } else {
                0f
            })
            )

        //  We only handle the [128,255] range, which goes from fast reverse to fast forward, stopped at [191,192].
        override var prismRotation: Float
            get() {
                val current = dmxBuffer[adapter.prismRotationChannel].toUByte();
                if (current < 128u) {
                    return 0f
                }
                return (dmxBuffer[adapter.prismRotationChannel].toFloat() - 191) / 62
            }
            set(value) {
                dmxBuffer[adapter.prismRotationChannel] = (191 + (-1f..1f).clamp(value) * 62).toUInt().toByte();
            }
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
        var prism: Boolean
        var prismRotation: Float

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

    override val fixtureType: FixtureType
        get() = MovingHeadDevice

    override fun createFixtureSimulation(adapter: EntityAdapter): FixtureSimulation =
        simulations.forMovingHead(this, adapter)

    override fun createVisualizer(adapter: EntityAdapter) =
        adapter.createMovingHeadVisualizer(this)
}