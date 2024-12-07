package three.addons

import three.DataTexture
import three.Loader__1
import three.LoadingManager

open external class IESLoader(manager: LoadingManager = definedExternally) : Loader__1<DataTexture> {
    open fun parse(text: String): DataTexture
}