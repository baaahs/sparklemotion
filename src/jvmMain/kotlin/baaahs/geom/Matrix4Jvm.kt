package baaahs.geom

import com.jogamp.opengl.math.Quaternion

actual fun createMatrixWithPositionAndOrientation(origin: Vector3F, heading: Vector3F): Matrix4 {
    val quaternion = Quaternion()
    quaternion.setFromEuler(floatArrayOf(heading.x, heading.y, heading.z))

    val matrix = com.jogamp.opengl.math.Matrix4()
    quaternion.toMatrix(matrix.matrix, 0)

    // TODO: for some reason this doesn't match the results from JS?
//    matrix.translate(origin.x, origin.y, origin.z)

    matrix.matrix.apply {
        this[3*4 + 0] = origin.x
        this[3*4 + 1] = origin.y
        this[3*4 + 2] = origin.z
    }

    return Matrix4(matrix.matrix.map { it.toDouble() }.toDoubleArray())
}