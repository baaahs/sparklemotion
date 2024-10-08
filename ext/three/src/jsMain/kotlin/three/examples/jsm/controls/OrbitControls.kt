@file:JsModule("three/examples/jsm/controls/OrbitControls")
@file:JsNonModule
package three.examples.jsm.controls

import three.EventDispatcher
import three.Object3D
import three.Vector3
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