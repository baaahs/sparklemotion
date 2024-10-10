package baaahs.util

import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.because
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.math.PI

object MathSpec : Spek({
    describe("Math utils") {
        describe("degrees to radians") {
            it("should return a value in (-π, π]") {
                expect(deg2rad(180.0)).because("180°") {
                    toEqual(PI)
                }
                expect(deg2rad(90.0)).because("90°") {
                    toEqual(PI / 2)
                }
                expect(deg2rad(-90.0)).because("-90°") {
                    toEqual(-PI / 2)
                }
                expect(deg2rad(-180.0)).because("-180°") {
                    toEqual(PI)
                }
                expect(deg2rad(360.0)).because("360°") {
                    toEqual(0.0)
                }
                expect(deg2rad(-540.0)).because("-540°") {
                    toEqual(PI)
                }
            }
        }

        describe("radians to degrees") {
            it("should return a value in (-180, 180]") {
                expect(rad2deg(PI)).because("π") {
                    toEqual(180.0)
                }
                expect(rad2deg(2 * PI)).because("2π") {
                    toEqual(0.0)
                }
                expect(rad2deg(3 * PI)).because("3π") {
                    toEqual(180.0)
                }
                expect(rad2deg(PI / 2)).because("π/2") {
                    toEqual(90.0)
                }
                expect(rad2deg(-PI / 2)).because("-π/2") {
                    toEqual(-90.0)
                }
                expect(rad2deg(-PI)).because("-π") {
                    toEqual(180.0)
                }
                expect(rad2deg(0.0)).because("0") {
                    toEqual(0.0)
                }
                expect(rad2deg(-1.5 * PI)).because("π") {
                    toEqual(90.0)
                }
            }
        }
    }
})