package baaahs.dmx

import baaahs.Color
import baaahs.model.ModelUnit
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.toRadians
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable @SerialName("LixadaMiniMovingHead")
object LixadaMiniMovingHead : MovingHeadAdapter {
    override val id: String get() = "LixadaMiniMovingHead"
    override val dmxChannelCount: Int get() = 9 // TODO: ?

    override val colorModel: MovingHead.ColorModel get() = MovingHead.ColorModel.RGBW
    override val colorWheelColors: List<Shenzarpy.WheelColor> get() = emptyList()
    override val colorWheelMotorSpeed: Float = 1f

    override val dimmerChannel: Dmx.Channel get() = Channel.DIMMER
    override val shutterChannel: Dmx.Channel get() = TODO("not implemented")

    override val panChannel get() = Channel.PAN
    override val panFineChannel: Dmx.Channel? get() = null /*Channel.PAN_FINE*/
    override val panRange: ClosedRange<Float> = toRadians(0f)..toRadians(540f)
    override val panMotorSpeed: Float = 1.5f

    override val tiltChannel: Dmx.Channel get() = Channel.TILT
    override val tiltFineChannel: Dmx.Channel? get() = null /*Channel.TILT_FINE*/
    override val tiltRange: ClosedRange<Float> = toRadians(-110f)..toRadians(110f)
    override val tiltMotorSpeed: Float = 1f

    override val prismChannel get() = Channel.EMPTY
    override val prismRotationChannel get() = Channel.EMPTY

    override val visualizerInfo: MovingHeadAdapter.VisualizerInfo
        get() = MovingHeadAdapter.VisualizerInfo(
            canRadius = 1.5f.`in`,
            lensRadius = 1f.`in`,
            canLengthInFrontOfLight = 3f.`in`,
            canLengthBehindLight = 1f.`in`
        )

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
        COLOR_RESET,
        EMPTY;

        override val offset = ordinal
    }

    class Buffer(override val dmxBuffer: Dmx.Buffer) : MovingHead.BaseBuffer(LixadaMiniMovingHead) {
        init {
            dimmer = 134 * 256 / 65535f
            dmxBuffer[Channel.WHITE] = 255.toByte()
            dmxBuffer[Channel.RED] = 255.toByte()
            dmxBuffer[Channel.GREEN] = 255.toByte()
            dmxBuffer[Channel.BLUE] = 255.toByte()
        }

        var color: Color
            get() = Color.from(dmxBuffer[Channel.RED], dmxBuffer[Channel.GREEN], dmxBuffer[Channel.BLUE])
            set(value) {
                dmxBuffer[Channel.RED] = value.redI.toByte()
                dmxBuffer[Channel.GREEN] = value.greenI.toByte()
                dmxBuffer[Channel.BLUE] = value.blueI.toByte()
            }

        override var colorWheelPosition: Float get() = error("not supported")
            set(_) {}
    }

    private val Float.`in` get() = ModelUnit.Inches.toCm(this)
}