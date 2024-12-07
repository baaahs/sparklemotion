package three.addons

import org.khronos.webgl.ArrayBuffer
import three.DataTexture
import three.Loader__1
import three.LoadingManager

external interface UltraHDRLoaderTextureData {
    var width: Number
    var height: Number
    var hdrBuffer: dynamic /* Uint16Array | Float32Array */
        get() = definedExternally
        set(value) = definedExternally
    var format: Any
    var type: Any
}

open external class UltraHDRLoader(manager: LoadingManager = definedExternally) : Loader__1<DataTexture> {
    open var type: Any
    open fun setDataType(value: Any): UltraHDRLoader /* this */
    open fun parse(buffer: ArrayBuffer, onLoad: (texData: UltraHDRLoaderTextureData) -> Unit)
}