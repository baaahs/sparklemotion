package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Group
import three.Loader__1
import three.LoadingManager

open external class FBXLoader(manager: LoadingManager = definedExternally) : Loader__1<Group> {
    open fun parse(FBXBuffer: ArrayBuffer, path: String): Group
    open fun parse(FBXBuffer: String, path: String): Group
}