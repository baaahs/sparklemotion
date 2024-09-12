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

external interface `T$32` {
    var buffer: WebGLBuffer
    var type: Number
    var bytesPerElement: Number
    var version: Number
    var size: Number
}

external open class WebGLAttributes {
    constructor(gl: WebGLRenderingContext)
    constructor(gl: WebGL2RenderingContext)
    open fun get(attribute: BufferAttribute): `T$32`?
    open fun get(attribute: InterleavedBufferAttribute): `T$32`?
    open fun get(attribute: GLBufferAttribute): `T$32`?
    open fun remove(attribute: BufferAttribute)
    open fun remove(attribute: InterleavedBufferAttribute)
    open fun remove(attribute: GLBufferAttribute)
    open fun update(attribute: BufferAttribute, bufferType: Number)
    open fun update(attribute: InterleavedBufferAttribute, bufferType: Number)
    open fun update(attribute: GLBufferAttribute, bufferType: Number)
}