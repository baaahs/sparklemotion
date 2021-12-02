package baaahs.geom

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.spekframework.spek2.Spek

object Matrix4FSpec : Spek({
    describe<Matrix4F> {
        context("fromPositionAndOrientation") {
            val position by value { Vector3F(x = -11f, y = 202.361f, z = 27.5f) }
            val rotation by value {
                EulerAngle(
                    pitchRad = 0.0,
                    yawRad = -0.15707963267948966,
                    rollRad = 1.5707963267948966
                )
            }
            val matrix by value { Matrix4F.fromPositionAndRotation(position, rotation) }

            it("should be calculated properly") {
                expect(matrix.elements.toList()).toEqual(
                    listOf(
                        -0.0f, 1.0f, 0.0f, 0.0f,
                        -0.9876883f, 0.0f, -0.15643448f, 0.0f,
                        -0.15643448f, -0.0f, 0.9876883f, 0.0f,
                        -11.0f, 202.361f, 27.5f, 1.0f
                    )
                )
            }

            it("should have unit scale") {
                expect(matrix.scale.x).toBeWithErrorTolerance(1f, .00001f)
                expect(matrix.scale.y).toBeWithErrorTolerance(1f, .00001f)
                expect(matrix.scale.z).toBeWithErrorTolerance(1f, .00001f)
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