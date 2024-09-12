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

external open class Cylindrical(radius: Number = definedExternally, theta: Number = definedExternally, y: Number = definedExternally) {
    open var radius: Number
    open var theta: Number
    open var y: Number
    open fun clone(): Cylindrical /* this */
    open fun copy(other: Cylindrical): Cylindrical /* this */
    open fun set(radius: Number, theta: Number, y: Number): Cylindrical /* this */
    open fun setFromVector3(vec3: Vector3): Cylindrical /* this */
    open fun setFromCartesianCoords(x: Number, y: Number, z: Number): Cylindrical /* this */
}