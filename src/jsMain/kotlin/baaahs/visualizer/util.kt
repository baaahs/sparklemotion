package baaahs.visualizer

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.*
import info.laht.threekt.math.*

fun Face3.segments() = arrayOf(arrayOf(a, b), arrayOf(b, c), arrayOf(c, a))
fun Array<Int>.asKey() = sorted().joinToString("-")

class Rotator(val from: Vector3, val to: Vector3) {
    private val quaternion = Quaternion()
    private val matrix = Matrix4()

    init {
        quaternion.setFromUnitVectors(from, to)
        matrix.makeRotationFromQuaternion(quaternion)
    }

    fun rotate(vararg geoms: Geometry) {
        geoms.forEach { it.applyMatrix(matrix) }
    }

    fun rotate(vararg geoms: BufferGeometry) {
        geoms.forEach { it.applyMatrix(matrix) }
    }

    fun rotate(vararg vectors: Vector3) {
        vectors.forEach { it.applyMatrix4(matrix) }
    }

    fun invert(): Rotator = Rotator(to, from)
}