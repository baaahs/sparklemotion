@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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