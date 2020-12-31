@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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