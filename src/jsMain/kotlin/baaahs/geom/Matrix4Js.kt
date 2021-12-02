package baaahs.geom

import three.js.Euler
import three.js.Quaternion
import three.js.Vector3

internal actual fun createMatrixWithPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4 {
    val matrix = three.js.Matrix4()
    matrix.makeRotationFromEuler(rotation.toThreeEuler())
    matrix.setPosition(position.x, position.y, position.z)
    return Matrix4(matrix.elements.map { it.toDouble() }.toDoubleArray())
}

internal actual fun getPositionFromMatrix(matrix: Matrix4 ): Vector3F {
    val nativeMatrix = three.js.Matrix4()
    nativeMatrix.fromArray(matrix.elements)
    val translation = Vector3()
    val rotation = Quaternion()
    val scale = Vector3()
    nativeMatrix.decompose(translation, rotation, scale)
    return translation.toVector3F()
}

internal actual fun getRotationFromMatrix(matrix: Matrix4): EulerAngle {
    val nativeMatrix = three.js.Matrix4()
    nativeMatrix.fromArray(matrix.elements)
    val translation = Vector3()
    val rotation = Quaternion()
    val scale = Vector3()
    nativeMatrix.decompose(translation, rotation, scale)
    val euler = Euler()
    euler.setFromQuaternion(rotation)
    return EulerAngle(euler.x.toDouble(),  euler.y.toDouble(), euler.z.toDouble())
}

fun EulerAngle.toThreeEuler(): Euler = Euler(xRad, yRad, zRad)