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

open external class CubeTexture(images: Array<Any> = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally) : Texture {
    open val isCubeTexture: Boolean
    override var mapping: Any
    override var flipY: Boolean
}