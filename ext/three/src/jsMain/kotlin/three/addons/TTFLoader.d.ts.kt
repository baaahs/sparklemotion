package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Loader__1
import three.LoadingManager

open external class TTFLoader(manager: LoadingManager = definedExternally) : Loader__1<FontData> {
    open var reversed: Boolean
    open fun parse(arraybuffer: ArrayBuffer): FontData
}