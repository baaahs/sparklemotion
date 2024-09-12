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

external open class FirstPersonControls(obj: Camera, domElement: HTMLElement = definedExternally) : Controls<Any> {
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