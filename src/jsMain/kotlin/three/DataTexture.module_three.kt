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

open external class DataTexture : Texture {
    constructor(data: Int8Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Uint8Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Uint8ClampedArray, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Int16Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Uint16Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Int32Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Uint32Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Float32Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    constructor(data: Float64Array, width: Number, height: Number, format: PixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally)
    override var image: ImageData
    override var flipY: Boolean
    override var generateMipmaps: Boolean
    override var unpackAlignment: Number
    override var format: PixelFormat
}