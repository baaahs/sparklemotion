@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Controls

external interface FlyControlsEventMap {
    var change: Any
}

open external class FlyControls(obj: Camera, domElement: HTMLElement? = definedExternally) : Controls {
    open var movementSpeed: Number
    open var rollSpeed: Number
    open var dragToLook: Boolean
    open var autoForward: Boolean
}