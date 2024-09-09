@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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