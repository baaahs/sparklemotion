package baaahs;

import baaahs.dmx.Shenzarpy
import baaahs.sim.FakeDmxUniverse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.floats.shouldBeLessThan
import kotlin.math.abs

object DmxTests : DescribeSpec({
    describe("Shenzarpy pan") {
        it("should be settable") {
            val shenzarpy = Shenzarpy
            val universe = FakeDmxUniverse()
            val buffer = shenzarpy.newBuffer(universe, 0)

            val original = toRadians(30f)
            buffer.pan = original

            abs(original - buffer.pan) shouldBeLessThan 0.01f
        }
    }
})