package three.addons

import org.khronos.webgl.Uint8Array
import three.DataTextureLoader
import three.LoadingManager

external interface TIFFResult {
    var width: Number
    var height: Number
    var data: Uint8Array
    var flipY: Boolean
    var magFilter: Any
    var minFilter: Any
}

open external class TIFFLoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open fun parse(buffer: Iterable<Number>): TIFFResult
}