package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Group
import three.Loader__1
import three.LoadingManager

open external class AMFLoader(manager: LoadingManager = definedExternally) : Loader__1<Group> {
    open fun parse(data: ArrayBuffer): Group
}