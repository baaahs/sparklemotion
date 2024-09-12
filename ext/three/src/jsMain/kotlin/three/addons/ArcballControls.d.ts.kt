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

external interface ArcballControlsEventMap {
    var change: Any
    var start: Any
    var end: Any
}

external open class ArcballControls(camera: Camera, domElement: HTMLElement? = definedExternally, scene: Scene? = definedExternally) : Controls<ArcballControlsEventMap> {
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