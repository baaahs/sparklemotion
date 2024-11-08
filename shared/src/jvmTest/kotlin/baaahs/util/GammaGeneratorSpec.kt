package baaahs.util

import baaahs.describe
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object GammaGeneratorSpec : DescribeSpec({
    describe<GammaGenerator> {
        describe("dithering") {
            it("should dither properly") {
                val gammaGenerator = GammaGenerator(ditherStateBits = 8)
                expect(gammaGenerator.calculateDitherThresholds().toList()).containsExactly(
                    0b000, 0b100, 0b010, 0b110, 0b001, 0b101, 0b011, 0b111
                )
            }
        }
    }
})