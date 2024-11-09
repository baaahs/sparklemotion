package baaahs.imaging

import baaahs.describe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

object DimenSpec : DescribeSpec({
    describe<Dimen> {
        context("#bestFit") {
            context("when both are the same ratio") {
                it("returns a good dimen") {
                    Dimen(100, 50).bestFit(Dimen(100, 50))
                        .shouldBe(Dimen(100, 50))
                }

                it("returns a good dimen") {
                    Dimen(100, 50).bestFit(Dimen(50, 25))
                        .shouldBe(Dimen(100, 50))
                }
            }

            context("when receiver is wider than argument") {
                it("returns a good dimen") {
                    Dimen(100, 50).bestFit(Dimen(200, 50))
                        .shouldBe(Dimen(100, 25))
                }

                context("and overall is smaller") {
                    it("returns a good dimen") {
                        Dimen(100, 50).bestFit(Dimen(20, 5))
                            .shouldBe(Dimen(100, 25))
                    }
                }
            }

            context("when receiver is taller than argument") {
                it("returns a good dimen") {
                    Dimen(100, 50).bestFit(Dimen(100, 100))
                        .shouldBe(Dimen(50, 50))
                }

                context("and overall is smaller") {
                    it("returns a good dimen") {
                        Dimen(100, 50).bestFit(Dimen(50, 50))
                            .shouldBe(Dimen(50, 50))
                    }
                }
            }
        }
    }
})