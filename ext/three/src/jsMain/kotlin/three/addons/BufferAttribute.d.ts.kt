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

external interface BufferAttributeJSON {
    var itemSize: Number
    var type: String
    var array: Array<Number>
    var normalized: Boolean
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var usage: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$3` {
    var offset: Number
    var count: Number
}

external interface `T$4` {
    var start: Number
    var count: Number
}

external open class BufferAttribute {
    constructor(array: Int8Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Int8Array, itemSize: Number)
    constructor(array: Uint8Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint8Array, itemSize: Number)
    constructor(array: Uint8ClampedArray, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint8ClampedArray, itemSize: Number)
    constructor(array: Int16Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Int16Array, itemSize: Number)
    constructor(array: Uint16Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint16Array, itemSize: Number)
    constructor(array: Int32Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Int32Array, itemSize: Number)
    constructor(array: Uint32Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint32Array, itemSize: Number)
    constructor(array: Float32Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Float32Array, itemSize: Number)
    constructor(array: Float64Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Float64Array, itemSize: Number)
    open var name: String
    open var array: dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */
    open var itemSize: Number
    open var usage: Any
    open var gpuType: Any
    open var updateRange: `T$3`
    open var updateRanges: Array<`T$4`>
    open var version: Number
    open var normalized: Boolean
    open val count: Number
    open val isBufferAttribute: Boolean
    open var onUploadCallback: () -> Unit
    open fun onUpload(callback: () -> Unit): BufferAttribute /* this */
    open fun setUsage(usage: Any): BufferAttribute /* this */
    open fun addUpdateRange(start: Number, count: Number)
    open fun clearUpdateRanges()
    open fun clone(): BufferAttribute
    open fun copy(source: BufferAttribute): BufferAttribute /* this */
    open fun copyAt(index1: Number, attribute: BufferAttribute, index2: Number): BufferAttribute /* this */
    open fun copyArray(array: ArrayLike<Number>): BufferAttribute /* this */
    open fun applyMatrix3(m: Matrix3): BufferAttribute /* this */
    open fun applyMatrix4(m: Matrix4): BufferAttribute /* this */
    open fun applyNormalMatrix(m: Matrix3): BufferAttribute /* this */
    open fun transformDirection(m: Matrix4): BufferAttribute /* this */
    open fun set(value: ArrayLike<Number>, offset: Number = definedExternally): BufferAttribute /* this */
    open fun set(value: ArrayLike<Number>): BufferAttribute /* this */
    open fun set(value: ArrayBufferView, offset: Number = definedExternally): BufferAttribute /* this */
    open fun set(value: ArrayBufferView): BufferAttribute /* this */
    open fun getComponent(index: Number, component: Number): Number
    open fun setComponent(index: Number, component: Number, value: Number)
    open fun getX(index: Number): Number
    open fun setX(index: Number, x: Number): BufferAttribute /* this */
    open fun getY(index: Number): Number
    open fun setY(index: Number, y: Number): BufferAttribute /* this */
    open fun getZ(index: Number): Number
    open fun setZ(index: Number, z: Number): BufferAttribute /* this */
    open fun getW(index: Number): Number
    open fun setW(index: Number, z: Number): BufferAttribute /* this */
    open fun setXY(index: Number, x: Number, y: Number): BufferAttribute /* this */
    open fun setXYZ(index: Number, x: Number, y: Number, z: Number): BufferAttribute /* this */
    open fun setXYZW(index: Number, x: Number, y: Number, z: Number, w: Number): BufferAttribute /* this */
    open fun toJSON(): BufferAttributeJSON
}