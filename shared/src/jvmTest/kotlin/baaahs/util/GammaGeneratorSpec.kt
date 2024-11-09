package baaahs.util

import baaahs.describe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly

object GammaGeneratorSpec : DescribeSpec({
    describe<GammaGenerator> {
        describe("dithering") {
            it("should dither properly") {
                val gammaGenerator = GammaGenerator(ditherStateBits = 8)
                gammaGenerator.calculateDitherThresholds().toList().shouldContainExactly(
                    0b000, 0b100, 0b010, 0b110, 0b001, 0b101, 0b011, 0b111
                )
            }
        }
    }
})