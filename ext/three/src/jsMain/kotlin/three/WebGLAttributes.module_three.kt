@file:JsModule("three")
@file:JsNonModule
package three

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
import web.gl.WebGL2RenderingContext

external interface `T$63` {
    var buffer: WebGLBuffer
    var type: Number
    var bytesPerElement: Number
    var version: Number
    var size: Number
}

open external class WebGLAttributes {
    constructor(gl: WebGLRenderingContext)
    constructor(gl: WebGL2RenderingContext)
    open fun get(attribute: BufferAttribute): `T$63`?
    open fun get(attribute: InterleavedBufferAttribute): `T$63`?
    open fun get(attribute: GLBufferAttribute): `T$63`?
    open fun remove(attribute: BufferAttribute)
    open fun remove(attribute: InterleavedBufferAttribute)
    open fun remove(attribute: GLBufferAttribute)
    open fun update(attribute: BufferAttribute, bufferType: Number)
    open fun update(attribute: InterleavedBufferAttribute, bufferType: Number)
    open fun update(attribute: GLBufferAttribute, bufferType: Number)
}