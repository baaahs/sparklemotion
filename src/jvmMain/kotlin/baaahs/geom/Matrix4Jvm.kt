package baaahs.geom

import org.joml.Matrix4f as NativeMatrix4F
import org.joml.Quaternionf as NativeQuaternionF
import org.joml.Vector3f as NativeVector3F

internal actual fun createMatrixWithPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4 {
    val matrix = NativeMatrix4F()
    matrix.setTranslation(position.x, position.y, position.z)
    matrix.setRotationXYZ(
        rotation.xRad.toFloat(), rotation.yRad.toFloat(), rotation.zRad.toFloat()
    )
    val matrixArray = FloatArray(16)
    matrix.get(matrixArray)
    return Matrix4(matrixArray.toDoubleArray())
}

internal actual fun getPositionFromMatrix(matrix: Matrix4): Vector3F {
    val nativeMatrix = nativeMatrix4(matrix)
    val translation = NativeVector3F()
    nativeMatrix.getTranslation(translation)
    return Vector3F(translation.x, translation.y, translation.z)
}

internal actual fun getRotationFromMatrix(matrix: Matrix4): EulerAngle {
    val nativeMatrix = nativeMatrix4(matrix)
    val rotation = NativeQuaternionF()
    rotation.setFromUnnormalized(nativeMatrix)
    val euler = NativeVector3F()
    rotation.getEulerAnglesXYZ(euler)
    return EulerAngle(euler[0].toDouble(),  euler[1].toDouble(), euler[2].toDouble())
}

private fun nativeMatrix4(matrix: Matrix4): NativeMatrix4F {
    return NativeMatrix4F().apply {
        set(matrix.elements.toFloatArray())
    }
}

fun FloatArray.toDoubleArray(): DoubleArray {
    return DoubleArray(size) { i -> get(i).toDouble() }
}

fun DoubleArray.toFloatArray(): FloatArray {
    return FloatArray(size) { i -> get(i).toFloat() }
}