package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.math.PI
import kotlin.math.abs

class LightRingSpec : Spek({
    describe<LightRing> {
        val lightRing by value {
            LightRing(
                "", "", Vector3F.origin, 1f, Vector3F.facingForward
            )
        }

        it("should create pixels at the right spot") {
            expect(lightRing.calculatePixelLocations(4).map { it.round() }).toEqual(
                listOf(
                    Vector3F(1f, 0f, 0f),
                    Vector3F(0f, -1f, 0f),
                    Vector3F(-1f, 0f, 0f),
                    Vector3F(0f, 1f, 0f)
                )
            )
        }

        context("starting at 12:00") {
            override(lightRing) {
                LightRing(
                    "", "", Vector3F.origin, 1f, Vector3F.facingForward,
                    firstPixelRadians = (PI / 2).toFloat()
                )
            }

            it("should create pixels at the right spot") {
                expect(lightRing.calculatePixelLocations(4).map { it.round() }).toEqual(
                    listOf(
                        Vector3F(0f, 1f, 0f),
                        Vector3F(1f, 0f, 0f),
                        Vector3F(0f, -1f, 0f),
                        Vector3F(-1f, 0f, 0f)
                    )
                )
            }
        }

        context("counterclockwise") {
            override(lightRing) {
                LightRing(
                    "", "", Vector3F.origin, 1f, Vector3F.facingForward,
                    pixelDirection = LightRing.PixelDirection.Counterclockwise
                )
            }

            it("should create pixels at the right spot") {
                expect(lightRing.calculatePixelLocations(4).map { it.round() }).toEqual(
                    listOf(
                        Vector3F(1f, 0f, 0f),
                        Vector3F(0f, 1f, 0f),
                        Vector3F(-1f, 0f, 0f),
                        Vector3F(0f, -1f, 0f)
                    )
                )
            }

        }
    }
})

fun Vector3F.round(): Vector3F {
    fun Float.round() = if (abs(this - 0f) < 0.01f) 0f else this
    return Vector3F(x.round(), y.round(), z.round())
}