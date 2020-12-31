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

external interface WebGLRenderTargetOptions {
    var wrapS: Wrapping?
        get() = definedExternally
        set(value) = definedExternally
    var wrapT: Wrapping?
        get() = definedExternally
        set(value) = definedExternally
    var magFilter: TextureFilter?
        get() = definedExternally
        set(value) = definedExternally
    var minFilter: TextureFilter?
        get() = definedExternally
        set(value) = definedExternally
    var format: Number?
        get() = definedExternally
        set(value) = definedExternally
    var type: TextureDataType?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropy: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencilBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var generateMipmaps: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depthTexture: DepthTexture?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: TextureEncoding?
        get() = definedExternally
        set(value) = definedExternally
}

open external class WebGLRenderTarget(width: Number, height: Number, options: WebGLRenderTargetOptions = definedExternally) : EventDispatcher {
    open var uuid: String
    open var width: Number
    open var height: Number
    open var scissor: Vector4
    open var scissorTest: Boolean
    open var viewport: Vector4
    open var texture: Texture
    open var depthBuffer: Boolean
    open var stencilBuffer: Boolean
    open var depthTexture: DepthTexture
    open var isWebGLRenderTarget: Boolean
    open var wrapS: Any
    open var wrapT: Any
    open var magFilter: Any
    open var minFilter: Any
    open var anisotropy: Any
    open var offset: Any
    open var repeat: Any
    open var format: Any
    open var type: Any
    open var generateMipmaps: Any
    open fun setSize(width: Number, height: Number)
    open fun clone(): WebGLRenderTarget /* this */
    open fun copy(source: WebGLRenderTarget): WebGLRenderTarget /* this */
    open fun dispose()
}