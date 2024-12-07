package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Loader__1
import three.LoadingManager

open external class KMZLoader(manager: LoadingManager = definedExternally) : Loader__1<Collada> {
    open fun parse(data: ArrayBuffer): Collada
}