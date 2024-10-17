package three_ext

import three.Box3
import three.Object3D

fun Box3.expandByObjectLocal(obj: Object3D): Box3 {
    // Computes the local bounding box of an object (including its children).
    val geometry = obj.asDynamic()["geometry"]

    if (geometry !== undefined) {
        if (geometry.boundingBox === null) {
            geometry.computeBoundingBox()
        }

        tempBox.copy(geometry.boundingBox!!)
        tempBox.applyMatrix4(obj.matrix)

        this.union(tempBox)
    }

    val children = obj.children.unsafeCast<Array<Object3D>>()

    for (child in children) {
        this.expandByObjectLocal(child)
    }

    return this
}

private val tempBox = Box3()
