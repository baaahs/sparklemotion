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