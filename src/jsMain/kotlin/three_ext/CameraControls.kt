@file:JsModule("camera-controls")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three_ext

import three.js.Box3
import three.js.Camera
import three.js.Mesh
import three.js.Vector3
import web.dom.Element

@JsName("default")
external class CameraControls(camera: Camera, domElement: Element) {
    var dampingFactor: Double
    var enabled: Boolean

    fun getTarget(out: Vector3? = definedExternally): Vector3
    fun getPosition(out: Vector3? = definedExternally): Vector3
    fun getFocalOffset(out: Vector3? = definedExternally): Vector3

    fun update(delta: Number)

    fun rotate(azimuthAngle: Number, polarAngle: Number, enableTransition: Boolean)
    fun rotateAzimuthTo(azimuthAngle: Number, enableTransition: Boolean)
    fun rotatePolarTo(polarAngle: Number, enableTransition: Boolean)
    fun rotateTo(azimuthAngle: Number, polarAngle: Number, enableTransition: Boolean)
    fun dolly(distance: Number, enableTransition: Boolean)
    fun dollyTo(distance: Number, enableTransition: Boolean)
    fun zoom(zoomStep: Number, enableTransition: Boolean)
    fun zoomTo(zoomStep: Number, enableTransition: Boolean)
    fun truck(x: Number, y: Number, enableTransition: Boolean)
    fun forward(distance: Number, enableTransition: Boolean)
    fun moveTo(x: Number, y: Number, z: Number, enableTransition: Boolean)
    fun fitToBox(box3OrMesh: Box3, enableTransition: Boolean, options: Options? = definedExternally)
    fun fitToBox(box3OrMesh: Mesh<*, *>, enableTransition: Boolean, options: Options? = definedExternally)
    fun setPosition(x: Number, y: Number, z: Number, enableTransition: Boolean): Vector3
    fun setTarget(x: Number, y: Number, z: Number, enableTransition: Boolean): Vector3
    fun setFocalOffset(x: Number, y: Number, z: Number, enableTransition: Boolean)

    fun setLookAt(
        positionX: Number, positionY: Number, positionZ: Number,
        targetX: Number, targetY: Number, targetZ: Number,
        enableTransition: Boolean
    )

    companion object {
        fun install(libs: Libs)
    }
}

external interface Options {
    var paddingTop: Number
    var paddingLeft: Number
    var paddingBottom: Number
    var paddingRight: Number
}

external interface Libs {
    var THREE: Any
}