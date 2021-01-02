package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.model.MovingHead

class TestMovingHead : MovingHead("test", "Test", 1, Vector3F.origin, Vector3F.origin) {
    override val dmxChannelCount: Int get() = 10

    override val colorModel: ColorModel get() = ColorModel.RGB
    override val colorWheelColors: List<Shenzarpy.WheelColor> get() = error("not supported")
    override val colorWheelMotorSpeed: Float
        get() = TODO("not implemented")

    override val dimmerChannel: Dmx.Channel get() = MovingHeadTest.TestChannel(5)

    override val panChannel: Dmx.Channel get() = MovingHeadTest.TestChannel(1)
    override val panFineChannel: Dmx.Channel get() = MovingHeadTest.TestChannel(2)
    override val panRange: ClosedRange<Float> = toRadians(0f)..toRadians(540f)
    override val panMotorSpeed: Float
        get() = TODO("not implemented")

    override val tiltChannel: Dmx.Channel get() = MovingHeadTest.TestChannel(3)
    override val tiltFineChannel: Dmx.Channel get() = MovingHeadTest.TestChannel(4)
    override val tiltRange: ClosedRange<Float> = toRadians(-110f)..toRadians(110f)
    override val tiltMotorSpeed: Float
        get() = TODO("not implemented")

    override fun newBuffer(dmxBuffer: Dmx.Buffer) = Buffer(dmxBuffer)

    inner class Buffer(override val dmxBuffer: Dmx.Buffer) : BaseBuffer(this) {
        override var colorWheelPosition: Float get() = TODO("not implemented")
            set(_) = TODO("not implemented")
    }
}