package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Group
import three.Loader__1
import three.LoadingManager
import three.Mesh

open external class USDAParser {
    open fun parse(text: String): Any?
}

open external class USDZLoader(manager: LoadingManager = definedExternally) : Loader__1<Mesh<*, *>> {
    open fun parse(buffer: ArrayBuffer): Group
    open fun parse(buffer: String): Group
}