@file:JsModule("camera-controls")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three_ext

import org.w3c.dom.Element
import three.js.Box3
import three.js.Camera
import three.js.Mesh
import three.js.Vector3

@JsName("default")
external class CameraControls(camera: Camera, domElement: Element) {
    fun getPosition(out: Vector3? = definedExternally): Vector3
    fun getTarget(out: Vector3? = definedExternally): Vector3

    fun update(delta: Number)

    fun fitToBox(
        box3OrMesh: Box3,
        enableTransition: Boolean,
        options: Options? = definedExternally
    )

    fun fitToBox(
        box3OrMesh: Mesh<*, *>,
        enableTransition: Boolean,
        options: Options? = definedExternally
    )

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