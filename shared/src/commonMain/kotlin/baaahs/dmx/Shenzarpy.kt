package baaahs.dmx

import baaahs.Color
import baaahs.model.ModelUnit
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.toRadians
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable @SerialName("Shenzarpy")
object Shenzarpy : MovingHeadAdapter {
    override val id: String get() = "Shenzarpy"
    override val dmxChannelCount: Int get() = 16

    override val colorModel: MovingHead.ColorModel get() = MovingHead.ColorModel.ColorWheel
    override val colorWheelColors: List<WheelColor> = WheelColor.values.toList()
    override val colorWheelMotorSpeed: Float = .3f
    private val colorWheelChannel: Dmx.Channel get() = Channel.COLOR_WHEEL

    override val dimmerChannel: Dmx.Channel get() = Channel.DIMMER
    override val shutterChannel: Dmx.Channel get() = Channel.SHUTTER

    override val panChannel get() = Channel.PAN
    override val panFineChannel: Dmx.Channel get() = Channel.PAN_FINE
    override val panRange: ClosedRange<Float> =
        toRadians(0f)..toRadians(540f)
    /** Time required to move from panRange.start to panRange.end, in seconds. */
    override val panMotorSpeed: Float = 2.54f

    override val tiltChannel: Dmx.Channel get() = Channel.TILT
    override val tiltFineChannel: Dmx.Channel get() = Channel.TILT_FINE
    override val tiltRange: ClosedRange<Float> =
        toRadians(-126f)..toRadians(126f)
    /** Time required to move from tiltRange.start to tiltRange.end, in seconds. */
    override val tiltMotorSpeed: Float = 1.3f

    override val prismChannel get() = Channel.PRISM
    override val prismRotationChannel get() = Channel.PRISM_ROTATION

    override val visualizerInfo: MovingHeadAdapter.VisualizerInfo
        get() = MovingHeadAdapter.VisualizerInfo(
            canRadius = 5f.`in`,
            lensRadius = 3f.`in`,
            canLengthInFrontOfLight = 9f.`in`,
            canLengthBehindLight = 3f.`in`
        )

    override fun newBuffer(dmxBuffer: Dmx.Buffer) = Buffer(dmxBuffer)

    class Buffer(override val dmxBuffer: Dmx.Buffer) : MovingHead.BaseBuffer(Shenzarpy) {
        override var colorWheelPosition: Float
            get() = colorWheel.toInt() / 128f
            set(value) { colorWheel = (value * 128f).toInt().toByte() }

        var colorWheel: Byte
            get() = dmxBuffer[colorWheelChannel]
            set(value) { dmxBuffer[colorWheelChannel] = value }

        init {
            dimmer = 1f
            shutter = 255
        }
    }

    enum class WheelColor(val color: Color) {
        WHITE(Color.from(0xffffff)),
        RED(Color.from(0xc21e22)),
        ORANGE(Color.from(0xeb8236)),
        AQUAMARINE(Color.from(0x7cbc84)),
        DEEP_GREEN(Color.from(0x12812f)),
        LIGHT_GREEN(Color.from(0x9fc13f)),
        LAVENDER(Color.from(0x8f74ab)),
        PINK(Color.from(0xeb8182)),
        YELLOW(Color.from(0xfeeb34)),
        MAGENTA(Color.from(0xe11382)),
        CYAN(Color.from(0x1ba7e8)),
        CTO2(Color.from(0xf4c651)),
        CTO1(Color.from(0xf4d88a)),
        CTB(Color.from(0x97c7b8)),
        DARK_BLUE(Color.from(0x085197));

        companion object {
            val values = values()
            fun get(i: Byte) = values[i.toInt()]
        }
    }

    enum class Channel : Dmx.Channel {
        COLOR_WHEEL,
        SHUTTER,
        DIMMER,
        GOBO_WHEEL,
        PRISM,
        PRISM_ROTATION,
        MACRO,
        FROST,
        FOCUS,
        PAN,
        PAN_FINE,
        TILT,
        TILT_FINE,
        PAN_TILT_SPEED,
        RESET,
        LAMP_CONTROL,
        BLANK,
        COLOR_WHEEL_SPEED,
        DIM_PRISM_ATOM_SPEED,
        GOBO_WHEEL_SPEED;


        companion object {
            val values = values()
            fun get(i: Byte) = values[i.toInt()]
        }

        override val offset = ordinal
    }

    private val Float.`in` get() = ModelUnit.Inches.toCm(this)
}