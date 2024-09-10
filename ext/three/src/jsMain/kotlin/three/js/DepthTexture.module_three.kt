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

open external class DepthTexture(width: Number, height: Number, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally, format: Any = definedExternally) : Texture {
    open val isDepthTexture: Boolean
    override var flipY: Boolean
    override var magFilter: Any
    override var minFilter: Any
    override var generateMipmaps: Boolean
    override var format: Any
    override var type: Any
    open var compareFunction: Any?
}