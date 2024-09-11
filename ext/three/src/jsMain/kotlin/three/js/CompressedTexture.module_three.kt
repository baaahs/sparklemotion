@file:JsModule("three")
@file:JsNonModule
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

external interface CompressedTextureMipmap {
    var data: dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */
        get() = definedExternally
        set(value) = definedExternally
    var width: Number
    var height: Number
}

open external class CompressedTexture(mipmaps: Array<CompressedTextureMipmap> = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally) : Texture {
    open val isCompressedTexture: Boolean
    override var mipmaps: Array<CompressedTextureMipmap>?
    override var format: Any
    override var flipY: Boolean
    override var generateMipmaps: Boolean
}