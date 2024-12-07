package three.addons

import js.objects.Record
import org.w3c.xhr.ProgressEvent
import three.Loader__1
import three.LoadingManager
import three.Shape

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

open external class FontLoader(manager: LoadingManager = definedExternally) : Loader__1<Font> {
    override fun load(url: String, onLoad: (data: Font) -> Unit, onProgress: (event: ProgressEvent) -> Unit, onError: (err: Any) -> Unit)
    open fun parse(json: FontData): Font
}

open external class Font(data: FontData) {
    open val isFont: Boolean
    open var type: String
    open var data: FontData
    open fun generateShapes(text: String, size: Number = definedExternally): Array<Shape>
}