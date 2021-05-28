package baaahs.geom

import three.js.Euler

actual fun createMatrixWithPositionAndOrientation(origin: Vector3F, heading: Vector3F): Matrix4 {
    val matrix = three.js.Matrix4()
    matrix.makeRotationFromEuler(Euler(heading.x, heading.y, heading.z))
    matrix.setPosition(origin.x, origin.y, origin.z)
    return Matrix4(matrix.elements.map { it.toDouble() }.toDoubleArray())
}
