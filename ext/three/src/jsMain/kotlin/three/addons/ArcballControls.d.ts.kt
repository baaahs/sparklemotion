package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Controls
import three.Raycaster
import three.Scene

external interface ArcballControlsEventMap {
    var change: Any
    var start: Any
    var end: Any
}

open external class ArcballControls(camera: Camera, domElement: HTMLElement? = definedExternally, scene: Scene? = definedExternally) : Controls {
    open var scene: Scene?
    open var radiusFactor: Number
    open var focusAnimationTime: Number
    open var adjustNearFar: Boolean
    open var scaleFactor: Number
    open var dampingFactor: Number
    open var wMax: Number
    open var enableAnimations: Boolean
    open var enableGrid: Boolean
    open var cursorZoom: Boolean
    open var rotateSpeed: Number
    open var enablePan: Boolean
    open var enableRotate: Boolean
    open var enableZoom: Boolean
    open var minDistance: Number
    open var maxDistance: Number
    open var minZoom: Number
    open var maxZoom: Number
    open fun setMouseAction(operation: String /* "PAN" | "ROTATE" | "ZOOM" | "FOV" */, mouse: Number /* 0 | 1 | 2 */, key: String /* "SHIFT" | "CTRL" */ = definedExternally): Boolean
    open fun setMouseAction(operation: String /* "PAN" | "ROTATE" | "ZOOM" | "FOV" */, mouse: Number /* 0 | 1 | 2 */): Boolean
    open fun setMouseAction(operation: String /* "PAN" | "ROTATE" | "ZOOM" | "FOV" */, mouse: String /* "WHEEL" */, key: String /* "SHIFT" | "CTRL" */ = definedExternally): Boolean
    open fun setMouseAction(operation: String /* "PAN" | "ROTATE" | "ZOOM" | "FOV" */, mouse: String /* "WHEEL" */): Boolean
    open fun unsetMouseAction(mouse: Number /* 0 | 1 | 2 */, key: String /* "SHIFT" | "CTRL" */ = definedExternally): Boolean
    open fun unsetMouseAction(mouse: Number /* 0 | 1 | 2 */): Boolean
    open fun unsetMouseAction(mouse: String /* "WHEEL" */, key: String /* "SHIFT" | "CTRL" */ = definedExternally): Boolean
    open fun unsetMouseAction(mouse: String /* "WHEEL" */): Boolean
    open fun activateGizmos(isActive: Boolean)
    open fun setCamera(camera: Camera)
    open fun setGizmosVisible(value: Boolean)
    open fun setTbRadius(value: Number)
    open fun reset()
    open fun copyState()
    open fun pasteState()
    open fun saveState()
    open fun getRaycaster(): Raycaster
    open fun update()
}