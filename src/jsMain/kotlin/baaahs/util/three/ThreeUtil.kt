package baaahs.util.three

import three.js.*
import web.events.Event
import web.events.EventTarget
import kotlin.math.abs
import kotlin.math.tan

object ThreeUtil {
    fun fitCameraToObject(camera: PerspectiveCamera, obj: Object3D, offset: Double = 1.5) {
        val boundingBox = Box3()
        boundingBox.setFromObject(obj)

        val center = boundingBox.getCenter(Vector3())
        val size = boundingBox.getSize(Vector3())

        val startDistance = center.distanceTo(camera.position)

        // here we must check if the screen is horizontal or vertical, because camera.fov is
        // based on the vertical direction.
        val endDistance = if (camera.aspect.toDouble() > 1) {
            ((size.y / 2) + offset) / abs(tan(camera.fov.toDouble() / 2))
        } else {
            ((size.y / 2) + offset) / abs(tan(camera.fov.toDouble() / 2)) / camera.aspect.toDouble()
        }

        camera.position.set(
            camera.position.x * endDistance / startDistance,
            camera.position.y * endDistance / startDistance,
            camera.position.z * endDistance / startDistance,
        )
        camera.lookAt(center)
    }
}

fun Box3.addPadding(fractionalAmount: Double) {
    val padding = min.distanceTo(max) * fractionalAmount
    expandByScalar(padding)
}

fun Float32BufferAttribute.resize(count: Int): Float32BufferAttribute {
    val oldSize = this.count * this.itemSize
    val newArray = DoubleArray(count * itemSize) { i ->
        if (i < oldSize) array[i] else 0.0
    }
    return Float32BufferAttribute(newArray, itemSize)
}

fun EventTarget.addEventListener(type: String, callback: (Event) -> Unit) {
    asDynamic().addEventListener(type, callback)
}
fun EventTarget.removeEventListener(type: String, callback: (Event) -> Unit) {
    asDynamic().removeEventListener(type, callback)
}
