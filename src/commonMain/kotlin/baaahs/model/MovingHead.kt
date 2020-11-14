package baaahs.model

import baaahs.Color
import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.fixtures.DeviceType
import baaahs.fixtures.MovingHeadDevice
import baaahs.geom.Vector3F
import kotlinx.serialization.Serializable

class MovingHead(
    override val name: String,
    override val description: String,
    val origin: Vector3F,
    val heading: Vector3F
) : Model.Entity {
    enum class ColorMode {
        ColorWheel,
        RGB,
        RGBW
    }

    interface Buffer {
        val buffer: Dmx.Buffer
        val panChannel: Dmx.Channel
        val panFineChannel: Dmx.Channel?
        val tiltChannel: Dmx.Channel
        val tiltFineChannel: Dmx.Channel?
        val dimmerChannel: Dmx.Channel

        val supportsFinePositioning: Boolean
            get() = panFineChannel != null && tiltFineChannel != null

        var pan: Float
            get() = getFloat(panChannel, panFineChannel)
            set(value) = setFloat(panChannel, panFineChannel, value)

        val panRange: ClosedRange<Float>

        var tilt: Float
            get() = getFloat(tiltChannel, tiltFineChannel)
            set(value) = setFloat(tiltChannel, tiltFineChannel, value)

        val tiltRange: ClosedRange<Float>

        var dimmer: Float
            get() = getFloat(dimmerChannel)
            set(value) = setFloat(dimmerChannel, value)

        var color: Color
        val colorMode: ColorMode
        val colorWheelColors: List<Shenzarpy.WheelColor>

        fun closestColorFor(color: Color): Byte {
            var bestMatch = Shenzarpy.WheelColor.WHITE
            var bestDistance = 1f

            colorWheelColors.forEach { wheelColor ->
                val distance = wheelColor.color.distanceTo(color)
                if (distance < bestDistance) {
                    bestMatch = wheelColor
                    bestDistance = distance
                }
            }

            return bestMatch.ordinal.toByte()
        }

        private fun getFloat(channel: Dmx.Channel): Float {
            val byteVal = buffer[channel].toInt() and 0xff
            return ((byteVal shl 8) + byteVal) / 65535f
        }

        private fun getFloat(coarseChannel: Dmx.Channel, fineChannel: Dmx.Channel?): Float {
            if (fineChannel == null) {
                return getFloat(coarseChannel)
            }

            val firstByte = buffer[coarseChannel].toInt() and 0xff
            val secondByte = buffer[fineChannel].toInt() and 0xff
            val scaled = firstByte * 256 + secondByte
            return scaled / 65535f
        }

        private fun setFloat(channel: Dmx.Channel, value: Float) {
            val scaled = (value * 65535).toInt()
            buffer[channel] = (scaled shr 8).toByte()
        }

        private fun setFloat(coarseChannel: Dmx.Channel, fineChannel: Dmx.Channel?, value: Float) {
            if (fineChannel == null) {
                return setFloat(coarseChannel, value)
            }

            val scaled = (value * 65535).toInt()
            buffer[coarseChannel] = (scaled shr 8).toByte()
            buffer[fineChannel] = (scaled and 0xff).toByte()
        }
    }

    @Serializable
    data class MovingHeadPosition(
        val x: Int,
        val y: Int
    )

    override val deviceType: DeviceType
        get() = MovingHeadDevice
}