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

external interface TextureJSON {
    var metadata: `T$0`
    var uuid: String
    var name: String
    var image: String
    var mapping: Any
    var channel: Number
    var repeat: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var offset: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var center: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var rotation: Number
    var wrap: dynamic /* JsTuple<wrapS, Number, wrapT, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var format: Any
    var internalFormat: String /* "ALPHA" | "RGB" | "RGBA" | "LUMINANCE" | "LUMINANCE_ALPHA" | "RED_INTEGER" | "R8" | "R8_SNORM" | "R8I" | "R8UI" | "R16I" | "R16UI" | "R16F" | "R32I" | "R32UI" | "R32F" | "RG8" | "RG8_SNORM" | "RG8I" | "RG8UI" | "RG16I" | "RG16UI" | "RG16F" | "RG32I" | "RG32UI" | "RG32F" | "RGB565" | "RGB8" | "RGB8_SNORM" | "RGB8I" | "RGB8UI" | "RGB16I" | "RGB16UI" | "RGB16F" | "RGB32I" | "RGB32UI" | "RGB32F" | "RGB9_E5" | "SRGB8" | "R11F_G11F_B10F" | "RGBA4" | "RGBA8" | "RGBA8_SNORM" | "RGBA8I" | "RGBA8UI" | "RGBA16I" | "RGBA16UI" | "RGBA16F" | "RGBA32I" | "RGBA32UI" | "RGBA32F" | "RGB5_A1" | "RGB10_A2" | "RGB10_A2UI" | "SRGB8_ALPHA8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" */
    var type: Any
    var colorSpace: Any
    var minFilter: Any
    var magFilter: Any
    var anisotropy: Number
    var flipY: Boolean
    var generateMipmaps: Boolean
    var premultiplyAlpha: Boolean
    var unpackAlignment: Number
    var userData: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface OffscreenCanvas : EventTarget

external open class Texture : EventDispatcher<`T$8`> {
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor()
    constructor(image: ImageBitmap = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(image: ImageBitmap = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor(image: ImageData = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(image: ImageData = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(image: HTMLImageElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(image: OffscreenCanvas = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(image: ImageBitmap, mapping: Any, wrapS: Any, wrapT: Any, magFilter: Any, minFilter: Any, format: Any, type: Any, anisotropy: Number)
    constructor(image: ImageData, mapping: Any, wrapS: Any, wrapT: Any, magFilter: Any, minFilter: Any, format: Any, type: Any, anisotropy: Number)
    constructor(image: HTMLImageElement, mapping: Any, wrapS: Any, wrapT: Any, magFilter: Any, minFilter: Any, format: Any, type: Any, anisotropy: Number)
    constructor(image: HTMLCanvasElement, mapping: Any, wrapS: Any, wrapT: Any, magFilter: Any, minFilter: Any, format: Any, type: Any, anisotropy: Number)
    constructor(image: HTMLVideoElement, mapping: Any, wrapS: Any, wrapT: Any, magFilter: Any, minFilter: Any, format: Any, type: Any, anisotropy: Number)
    constructor(image: OffscreenCanvas, mapping: Any, wrapS: Any, wrapT: Any, magFilter: Any, minFilter: Any, format: Any, type: Any, anisotropy: Number)
    open val isTexture: Boolean
    open val id: Number
    open var uuid: String
    open var name: String
    open var source: Source
    open var mipmaps: dynamic /* Array<CompressedTextureMipmap>? | Array<CubeTexture>? | Array<HTMLCanvasElement>? */
    open var mapping: Any
    open var channel: Number
    open var wrapS: Any
    open var wrapT: Any
    open var magFilter: Any
    open var minFilter: Any
    open var anisotropy: Number
    open var format: Any
    open var type: Any
    open var internalFormat: String /* "ALPHA" | "RGB" | "RGBA" | "LUMINANCE" | "LUMINANCE_ALPHA" | "RED_INTEGER" | "R8" | "R8_SNORM" | "R8I" | "R8UI" | "R16I" | "R16UI" | "R16F" | "R32I" | "R32UI" | "R32F" | "RG8" | "RG8_SNORM" | "RG8I" | "RG8UI" | "RG16I" | "RG16UI" | "RG16F" | "RG32I" | "RG32UI" | "RG32F" | "RGB565" | "RGB8" | "RGB8_SNORM" | "RGB8I" | "RGB8UI" | "RGB16I" | "RGB16UI" | "RGB16F" | "RGB32I" | "RGB32UI" | "RGB32F" | "RGB9_E5" | "SRGB8" | "R11F_G11F_B10F" | "RGBA4" | "RGBA8" | "RGBA8_SNORM" | "RGBA8I" | "RGBA8UI" | "RGBA16I" | "RGBA16UI" | "RGBA16F" | "RGBA32I" | "RGBA32UI" | "RGBA32F" | "RGB5_A1" | "RGB10_A2" | "RGB10_A2UI" | "SRGB8_ALPHA8" | "DEPTH_COMPONENT16" | "DEPTH_COMPONENT24" | "DEPTH_COMPONENT32F" | "DEPTH24_STENCIL8" | "DEPTH32F_STENCIL8" */
    open var matrix: Matrix3
    open var matrixAutoUpdate: Boolean
    open var offset: Vector2
    open var repeat: Vector2
    open var center: Vector2
    open var rotation: Number
    open var generateMipmaps: Boolean
    open var premultiplyAlpha: Boolean
    open var flipY: Boolean
    open var unpackAlignment: Number
    open var colorSpace: Any
    open var isRenderTargetTexture: Boolean
    open var userData: Record<String, Any>
    open var version: Number
    open var pmremVersion: Number
    open var onUpdate: () -> Unit
    open fun transformUv(uv: Vector2): Vector2
    open fun updateMatrix()
    open fun clone(): Texture /* this */
    open fun copy(source: Texture): Texture /* this */
    open fun toJSON(meta: String = definedExternally): TextureJSON
    open fun toJSON(): TextureJSON
    open fun toJSON(meta: Any = definedExternally): TextureJSON
    open fun dispose()

    companion object {
        var DEFAULT_ANISOTROPY: Number
        var DEFAULT_IMAGE: Any
        var DEFAULT_MAPPING: Any
    }
}