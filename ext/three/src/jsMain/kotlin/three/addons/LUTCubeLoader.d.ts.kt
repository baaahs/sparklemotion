@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Data3DTexture
import three.Loader__1
import three.LoadingManager
import three.Vector3

external interface LUTCubeResult {
    var title: String
    var size: Number
    var domainMin: Vector3
    var domainMax: Vector3
    var texture3D: Data3DTexture
}

open external class LUTCubeLoader(manager: LoadingManager = definedExternally) : Loader__1<LUTCubeResult> {
    open var type: Any
    open fun setType(type: Any): LUTCubeLoader /* this */
    open fun parse(input: String): LUTCubeResult
}