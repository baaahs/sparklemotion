@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

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

external open class TrackballControls(camera: Camera, domElement: HTMLElement? = definedExternally) : Controls<TrackballControlsEventMap> {
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