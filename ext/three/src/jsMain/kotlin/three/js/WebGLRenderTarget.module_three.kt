@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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