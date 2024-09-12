@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Controls
import three.Vector3

external interface TrackballControlsEventMap {
    var change: Any
    var start: Any
    var end: Any
}

external interface `T$60` {
    var left: Number
    var top: Number
    var width: Number
    var height: Number
}

open external class TrackballControls(camera: Camera, domElement: HTMLElement? = definedExternally) : Controls {
    open var screen: `T$60`
    open var rotateSpeed: Number
    open var zoomSpeed: Number
    open var panSpeed: Number
    open var noRotate: Boolean
    open var noZoom: Boolean
    open var noPan: Boolean
    open var staticMoving: Boolean
    open var dynamicDampingFactor: Number
    open var minDistance: Number
    open var maxDistance: Number
    open var minZoom: Number
    open var maxZoom: Number
    open var keys: dynamic /* JsTuple<String, String, String> */
    open var mouseButtons: `T$56`
    open var target: Vector3
    open fun handleResize()
    open fun update()
    open fun reset()
}