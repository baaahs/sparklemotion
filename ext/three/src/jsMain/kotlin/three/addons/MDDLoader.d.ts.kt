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