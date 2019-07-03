package baaahs

import baaahs.dmx.Shenzarpy
import kotlin.test.Test
import kotlin.test.assertTrue

class MovingHeadTest {
    val testMovingHead = TestMovingHead()

    @Test
    fun floatValues() {
        testMovingHead.pan = 0.75f
        testMovingHead.tilt = 1f
        assertTrue(0.75f - testMovingHead.pan < 0.001)
        assertTrue(1f - testMovingHead.tilt < 0.001)
    }

    class TestMovingHead : MovingHead.Buffer {
        override val buffer: Dmx.Buffer = Dmx.Buffer(ByteArray(10), 0, 10)
        override val panChannel: Dmx.Channel get() = TestChannel(1)
        override val panFineChannel: Dmx.Channel? get() = TestChannel(2)
        override val tiltChannel: Dmx.Channel get() = TestChannel(3)
        override val tiltFineChannel: Dmx.Channel? get() = TestChannel(4)
        override val dimmerChannel: Dmx.Channel get() = TestChannel(5)
        override var color: Color
            get() = Color.BLACK
            set(value) {}
        override val colorMode: MovingHead.ColorMode get() = MovingHead.ColorMode.ColorWheel
        override val colorWheelColors: List<Shenzarpy.WheelColor> get() = emptyList()
    }

    class TestChannel(override val offset: Int) : Dmx.Channel
}