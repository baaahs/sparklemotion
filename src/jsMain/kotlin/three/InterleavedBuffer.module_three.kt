@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$24` {
    var uuid: String
    var buffer: String
    var type: String
    var stride: Number
}

open external class InterleavedBuffer(array: ArrayLike<Number>, stride: Number) {
    open var array: ArrayLike<Number>
    open var stride: Number
    open var usage: Usage
    open var updateRange: `T$1`
    open var version: Number
    open var length: Number
    open var count: Number
    open var needsUpdate: Boolean
    open var uuid: String
    open fun setUsage(usage: Usage): InterleavedBuffer
    open fun clone(data: Any?): InterleavedBuffer /* this */
    open fun copy(source: InterleavedBuffer): InterleavedBuffer /* this */
    open fun copyAt(index1: Number, attribute: InterleavedBufferAttribute, index2: Number): InterleavedBuffer
    open fun set(value: ArrayLike<Number>, index: Number): InterleavedBuffer
    open fun toJSON(data: Any?): `T$24`
}