package three.addons

import org.khronos.webgl.ArrayBuffer
import three.DataTextureLoader
import three.LoadingManager

external interface RGBE {
    var width: Number
    var height: Number
    var data: dynamic /* Float32Array | Uint8Array */
        get() = definedExternally
        set(value) = definedExternally
    var header: String
    var gamma: Number
    var exposure: Number
    var type: Any
}

open external class RGBELoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open var type: Any
    open fun parse(buffer: ArrayBuffer): RGBE
    open fun setDataType(type: Any): RGBELoader /* this */
}