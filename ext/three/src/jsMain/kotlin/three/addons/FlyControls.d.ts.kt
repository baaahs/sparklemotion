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