package baaahs;

import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.sim.FakeDmxUniverse
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class DmxTest {
    @Test
    fun testShenzarpyPan() {
        val shenzarpy = Shenzarpy("x", "x", 0, Vector3F.origin, Vector3F.origin)
        val universe = FakeDmxUniverse()
        val buffer = shenzarpy.newBuffer(universe)

        val original = toRadians(30f)
        buffer.pan = original

        assertTrue {
            abs(original - buffer.pan) < 0.01
        }
    }
}
