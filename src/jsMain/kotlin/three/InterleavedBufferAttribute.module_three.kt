@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$23` {
    var isInterleavedBufferAttribute: Boolean
    var itemSize: Number
    var data: String
    var offset: Number
    var normalized: Boolean
}

open external class InterleavedBufferAttribute(interleavedBuffer: InterleavedBuffer, itemSize: Number, offset: Number, normalized: Boolean = definedExternally) {
    open var name: String
    open var data: InterleavedBuffer
    open var itemSize: Number
    open var offset: Number
    open var normalized: Boolean
    open var isInterleavedBufferAttribute: Boolean
    open fun applyMatrix4(m: Matrix4): InterleavedBufferAttribute /* this */
    open fun clone(data: Any? = definedExternally): BufferAttribute
    open fun getX(index: Number): Number
    open fun setX(index: Number, x: Number): InterleavedBufferAttribute
    open fun getY(index: Number): Number
    open fun setY(index: Number, y: Number): InterleavedBufferAttribute
    open fun getZ(index: Number): Number
    open fun setZ(index: Number, z: Number): InterleavedBufferAttribute
    open fun getW(index: Number): Number
    open fun setW(index: Number, z: Number): InterleavedBufferAttribute
    open fun setXY(index: Number, x: Number, y: Number): InterleavedBufferAttribute
    open fun setXYZ(index: Number, x: Number, y: Number, z: Number): InterleavedBufferAttribute
    open fun setXYZW(index: Number, x: Number, y: Number, z: Number, w: Number): InterleavedBufferAttribute
    open fun toJSON(data: Any? = definedExternally): `T$23`
}