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

external interface OrbitControlsEventMap {
    var change: Any
    var start: Any
    var end: Any
}

external interface `T$58` {
    var LEFT: String
    var UP: String
    var RIGHT: String
    var BOTTOM: String
}

external interface `T$59` {
    var ONE: TOUCH?
        get() = definedExternally
        set(value) = definedExternally
    var TWO: TOUCH?
        get() = definedExternally
        set(value) = definedExternally
}

external open class OrbitControls(obj: Camera, domElement: HTMLElement? = definedExternally) : Controls<OrbitControlsEventMap> {
    open var target: Vector3
    open var cursor: Vector3
    open var minDistance: Number
    open var maxDistance: Number
    open var minZoom: Number
    open var maxZoom: Number
    open var minTargetRadius: Number
    open var maxTargetRadius: Number
    open var minPolarAngle: Number
    open var maxPolarAngle: Number
    open var minAzimuthAngle: Number
    open var maxAzimuthAngle: Number
    open var enableDamping: Boolean
    open var dampingFactor: Number
    open var enableZoom: Boolean
    open var zoomSpeed: Number
    open var enableRotate: Boolean
    open var rotateSpeed: Number
    open var enablePan: Boolean
    open var panSpeed: Number
    open var screenSpacePanning: Boolean
    open var keyPanSpeed: Number
    open var zoomToCursor: Boolean
    open var autoRotate: Boolean
    open var autoRotateSpeed: Number
    open var keys: `T$58`
    open var mouseButtons: `T$56`
    open var touches: `T$59`
    open var target0: Vector3
    open var position0: Vector3
    open var zoom0: Number
    open fun getPolarAngle(): Number
    open fun getAzimuthalAngle(): Number
    open fun getDistance(): Number
    open fun listenToKeyEvents(domElement: HTMLElement)
    open fun listenToKeyEvents(domElement: Window)
    open fun stopListenToKeyEvents()
    open fun saveState()
    open fun reset()
    open fun update(deltaTime: Number? = definedExternally): Boolean
}