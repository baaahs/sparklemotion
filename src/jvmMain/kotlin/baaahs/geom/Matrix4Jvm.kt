package baaahs.geom

import com.jogamp.opengl.math.Quaternion

actual fun createMatrixWithPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4 {
    val quaternion = Quaternion()
    quaternion.setFromEuler(rotation.bankRad.toFloat(), rotation.headingRad.toFloat(), rotation.attitudeRad.toFloat())

    val matrix = com.jogamp.opengl.math.Matrix4()
    quaternion.toMatrix(matrix.matrix, 0)

    // TODO: for some reason this doesn't match the results from JS?
//    matrix.translate(origin.x, origin.y, origin.z)

    matrix.matrix.apply {
        this[3*4 + 0] = position.x
        this[3*4 + 1] = position.y
        this[3*4 + 2] = position.z
    }

    return Matrix4(matrix.matrix.map { it.toDouble() }.toDoubleArray())
}