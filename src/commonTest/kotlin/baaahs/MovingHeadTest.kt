package baaahs

import baaahs.dmx.Dmx
import kotlin.test.Test
import kotlin.test.assertTrue

class MovingHeadTest {
    val testMovingHead = TestMovingHead()
    val buffer = testMovingHead.newBuffer(Dmx.Buffer(ByteArray(16), 0, 16))

    @Test
    fun floatValues() {
        buffer.pan = 0.75f
        buffer.tilt = 1f
        assertTrue(0.75f - buffer.pan < 0.001)
        assertTrue(1f - buffer.tilt < 0.001)
    }
}