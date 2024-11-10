package baaahs.geom

import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue

class Matrix4FSpec : DescribeSpec({
    describe<Matrix4F> {
        context("default value") {
            it("should be the identity matrix") {
                Matrix4F().elements.shouldBe(floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f
                ))
            }
        }

        context("#identity") {
            it("should be the identity matrix") {
                Matrix4F.identity.elements.shouldBe(floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f
                ))
            }
        }

        context("equals") {
            it("should be equal to itself") {
                Matrix4F().shouldBe(Matrix4F())
            }

            it("should be equal to a matrix with the same elements") {
                Matrix4F(floatArrayOf(
                    1f, 2f, 3f, 4f,
                    5f, 6f, 7f, 8f,
                    9f, 10f, 11f, 12f,
                    13f, 14f, 15f, 16f
                )).shouldBe(Matrix4F(floatArrayOf(
                    1f, 2f, 3f, 4f,
                    5f, 6f, 7f, 8f,
                    9f, 10f, 11f, 12f,
                    13f, 14f, 15f, 16f
                )))
            }

            it("should not be equal to a matrix with different elements") {
                Matrix4F(floatArrayOf(
                    1f, 2f, 3f, 4f,
                    5f, 6f, 7f, 8f,
                    9f, 10f, 11f, 12f,
                    13f, 14f, 15f, 16f
                )).shouldNotBe(Matrix4F.identity)
            }
        }

        context("composition") {
            val position by value { Vector3F.origin }
            val rotation by value { EulerAngle.identity }
            val scale by value { Vector3F.unit3d }
            val matrix by value { Matrix4F.compose(position, rotation, scale) }
            val point by value { Vector3F.unit3d }

            context("identity") {
                it("should be the identity matrix") {
                    matrix.shouldBe(Matrix4F.identity)
                }
                it("shouldn't change the point") {
                    matrix.transform(point).shouldBe(point)
                }
            }

            context("shifted position") {
                override(position) { Vector3F(1f, 2f, 3f) }
                it("should be the identity matrix") {
                    matrix.shouldBe(Matrix4F(floatArrayOf(
                        1f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f,
                        0f, 0f, 1f, 0f,
                        1f, 2f, 3f, 1f
                    )))
                }
                it("should shift the point") {
                    matrix.transform(point)
                        .shouldBe(point + position)
                }
            }

            context("rotated") {
                override(rotation) { EulerAngle(pitchRad = PI / 2, yawRad = PI / 3, rollRad = PI / 4) }
                it("should be a rotated matrix") {
                    matrix.shouldVeryNearlyBe(Matrix4F(floatArrayOf(
                        0.3535534f, 0.3535534f, -0.8660254f, 0.0f,
                        0.61237234f, 0.6123725f, 0.5f, 0.0f, 0.70710677f,
                        -0.7071067f, 5.9604645E-8f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                    )), .1)
                }
                it("should rotate the point around the origin") {
                    matrix.transform(point)
                        .shouldVeryNearlyBe(Vector3F(1.6730325, 0.25881922, -0.36602533))
                }
            }

            context("scaled") {
                override(scale) { Vector3F(.5f, 1.25f, 1.5f) }
                it("should be a scaled matrix") {
                    matrix.shouldBe(Matrix4F(floatArrayOf(
                        0.5f, 0.0f, 0.0f, 0.0f,
                        0.0f, 1.25f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.5f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                    )))
                }
                it("should scale the point") {
                    matrix.transform(point)
                        .shouldBe(point * scale)
                }
            }

            context("combination") {
                override(position) { Vector3F(1f, 2f, 3f) }
                override(rotation) { EulerAngle(pitchRad = PI / 2, yawRad = PI / 3, rollRad = PI / 4) }
                override(scale) { Vector3F(.5f, 1.25f, 1.5f) }
                it("should apply all transformations to the matrix") {
                    matrix.shouldVeryNearlyBe(Matrix4F(floatArrayOf(
                        0.17677668f, 0.17677668f, -0.43301272f, 0.0f,
                        0.7654656f, 0.7654655f, 0.62499994f, 0.0f,
                        1.0606601f, -1.0606601f, -3.278354E-8f,
                        0.0f, 1.0f, 2.0f, 3.0f, 1.0f
                    )))
                }
                it("should apply all transformations to the point") {
                    matrix.transform(point)
                        .shouldVeryNearlyBe(Vector3F(3.0029023, 1.8815823, 3.1919873))
                }
            }
        }

        context("a transformation") {
            val position by value { Vector3F(x = -11f, y = 202.361f, z = 27.5f) }
            val rotation by value {
                EulerAngle(
                    pitchRad = 0.0,
                    yawRad = -0.15707963267948966,
                    rollRad = 1.5707963267948966
                )
            }
            val scale by value { Vector3F(1f, 1f, 1.5f) }
            val matrix by value { Matrix4F.compose(position, rotation, scale) }

            it("should be calculated properly") {
                val expected = floatArrayOf(
                    1.1920929E-7f, 0.9876882f, 0.15643446f, 0.0f,
                    -0.9999999f, 1.1920929E-7f, 0.0f, 0.0f,
                    0.0f, -0.23465168f, 1.4815325f, 0.0f,
                    -11.0f, 202.361f, 27.5f, 1.0f
                )

                matrix.elements.shouldVeryNearlyBe(expected)
            }

            it("should have correct scale") {
                matrix.scale.x.shouldBeWithinPercentageOf(1f, .0001)
                matrix.scale.y.shouldBeWithinPercentageOf(1f, .0001)
                matrix.scale.z.shouldBeWithinPercentageOf(1.5f, .0001)
            }

            context("times") {
                it("should premultiply") {
                    (matrix * Matrix4F.identity)
                        .shouldVeryNearlyBe(matrix)
                }

                it("should postmultiply") {
                    (Matrix4F.identity * matrix)
                        .shouldVeryNearlyBe(matrix)
                }
            }
        }

        describe("serialization") {
            it("should serialize") {
                Json.encodeToJsonElement(Matrix4F.identity).shouldBe(
                    JsonArray(Matrix4F.identity.elements.map { JsonPrimitive(it) })
                )
            }

            it("should deserialize") {
                val json = JsonArray(Matrix4F.identity.elements.map { JsonPrimitive(it) })
                Json.decodeFromJsonElement(Matrix4F.serializer(), json).shouldBe(
                    Matrix4F.identity
                )
            }
        }
    }
})

fun Vector3F.shouldVeryNearlyBe(expected: Vector3F, percentage: Double = .01): Vector3F {
    floatArrayOf(x, y, z).shouldVeryNearlyBe(with (expected) { floatArrayOf(x, y, z) }, percentage)
    return this
}

fun Matrix4F.shouldVeryNearlyBe(expected: Matrix4F, percentage: Double = .01): Matrix4F {
    elements.shouldVeryNearlyBe(expected.elements, percentage)
    return this
}

fun FloatArray.shouldVeryNearlyBe(expected: FloatArray, percentage: Double = .01): FloatArray {
    val allInRange = this.zip(expected).all { (actual, expected) ->
        actual.isWithinPercentage(expected, percentage)
    }
    if (allInRange) {
        return this
    } else {
        println("shouldVeryNearlyBe matched?: $allInRange\n" +
                "Expect: ${expected.joinToString(", ")}\n" +
                "Actual: ${this.joinToString(", ")}")
        this.joinToString(", ").shouldBe(expected.joinToString())
    }
    return this
}

private fun Float.isWithinPercentage(expected: Float, percentage: Double): Boolean {
    val tolerance = expected.times(percentage / 100).absoluteValue.toFloat()
    val range = (expected - tolerance)..(expected + tolerance)
    val floatFuzz = 2E-7
    if (this !in range) {
        val diff = abs(this - expected)
        println(
            "isWithinPercentage matched?: ${this in range}\n" +
                    "Expect: $expected\n" +
                    "Actual: $this in $range\n" +
                    "$diff < $floatFuzz = ${diff < floatFuzz}"
        )
        if (diff < floatFuzz) return true
    }
    return this in range
}
