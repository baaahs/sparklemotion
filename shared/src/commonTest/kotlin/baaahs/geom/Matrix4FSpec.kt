package baaahs.geom

import baaahs.describe
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

object Matrix4FSpec : DescribeSpec({
    describe<Matrix4F> {
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
                val expected = listOf(
                    1.1920929E-7f, 0.9876882f, 0.15643446f, 0.0f,
                    -0.9999999f, 1.1920929E-7f, 0.0f, 0.0f,
                    0.0f, -0.23465168f, 1.4815325f, 0.0f,
                    -11.0f, 202.361f, 27.5f, 1.0f
                )

                matrix.elements.zip(expected).forEachIndexed { index, (actual, expected) ->
                    actual.shouldBeWithinPercentageOf(expected, 0.0001)
                }
            }

            it("should have correct scale") {
                matrix.scale.x.shouldBeWithinPercentageOf(1f, .0001)
                matrix.scale.y.shouldBeWithinPercentageOf(1f, .0001)
                matrix.scale.z.shouldBeWithinPercentageOf(1.5f, .0001)
            }

            context("times") {
                it("should premultiply") {
                    (matrix * Matrix4F.identity)
                        .shouldBe(matrix)
                }

                it("should postmultiply") {
                    (Matrix4F.identity * matrix)
                        .shouldBe(matrix)
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