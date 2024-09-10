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

external interface TextureImageData {
    var data: dynamic /* Uint8Array | Uint8ClampedArray */
        get() = definedExternally
        set(value) = definedExternally
    var height: Number
    var width: Number
}

external interface Texture3DImageData : TextureImageData {
    var depth: Number
}