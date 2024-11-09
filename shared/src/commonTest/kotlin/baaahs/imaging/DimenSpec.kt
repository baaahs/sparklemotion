package baaahs.imaging

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object DimenSpec : DescribeSpec({
    describe<Dimen> {
        context("#bestFit") {
            context("when both are the same ratio") {
                it("returns a good dimen") {
                    expect(Dimen(100, 50).bestFit(Dimen(100, 50)))
                        .toEqual(Dimen(100, 50))
                }

                it("returns a good dimen") {
                    expect(Dimen(100, 50).bestFit(Dimen(50, 25)))
                        .toEqual(Dimen(100, 50))
                }
            }

            context("when receiver is wider than argument") {
                it("returns a good dimen") {
                    expect(Dimen(100, 50).bestFit(Dimen(200, 50)))
                        .toEqual(Dimen(100, 25))
                }

                context("and overall is smaller") {
                    it("returns a good dimen") {
                        expect(Dimen(100, 50).bestFit(Dimen(20, 5)))
                            .toEqual(Dimen(100, 25))
                    }
                }
            }

            context("when receiver is taller than argument") {
                it("returns a good dimen") {
                    expect(Dimen(100, 50).bestFit(Dimen(100, 100)))
                        .toEqual(Dimen(50, 50))
                }

                context("and overall is smaller") {
                    it("returns a good dimen") {
                        expect(Dimen(100, 50).bestFit(Dimen(50, 50)))
                            .toEqual(Dimen(50, 50))
                    }
                }
            }
        }
    }
})