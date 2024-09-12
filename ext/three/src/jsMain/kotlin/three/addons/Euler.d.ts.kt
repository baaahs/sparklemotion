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

external open class Euler(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */ = definedExternally) {
    open var x: Number
    open var y: Number
    open var z: Number
    open var order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */
    open val isEuler: Boolean
    open var _onChangeCallback: () -> Unit
    open fun set(x: Number, y: Number, z: Number, order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */ = definedExternally): Euler
    open fun clone(): Euler /* this */
    open fun copy(euler: Euler): Euler /* this */
    open fun setFromRotationMatrix(m: Matrix4, order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */ = definedExternally, update: Boolean = definedExternally): Euler
    open fun setFromQuaternion(q: Quaternion, order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */ = definedExternally, update: Boolean = definedExternally): Euler
    open fun setFromVector3(v: Vector3, order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */ = definedExternally): Euler
    open fun reorder(newOrder: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */): Euler
    open fun equals(euler: Euler): Boolean
    open fun fromArray(array: Any /* JsTuple<x, Number, y, Number, z, Number, Any, String> */): Euler
    open fun toArray(array: Any = definedExternally, offset: Number = definedExternally): dynamic /* JsTuple<x, Number, y, Number, z, Number, Any, String> */
    open fun _onChange(callback: () -> Unit): Euler /* this */

    companion object {
        var DEFAULT_ORDER: String /* "XYZ" */
    }
}