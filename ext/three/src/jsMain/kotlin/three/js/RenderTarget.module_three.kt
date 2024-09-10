@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

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

external interface RenderTargetOptions {
    var wrapS: Any?
        get() = definedExternally
        set(value) = definedExternally
    var wrapT: Any?
        get() = definedExternally
        set(value) = definedExternally
    var magFilter: Any?
        get() = definedExternally
        set(value) = definedExternally
    var minFilter: Any?
        get() = definedExternally
        set(value) = definedExternally
    var generateMipmaps: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var format: Number?
        get() = definedExternally
        set(value) = definedExternally
    var type: Any?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropy: Number?
        get() = definedExternally
        set(value) = definedExternally
    var colorSpace: Any?
        get() = definedExternally
        set(value) = definedExternally
    var internalFormat: String? /* "ALPHA" | "RGB" | "RGBA" | "LUMINANCE" | "LUMINANCE_ALPHA" | "RED_INTEGER" | "R8" | "R8_SNORM" | "R8I" | "R8UI" | "R16I" | "R16UI" | "R16F" | "R32I" | "R32UI" | "R32F" | "RG8" | "RG8_SNORM" | "RG8I" | "RG8UI" | "RG16I" | "RG16UI" | "RG16F" | "RG32I" | "RG32UI" | "RG32F" | "RGB565" | "RGB8" | "RGB8_SNORM" | "RGB8I" | "RGB8UI" | "RGB16I" | "RGB16UI" | "RGB16F" | "RGB32I" | "RGB32UI" | "RGB32F" | "RGB9_E5" | "SRGB8" | "R11F_G11F_B10F" | "RGBA4" | "RGBA8" | "RGBA8_SNORM" | "RGBA8I" | "RGBA8UI" | "RGBA16I" | "RGBA16UI" | "RGBA16F" | "RGBA32I" | "RGBA32UI" | "RGBA32F" | "RGB5_A1" | "RGB10_A2" | "RGB10_A2UI" | "SRGB8_ALPHA8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" */
        get() = definedExternally
        set(value) = definedExternally
    var depthBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencilBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var resolveDepthBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var resolveStencilBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depthTexture: DepthTexture?
        get() = definedExternally
        set(value) = definedExternally
    var samples: Number?
        get() = definedExternally
        set(value) = definedExternally
    var count: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class RenderTarget<TTexture>(width: Number = definedExternally, height: Number = definedExternally, options: RenderTargetOptions = definedExternally) : EventDispatcher<`T$8`> {
    open val isRenderTarget: Boolean
    open var width: Number
    open var height: Number
    open var depth: Number
    open var scissor: Vector4
    open var scissorTest: Boolean
    open var viewport: Vector4
    open var textures: Array<TTexture>
    open var depthBuffer: Boolean
    open var stencilBuffer: Boolean
    open var resolveDepthBuffer: Boolean
    open var resolveStencilBuffer: Boolean
    open var depthTexture: DepthTexture?
    open var samples: Number
    open fun setSize(width: Number, height: Number, depth: Number = definedExternally)
    open fun clone(): RenderTarget<TTexture> /* this */
    open fun copy(source: RenderTarget__0): RenderTarget<TTexture> /* this */
    open fun dispose()
}

typealias RenderTarget__0 = RenderTarget<Texture>