package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.model.MovingHead

class TestMovingHead(
    override val dmxChannelCount: Int = 10,

    override val colorModel: ColorModel = ColorModel.RGB,
    override val colorWheelColors: List<Shenzarpy.WheelColor> = emptyList(),
    override val colorWheelMotorSpeed: Float = 1f,

    override val dimmerChannel: Dmx.Channel = MovingHeadTest.TestChannel(5),

    override val panChannel: Dmx.Channel = MovingHeadTest.TestChannel(1),
    override val panFineChannel: Dmx.Channel = MovingHeadTest.TestChannel(2),
    override val panRange: ClosedRange<Float> = toRadians(0f)..toRadians(540f),
    override val panMotorSpeed: Float = 1f,

    override val tiltChannel: Dmx.Channel = MovingHeadTest.TestChannel(3),
    override val tiltFineChannel: Dmx.Channel = MovingHeadTest.TestChannel(4),
    override val tiltRange: ClosedRange<Float> = toRadians(-110f)..toRadians(110f),
    override val tiltMotorSpeed: Float = 1f
) : MovingHead("test", "Test", 1, Vector3F.origin, Vector3F.origin) {
    override fun newBuffer(dmxBuffer: Dmx.Buffer) = Buffer(dmxBuffer)

    inner class Buffer(override val dmxBuffer: Dmx.Buffer) : BaseBuffer(this) {
        override var colorWheelPosition: Float
            get() = colorWheel.toInt() / 128f
            set(value) { colorWheel = (value * 128f).toInt().toByte() }

        private val colorWheelChannel: Dmx.Channel get() = Shenzarpy.Channel.COLOR_WHEEL

        private var colorWheel: Byte
            get() = dmxBuffer[colorWheelChannel]
            set(value) { dmxBuffer[colorWheelChannel] = value }
    }
}