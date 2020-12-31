@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class Euler(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, order: String = definedExternally) {
    open var x: Number
    open var y: Number
    open var z: Number
    open var order: String
    open var isEuler: Boolean
    open var _onChangeCallback: Function<*>
    open fun set(x: Number, y: Number, z: Number, order: String = definedExternally): Euler
    open fun clone(): Euler /* this */
    open fun copy(euler: Euler): Euler /* this */
    open fun setFromRotationMatrix(m: Matrix4, order: String = definedExternally): Euler
    open fun setFromQuaternion(q: Quaternion, order: String = definedExternally): Euler
    open fun setFromVector3(v: Vector3, order: String = definedExternally): Euler
    open fun reorder(newOrder: String): Euler
    open fun equals(euler: Euler): Boolean
    open fun fromArray(xyzo: Array<Any>): Euler
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toVector3(optionalResult: Vector3 = definedExternally): Vector3
    open fun _onChange(callback: Function<*>): Euler /* this */

    companion object {
        var RotationOrders: Array<String>
        var DefaultOrder: String
    }
}