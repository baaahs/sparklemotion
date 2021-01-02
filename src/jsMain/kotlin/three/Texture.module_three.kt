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

open external class Texture : EventDispatcher {
    constructor(image: HTMLImageElement = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(image: HTMLCanvasElement = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(image: HTMLVideoElement = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    open var id: Number
    open var uuid: String
    open var name: String
    open var sourceFile: String
    open var image: Any
    open var mipmaps: Array<Any>
    open var mapping: Mapping
    open var wrapS: Wrapping
    open var wrapT: Wrapping
    open var magFilter: TextureFilter
    open var minFilter: TextureFilter
    open var anisotropy: Number
    open var format: PixelFormat
    open var internalFormat: String /* 'ALPHA' | 'RGB' | 'RGBA' | 'LUMINANCE' | 'LUMINANCE_ALPHA' | 'RED_INTEGER' | 'R8' | 'R8_SNORM' | 'R8I' | 'R8UI' | 'R16I' | 'R16UI' | 'R16F' | 'R32I' | 'R32UI' | 'R32F' | 'RG8' | 'RG8_SNORM' | 'RG8I' | 'RG8UI' | 'RG16I' | 'RG16UI' | 'RG16F' | 'RG32I' | 'RG32UI' | 'RG32F' | 'RGB565' | 'RGB8' | 'RGB8_SNORM' | 'RGB8I' | 'RGB8UI' | 'RGB16I' | 'RGB16UI' | 'RGB16F' | 'RGB32I' | 'RGB32UI' | 'RGB32F' | 'RGB9_E5' | 'SRGB8' | 'R11F_G11F_B10F' | 'RGBA4' | 'RGBA8' | 'RGBA8_SNORM' | 'RGBA8I' | 'RGBA8UI' | 'RGBA16I' | 'RGBA16UI' | 'RGBA16F' | 'RGBA32I' | 'RGBA32UI' | 'RGBA32F' | 'RGB5_A1' | 'RGB10_A2' | 'RGB10_A2UI' | 'SRGB8_ALPHA8' | 'DEPTH_COMPONENT16' | 'DEPTH_COMPONENT24' | 'DEPTH_COMPONENT32F' | 'DEPTH24_STENCIL8' | 'DEPTH32F_STENCIL8' */
    open var type: TextureDataType
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
    open var encoding: TextureEncoding
    open var version: Number
    open var needsUpdate: Boolean
    open var isTexture: Boolean
    open var onUpdate: () -> Unit
    open fun clone(): Texture /* this */
    open fun copy(source: Texture): Texture /* this */
    open fun toJSON(meta: Any): Any
    open fun dispose()
    open fun transformUv(uv: Vector2): Vector2
    open fun updateMatrix()

    companion object {
        var DEFAULT_IMAGE: Any
        var DEFAULT_MAPPING: Any
    }
}