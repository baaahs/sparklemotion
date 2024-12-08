package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Controls
import three.Vector3

external interface PointerLockControlsEventMap {
    var change: Any
    var lock: Any
    var unlock: Any
}

open external class PointerLockControls(camera: Camera, domElement: HTMLElement? = definedExternally) : Controls {
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