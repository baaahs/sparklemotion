package baaahs.gl.result

import baaahs.Color
import baaahs.describe
import baaahs.kotest.value
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object ColorResultTypeSpec : DescribeSpec({
    describe<ColorResultType> {
        context("gamma correction") {

        }
    }

    describe<ColorResultType.RealGammaCorrector> {
        val gammaCorrector by value { ColorResultType.RealGammaCorrector(2.2) }

        it("adjusts colors") {
            expect(gammaCorrector.correct(Color.fromHex(0x4488CC)))
                .toBe(Color.fromHex(0x0e409c))
            expect(gammaCorrector.correct(Color.fromHex(0x112233)))
                .toBe(Color.fromHex(0x010308))
            expect(gammaCorrector.correct(Color.fromHex(0x88ccff)))
                .toBe(Color.fromHex(0x409cff))
        }

        it("applies temporal dithering") {
            expect(gammaCorrector.correct(Color.fromHex(0x202020)))
                .toBe(Color.fromHex(0x030303))
            expect(gammaCorrector.correct(Color.fromHex(0x202020)))
                .toBe(Color.fromHex(0x030303))
            expect(gammaCorrector.correct(Color.fromHex(0x202020)))
                .toBe(Color.fromHex(0x020202))
            expect(gammaCorrector.correct(Color.fromHex(0x202020)))
                .toBe(Color.fromHex(0x020202))
            expect(gammaCorrector.correct(Color.fromHex(0x202020)))
                .toBe(Color.fromHex(0x020202))
            expect(gammaCorrector.correct(Color.fromHex(0x202020)))
                .toBe(Color.fromHex(0x030303))
        }
    }
})