package three.addons

import org.khronos.webgl.ArrayBuffer
import three.DataTexture
import three.DataTextureLoader
import three.LoadingManager

open external class TGALoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open fun parse(data: ArrayBuffer): DataTexture
}