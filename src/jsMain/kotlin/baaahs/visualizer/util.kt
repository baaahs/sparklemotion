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

    fun rotate(box3: Box3) {
        box3.applyMatrix4(matrix)
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

var Object3D.boundsForCameraFit: Box3?
    get() = this.userData.asDynamic().boundsForCameraFit as Box3?
    set(value) { this.userData.asDynamic().boundsForCameraFit = value }

var Object3D.ignoreChildrenForCameraFit: Boolean
    get() = this.userData.asDynamic().ignoreChildrenForCameraFit as Boolean? ?: false
    set(value) { this.userData.asDynamic().ignoreChildrenForCameraFit = value }

fun Object3D.ignoreForCameraFit() {
    boundsForCameraFit = Box3()
    ignoreChildrenForCameraFit = true
}

// Adapted from three's Box3.expandByObject
private val _box = Box3()
fun Box3.expandByObjectForCameraFit(obj: Object3D) {
    // Computes the world-axis-aligned bounding box of an object (including its children),
    // accounting for both the object's, and children's, world transforms

    obj.updateWorldMatrix(updateParents = false, updateChildren = false)

    val b = obj.boundsForCameraFit ?: run {
        val geometry = obj.asDynamic().geometry
        if (geometry !== undefined) {
            if (geometry.boundingBox === null) {
                geometry.computeBoundingBox()
            }
        }
        geometry?.boundingBox as Box3?
    }?.let { boundingBox ->
        _box.copy(boundingBox)
        _box.applyMatrix4(obj.matrixWorld)

        this.union(_box)
    }

    if (!obj.ignoreChildrenForCameraFit) {
        val children = obj.children
        for (child in children) {
            this.expandByObjectForCameraFit(child)
        }
    }
}