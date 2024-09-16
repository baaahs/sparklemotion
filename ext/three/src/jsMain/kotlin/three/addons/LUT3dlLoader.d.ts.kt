@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Data3DTexture
import three.Loader__1
import three.LoadingManager

external interface LUT3dlResult {
    var size: Number
    var texture3D: Data3DTexture
}

open external class LUT3dlLoader(manager: LoadingManager = definedExternally) : Loader__1<LUT3dlResult> {
    open var type: Any
    open fun setType(type: Any): LUT3dlLoader /* this */
    open fun parse(input: String): LUT3dlResult
}