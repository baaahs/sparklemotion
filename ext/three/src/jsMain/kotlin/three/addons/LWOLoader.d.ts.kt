@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Loader__1
import three.LoadingManager
import three.Material
import three.Object3D

external interface LWO {
    var materials: Array<Material>
    var meshes: Array<Object3D>
}

external interface LWOLoaderParameters {
    var resourcePath: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LWOLoader(manager: LoadingManager = definedExternally, parameters: LWOLoaderParameters = definedExternally) : Loader__1<LWO> {
    open fun parse(data: ArrayBuffer, path: String, modelName: String): LWO
}