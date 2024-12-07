package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Group
import three.Loader__1
import three.LoadingManager

open external class ThreeMFLoader(manager: LoadingManager = definedExternally) : Loader__1<Group> {
    open var availableExtensions: Array<Any?>
    open fun parse(data: ArrayBuffer): Group
    open fun addExtension(extension: Any?)
}