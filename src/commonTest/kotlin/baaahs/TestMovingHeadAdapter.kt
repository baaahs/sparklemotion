package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter

class TestMovingHeadAdapter(
    override val dmxChannelCount: Int = 10,

    override val colorModel: MovingHead.ColorModel = MovingHead.ColorModel.RGB,
    override val colorWheelColors: List<Shenzarpy.WheelColor> = emptyList(),
    override val colorWheelMotorSpeed: Float = 1f,

    override val dimmerChannel: Dmx.Channel = TestChannel(5),

    override val panChannel: Dmx.Channel = TestChannel(1),
    override val panFineChannel: Dmx.Channel = TestChannel(2),
    override val panRange: ClosedRange<Float> = toRadians(0f)..toRadians(540f),
    override val panMotorSpeed: Float = 1f,

    override val tiltChannel: Dmx.Channel = TestChannel(3),
    override val tiltFineChannel: Dmx.Channel = TestChannel(4),
    override val tiltRange: ClosedRange<Float> = toRadians(-110f)..toRadians(110f),
    override val tiltMotorSpeed: Float = 1f,

    override val shutterChannel: Dmx.Channel = TestChannel(6)
) : MovingHeadAdapter {
    override fun newBuffer(dmxBuffer: Dmx.Buffer) = Buffer(dmxBuffer)

    inner class Buffer(override val dmxBuffer: Dmx.Buffer) : MovingHead.BaseBuffer(this) {
        override var colorWheelPosition: Float
            get() = colorWheel.toInt() / 128f
            set(value) { colorWheel = (value * 128f).toInt().toByte() }

        private val colorWheelChannel: Dmx.Channel get() = Shenzarpy.Channel.COLOR_WHEEL

        private var colorWheel: Byte
            get() = dmxBuffer[colorWheelChannel]
            set(value) { dmxBuffer[colorWheelChannel] = value }
    }

    class TestChannel(override val offset: Int) : Dmx.Channel
}