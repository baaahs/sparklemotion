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

external open class Data3DTexture : Texture {
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally)
    constructor()
    constructor(data: ArrayBufferView? = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally)
    constructor(data: ArrayBufferView? = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally)
    constructor(data: ArrayBuffer? = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open val isData3DTexture: Boolean
    override var magFilter: Any
    override var minFilter: Any
    open var wrapR: Any
    override var flipY: Boolean
    override var generateMipmaps: Boolean
    override var unpackAlignment: Number
}