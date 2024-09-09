@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

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

external open class Line3(start: Vector3 = definedExternally, end: Vector3 = definedExternally) {
    open var start: Vector3
    open var end: Vector3
    open fun set(start: Vector3 = definedExternally, end: Vector3 = definedExternally): Line3
    open fun clone(): Line3 /* this */
    open fun copy(line: Line3): Line3 /* this */
    open fun getCenter(target: Vector3): Vector3
    open fun delta(target: Vector3): Vector3
    open fun distanceSq(): Number
    open fun distance(): Number
    open fun at(t: Number, target: Vector3): Vector3
    open fun closestPointToPointParameter(point: Vector3, clampToLine: Boolean = definedExternally): Number
    open fun closestPointToPoint(point: Vector3, clampToLine: Boolean, target: Vector3): Vector3
    open fun applyMatrix4(matrix: Matrix4): Line3
    open fun equals(line: Line3): Boolean
}