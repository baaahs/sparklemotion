package baaahs.gl.result

import baaahs.Color
import baaahs.describe
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class ColorResultTypeSpec : DescribeSpec({
    describe<ColorResultType> {
        context("gamma correction") {

        }
    }

    describe<ColorResultType.RealGammaCorrector> {
        val gammaCorrector by value { ColorResultType.RealGammaCorrector(2.2) }

        it("adjusts colors") {
            gammaCorrector.correct(Color.fromHex(0x4488CC))
                .shouldBe(Color.fromHex(0x0e409c))
            gammaCorrector.correct(Color.fromHex(0x112233))
                .shouldBe(Color.fromHex(0x010308))
            gammaCorrector.correct(Color.fromHex(0x88ccff))
                .shouldBe(Color.fromHex(0x409cff))
        }

        it("applies temporal dithering") {
            gammaCorrector.correct(Color.fromHex(0x202020))
                .shouldBe(Color.fromHex(0x030303))
            gammaCorrector.correct(Color.fromHex(0x202020))
                .shouldBe(Color.fromHex(0x030303))
            gammaCorrector.correct(Color.fromHex(0x202020))
                .shouldBe(Color.fromHex(0x020202))
            gammaCorrector.correct(Color.fromHex(0x202020))
                .shouldBe(Color.fromHex(0x020202))
            gammaCorrector.correct(Color.fromHex(0x202020))
                .shouldBe(Color.fromHex(0x020202))
            gammaCorrector.correct(Color.fromHex(0x202020))
                .shouldBe(Color.fromHex(0x030303))
        }
    }
})