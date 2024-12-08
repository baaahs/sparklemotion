package three.addons

import org.w3c.xhr.ProgressEvent
import three.CubeTexture
import three.Loader
import three.LoadingManager

open external class HDRCubeTextureLoader(manager: LoadingManager = definedExternally) : Loader<CubeTexture, Array<String>> {
    open var hdrLoader: RGBELoader
    open var type: Any
//    open fun load(url: Array<String>, onLoad: (data: CubeTexture) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (err: Any) -> Unit = definedExternally): CubeTexture
    open fun setDataType(type: Any): HDRCubeTextureLoader /* this */
}