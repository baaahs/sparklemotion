@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class InstancedInterleavedBuffer(array: ArrayLike<Number>, stride: Number, meshPerAttribute: Number = definedExternally) : InterleavedBuffer {
    open var meshPerAttribute: Number
}