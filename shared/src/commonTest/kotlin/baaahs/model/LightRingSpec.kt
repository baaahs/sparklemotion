package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlin.math.PI
import kotlin.math.abs

class LightRingSpec : DescribeSpec({
    describe<LightRing> {
        val lightRing by value { LightRing("ring", radius = 1f) }

        it("should create pixels at the right spots") {
            lightRing.calculatePixelLocalLocations(4).map { it.round() }.shouldBe(
                listOf(
                    Vector3F(1f, 0f, 0f),
                    Vector3F(0f, -1f, 0f),
                    Vector3F(-1f, 0f, 0f),
                    Vector3F(0f, 1f, 0f)
                )
            )
        }

        context("starting at 12:00") {
            override(lightRing) { LightRing("ring", radius = 1f, firstPixelRadians = (PI / 2).toFloat()) }

            it("should create pixels at the right spots") {
                lightRing.calculatePixelLocalLocations(4).map { it.round() }.shouldBe(
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
                LightRing("ring", radius = 1f, pixelDirection = LightRing.PixelDirection.Counterclockwise)
            }

            it("should create pixels at the right spots") {
                lightRing.calculatePixelLocalLocations(4).map { it.round() }.shouldBe(
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