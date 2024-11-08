package baaahs.geom

import baaahs.describe
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
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
                    expect(actual).toBeWithErrorTolerance(expected, 0.0001f)
                }
            }

            it("should have correct scale") {
                expect(matrix.scale.x).toBeWithErrorTolerance(1f, .00001f)
                expect(matrix.scale.y).toBeWithErrorTolerance(1f, .00001f)
                expect(matrix.scale.z).toBeWithErrorTolerance(1.5f, .00001f)
            }

            context("times") {
                it("should premultiply") {
                    expect(matrix * Matrix4F.identity)
                        .toEqual(matrix)
                }

                it("should postmultiply") {
                    expect(Matrix4F.identity * matrix)
                        .toEqual(matrix)
                }
            }
        }

        describe("serialization") {
            it("should serialize") {
                expect(Json.encodeToJsonElement(Matrix4F.identity)).toEqual(
                    JsonArray(Matrix4F.identity.elements.map { JsonPrimitive(it) })
                )
            }

            it("should deserialize") {
                val json = JsonArray(Matrix4F.identity.elements.map { JsonPrimitive(it) })
                expect(Json.decodeFromJsonElement(Matrix4F.serializer(), json)).toEqual(
                    Matrix4F.identity
                )
            }
        }
    }
})