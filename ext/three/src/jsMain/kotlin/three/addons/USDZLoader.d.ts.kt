@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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