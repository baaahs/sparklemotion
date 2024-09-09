@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLRenderingContext

external interface `T$53` {
    var buffer: WebGLBuffer
    var type: GLenum
    var bytesPerElement: Number
    var version: Number
}

open external class WebGLAttributes(gl: WebGLRenderingContext, capabilities: WebGLCapabilities) {
    open fun get(attribute: BufferAttribute): `T$53`
    open fun get(attribute: InterleavedBufferAttribute): `T$53`
    open fun remove(attribute: BufferAttribute)
    open fun remove(attribute: InterleavedBufferAttribute)
    open fun update(attribute: BufferAttribute, bufferType: GLenum)
    open fun update(attribute: InterleavedBufferAttribute, bufferType: GLenum)
}