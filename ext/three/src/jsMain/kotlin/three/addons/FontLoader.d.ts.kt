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

external interface `T$71` {
    var ha: Number
    var x_min: Number
    var x_max: Number
    var o: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$72` {
    var yMin: Number
    var xMin: Number
    var yMax: Number
    var xMax: Number
}

external interface FontData {
    var glyphs: Record<String, `T$71`>
    var familyName: String
    var ascender: Number
    var descender: Number
    var underlinePosition: Number
    var underlineThickness: Number
    var boundingBox: `T$72`
    var resolution: Number
    var original_font_information: Record<String, String>
}

external open class FontLoader(manager: LoadingManager = definedExternally) : Loader__1<Font> {
    override fun load(url: String, onLoad: (data: Font) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit, onError: (err: Any) -> Unit)
    open fun parse(json: FontData): Font
}

external open class Font(data: FontData) {
    open val isFont: Boolean
    open var type: String
    open var data: FontData
    open fun generateShapes(text: String, size: Number = definedExternally): Array<Shape>
}