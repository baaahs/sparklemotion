package three.js

open external class Euler(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, order: String /* "XYZ" | "YXZ" | "ZXY" | "ZYX" | "YZX" | "XZY" */ = definedExternally) {
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