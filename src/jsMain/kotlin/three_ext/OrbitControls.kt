@file:JsModule("three/examples/jsm/controls/OrbitControls")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three_ext

import three.js.EventDispatcher
import three.js.Object3D
import three.js.Vector3
import web.html.HTMLCanvasElement

open external class OrbitControls(theObject: Any, domElement: Any) : EventDispatcher {
    var `object`: Object3D
    var domObject: HTMLCanvasElement
    var enabled: Boolean
    var target: Vector3
    var minDistance: Double
    var maxDistance: Double
    var minZoom: Double
    var maxZoom: Double
    var minPolarAngle: Double
    var maxPolarAngle: Double
    var minAzimuthAngle: Double
    var maxAzimuthAngle: Double
    var enableDamping: Boolean
    var dampingFactor: Double
    var enableZoom: Boolean
    var zoomSpeed: Double
    var enableRotate: Boolean
    var rotateSpeed: Double
    var enablePan: Boolean
    var panSpeed: Double
    var screenSpacePanning: Boolean
    var keyPanSpeed: Double
    var autoRotate: Boolean
    var autoRotateSpeed: Double
    var enableKeys: Boolean
    var keys: dynamic
    var mouseButtons: dynamic
    var touches: dynamic

    fun update()
    fun dispose()
    fun saveState()
}