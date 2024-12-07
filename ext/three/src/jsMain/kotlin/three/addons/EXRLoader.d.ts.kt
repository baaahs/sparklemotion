package three.addons

import org.khronos.webgl.ArrayBuffer
import three.DataTextureLoader
import three.LoadingManager

external interface EXR {
    var header: Any?
    var width: Number
    var height: Number
    var data: dynamic /* Float32Array | Uint16Array */
        get() = definedExternally
        set(value) = definedExternally
    var format: Any
    var colorSpace: Any
    var type: Any
}

open external class EXRLoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open var type: Any
    open fun parse(buffer: ArrayBuffer): EXR
    open fun setDataType(type: Any): EXRLoader /* this */
}