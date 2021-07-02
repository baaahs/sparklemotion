package baaahs.visualizer

import baaahs.geom.Vector3F
import three.js.*
import three_ext.Matrix4

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
        geoms.forEach { it.applyMatrix4(matrix) }
    }

    fun rotate(vararg geoms: BufferGeometry) {
        geoms.forEach { it.applyMatrix4(matrix) }
    }

    fun rotate(vararg vectors: Vector3) {
        vectors.forEach { it.applyMatrix4(matrix) }
    }

    fun invert(): Rotator = Rotator(to, from)
}

fun <T> MutableList<T>.findOrAdd(value: T): Int {
    var index = indexOf(value)
    if (index == -1) {
        index = size
        add(value)
    }
    return index
}

fun Vector3F.toVector3(): Vector3 = Vector3(x, y, z)
