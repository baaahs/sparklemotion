package baaahs.geom

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object Matrix4Spec : Spek({
    describe<Matrix4> {
        context("fromPositionAndOrientation") {
            val position by value { Vector3F(x = -11f, y = 202.361f, z = 27.5f) }
            val rotation by value { EulerAngle(pitchRad = 0.0, yawRad = -0.15707963267948966, rollRad = 1.5707963267948966) }
            val matrix4 by value { Matrix4.fromPositionAndOrientation(position, rotation) }

            it("should be calculated properly") {
                expect(matrix4.elements.toList()).toEqual(
                    listOf(
                        0.0, 1.0, 0.0, 0.0,
                        -0.9876883625984192, 0.0, -0.15643449127674103, 0.0,
                        -0.15643449127674103, 0.0, 0.9876883625984192, 0.0,
                        -11.0, 202.36099243164062, 27.5, 1.0
                    )
                )
            }
        }
    }
})