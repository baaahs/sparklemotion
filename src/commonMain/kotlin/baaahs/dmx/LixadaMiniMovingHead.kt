package baaahs.dmx

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.model.MovingHead
import baaahs.toRadians

class LixadaMiniMovingHead(
    name: String,
    description: String,
    baseDmxChannel: Int,
    origin: Vector3F,
    heading: Vector3F
) : MovingHead(name, description, baseDmxChannel, origin, heading) {
    override val dmxChannelCount: Int get() = 9 // TODO: ?

    override val colorModel: ColorModel get() = ColorModel.RGBW
    override val colorWheelColors: List<Shenzarpy.WheelColor> get() = emptyList()
    override val colorWheelMotorSpeed: Float = 1f

    override val dimmerChannel: Dmx.Channel get() = Channel.DIMMER

    override val panChannel get() = Channel.PAN
    override val panFineChannel: Dmx.Channel? get() = null /*Channel.PAN_FINE*/
    override val panRange: ClosedRange<Float> = toRadians(0f)..toRadians(540f)
    override val panMotorSpeed: Float = 1.5f

    override val tiltChannel: Dmx.Channel get() = Channel.TILT
    override val tiltFineChannel: Dmx.Channel? get() = null /*Channel.TILT_FINE*/
    override val tiltRange: ClosedRange<Float> = toRadians(-110f)..toRadians(110f)
    override val tiltMotorSpeed: Float = 1f

    override fun newBuffer(dmxBuffer: Dmx.Buffer) = Buffer(dmxBuffer)

    enum class Channel: Dmx.Channel {
        PAN,
//        PAN_FINE,
        TILT,
//        TILT_FINE,
        DIMMER,
        RED,
        GREEN,
        BLUE,
        WHITE,
        PAN_TILT_SPEED,
//        COLOR_11,
//        COLOR_12,
//        COLOR_CONTROL,
        COLOR_RESET;

        override val offset = ordinal
    }

    inner class Buffer(override val dmxBuffer: Dmx.Buffer) : BaseBuffer(this) {
        init {
            dimmer = 134 * 256 / 65535f
            dmxBuffer[Channel.WHITE] = 255.toByte()
            dmxBuffer[Channel.RED] = 255.toByte()
            dmxBuffer[Channel.GREEN] = 255.toByte()
            dmxBuffer[Channel.BLUE] = 255.toByte()
        }

        var color: Color
            get() = Color(dmxBuffer[Channel.RED], dmxBuffer[Channel.GREEN], dmxBuffer[Channel.BLUE])
            set(value) {
                dmxBuffer[Channel.RED] = value.redI.toByte()
                dmxBuffer[Channel.GREEN] = value.greenI.toByte()
                dmxBuffer[Channel.BLUE] = value.blueI.toByte()
            }

        override var colorWheelPosition: Float get() = error("not supported")
            set(_) = TODO("not implemented")
    }
}