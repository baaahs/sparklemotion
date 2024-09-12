@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.ArrayBuffer
import three.AnimationClip
import three.BufferAttribute
import three.Loader__1
import three.LoadingManager

external interface MDD {
    var morphTargets: Array<BufferAttribute>
    var clip: AnimationClip
}

open external class MDDLoader(manager: LoadingManager = definedExternally) : Loader__1<MDD> {
    open fun parse(data: ArrayBuffer): MDD
}