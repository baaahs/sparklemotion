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

external interface PointerLockControlsEventMap {
    var change: Any
    var lock: Any
    var unlock: Any
}

external open class PointerLockControls(camera: Camera, domElement: HTMLElement? = definedExternally) : Controls<PointerLockControlsEventMap> {
    open var isLocked: Boolean
    open var minPolarAngle: Number
    open var maxPolarAngle: Number
    open var pointerSpeed: Number
    open fun getObject(): Camera
    open fun getDirection(v: Vector3): Vector3
    open fun moveForward(distance: Number)
    open fun moveRight(distance: Number)
    open fun lock()
    open fun unlock()
}