package baaahs;

import baaahs.dmx.Shenzarpy
import baaahs.sim.FakeDmxUniverse
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class DmxTest {
    @Test
    fun testShenzarpyPan() {
        val shenzarpy = Shenzarpy
        val universe = FakeDmxUniverse()
        val buffer = shenzarpy.newBuffer(universe, 0)

        val original = toRadians(30f)
        buffer.pan = original

        assertTrue {
            abs(original - buffer.pan) < 0.01
        }
    }
}
