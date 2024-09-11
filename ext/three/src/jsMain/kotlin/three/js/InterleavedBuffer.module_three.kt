@file:JsModule("three")
@file:JsNonModule
package three.js

import org.khronos.webgl.*

external interface `T$5` {
    var uuid: String
    var buffer: String
    var type: String
    var stride: Number
}

open external class InterleavedBuffer {
    open val isInterleavedBuffer: Boolean
    constructor(array: Int8Array, stride: Number)
    constructor(array: Uint8Array, stride: Number)
    constructor(array: Uint8ClampedArray, stride: Number)
    constructor(array: Int16Array, stride: Number)
    constructor(array: Uint16Array, stride: Number)
    constructor(array: Int32Array, stride: Number)
    constructor(array: Uint32Array, stride: Number)
    constructor(array: Float32Array, stride: Number)
    constructor(array: Float64Array, stride: Number)
    open var array: dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */
    open var stride: Number
    open var usage: Any
    open var updateRange: `T$2`
    open var updateRanges: Array<`T$3`>
    open var version: Number
    open var count: Number
    open var uuid: String
    open fun set(value: Array<Number>, offset: Number): InterleavedBuffer /* this */
    open fun setUsage(value: Any): InterleavedBuffer /* this */
    open fun addUpdateRange(start: Number, count: Number)
    open fun clearUpdateRanges()
    open fun copy(source: InterleavedBuffer): InterleavedBuffer /* this */
    open fun copyAt(index1: Number, attribute: InterleavedBufferAttribute, index2: Number): InterleavedBuffer /* this */
    open fun clone(data: Any): InterleavedBuffer
    open fun toJSON(data: Any): `T$5`
}