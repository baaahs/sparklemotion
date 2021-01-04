package baaahs.model

import baaahs.Color
import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.fixtures.DeviceType
import baaahs.fixtures.MovingHeadDevice
import baaahs.geom.Vector3F
import kotlinx.serialization.Serializable

abstract class MovingHead(
    override val name: String,
    override val description: String,
    val baseDmxChannel: Int,
    override val origin: Vector3F,
    override val heading: Vector3F
) : Model.Entity, Model.FixtureInfo {
    abstract val dmxChannelCount: Int

    abstract val colorModel: ColorModel
    abstract val colorWheelColors: List<Shenzarpy.WheelColor>
    /** Seconds required to rotate through full color wheel range. */
    abstract val colorWheelMotorSpeed: Float

    abstract val dimmerChannel: Dmx.Channel
    abstract val shutterChannel: Dmx.Channel

    abstract val panChannel: Dmx.Channel
    abstract val panFineChannel: Dmx.Channel?
    /** In radians. */
    abstract val panRange: ClosedRange<Float>
    /** Seconds required to rotate through full pan range. */
    abstract val panMotorSpeed: Float

    abstract val tiltChannel: Dmx.Channel
    abstract val tiltFineChannel: Dmx.Channel?
    /** In radians. */
    abstract val tiltRange: ClosedRange<Float>
    /** Seconds required to rotate through full tilt range. */
    abstract val tiltMotorSpeed: Float

    enum class ColorModel {
        ColorWheel,
        RGB,
        RGBW
    }

    abstract class BaseBuffer(private val movingHead: MovingHead) : Buffer {
        override var pan: Float
            get() = getFloat(movingHead.panChannel, movingHead.panFineChannel)
            set(value) = setFloat(movingHead.panChannel, movingHead.panFineChannel, value)

        override var tilt: Float
            get() = getFloat(movingHead.tiltChannel, movingHead.tiltFineChannel)
            set(value) = setFloat(movingHead.tiltChannel, movingHead.tiltFineChannel, value)

        override var dimmer: Float
            get() = getFloat(movingHead.dimmerChannel)
            set(value) = setFloat(movingHead.dimmerChannel, value)
        override var shutter: Int
            get() = dmxBuffer[movingHead.shutterChannel].toInt()
            set(value) { dmxBuffer[movingHead.shutterChannel] = value.toByte() }
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

    fun newBuffer(universe: Dmx.Universe): Buffer {
        return newBuffer(universe.writer(baseDmxChannel, dmxChannelCount))
    }

    abstract fun newBuffer(dmxBuffer: Dmx.Buffer): Buffer

    @Serializable
    data class MovingHeadPosition(
        val x: Int,
        val y: Int
    )

    override val deviceType: DeviceType
        get() = MovingHeadDevice
}