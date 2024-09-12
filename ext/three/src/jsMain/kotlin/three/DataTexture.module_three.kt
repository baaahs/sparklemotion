@file:JsModule("three")
@file:JsNonModule
package three

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView

open external class DataTexture : Texture {
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor()
    constructor(data: ArrayBufferView? = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, format: Any = definedExternally, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally)
    open val isDataTexture: Boolean
    override var magFilter: Any
    override var minFilter: Any
    override var flipY: Boolean
    override var generateMipmaps: Boolean
    override var unpackAlignment: Number
}