@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Controls
import three.Vector3

open external class FirstPersonControls(obj: Camera, domElement: HTMLElement = definedExternally) : Controls {
    open var movementSpeed: Number
    open var lookSpeed: Number
    open var lookVertical: Boolean
    open var autoForward: Boolean
    open var activeLook: Boolean
    open var heightSpeed: Boolean
    open var heightCoef: Number
    open var heightMin: Number
    open var heightMax: Number
    open var constrainVertical: Boolean
    open var verticalMin: Number
    open var verticalMax: Number
    open var mouseDragOn: Boolean
    open fun handleResize()
    open fun lookAt(vector: Vector3): FirstPersonControls /* this */
    open fun lookAt(x: Number, y: Number, z: Number): FirstPersonControls /* this */
}