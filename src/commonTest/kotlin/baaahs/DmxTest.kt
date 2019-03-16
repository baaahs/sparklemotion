package baaahs;

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

public class DmxTest {
    @Test
    fun testShenzarpyPan() {
        val channels = ByteArray(16)
        val shenzarpy = Shenzarpy(Dmx.Buffer(channels, 0, 16))

        val original = toRadians(30f)
        shenzarpy.pan = original

        assertTrue {
            abs(original - shenzarpy.pan) < 0.01
        }
    }
}
