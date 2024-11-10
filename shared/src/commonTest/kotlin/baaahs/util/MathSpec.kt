package baaahs.util

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlin.math.PI

class MathSpec : DescribeSpec({
    describe("Math utils") {
        describe("degrees to radians") {
            it("should return a value in (-π, π]") {
                withClue("180°")  {
                    deg2rad(180.0) shouldBe(PI)
                }
                withClue("90°") {
                    deg2rad(90.0).shouldBe(PI / 2)
                }
                withClue("-90°") {
                    deg2rad(-90.0).shouldBe(-PI / 2)
                }
                withClue("-180°") {
                    deg2rad(-180.0).shouldBe(PI)
                }
                withClue("360°") {
                    deg2rad(360.0).shouldBe(0.0)
                }
                withClue("-540°") {
                    deg2rad(-540.0).shouldBe(PI)
                }
            }
        }

        describe("radians to degrees") {
            it("should return a value in (-180, 180]") {
                withClue("π") {
                    rad2deg(PI).shouldBe(180.0)
                }
                withClue("2π") {
                    rad2deg(2 * PI).shouldBe(0.0)
                }
                withClue("3π") {
                    rad2deg(3 * PI).shouldBe(180.0)
                }
                withClue("π/2") {
                    rad2deg(PI / 2).shouldBe(90.0)
                }
                withClue("-π/2") {
                    rad2deg(-PI / 2).shouldBe(-90.0)
                }
                withClue("-π") {
                    rad2deg(-PI).shouldBe(180.0)
                }
                withClue("0") {
                    rad2deg(0.0).shouldBe(0.0)
                }
                withClue("π") {
                    rad2deg(-1.5 * PI).shouldBe(90.0)
                }
            }
        }
    }
})