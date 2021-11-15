package baaahs.geom

import three.js.Euler

actual fun createMatrixWithPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4 {
    val matrix = three.js.Matrix4()
    matrix.makeRotationFromEuler(rotation.toThreeEuler())
    matrix.setPosition(position.x, position.y, position.z)
    return Matrix4(matrix.elements.map { it.toDouble() }.toDoubleArray())
}

fun EulerAngle.toThreeEuler(): Euler = Euler(xRad, yRad, zRad)