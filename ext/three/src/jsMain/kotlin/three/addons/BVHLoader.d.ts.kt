package three.addons

import three.AnimationClip
import three.Loader__1
import three.LoadingManager
import three.Skeleton

external interface BVH {
    var clip: AnimationClip
    var skeleton: Skeleton
}

open external class BVHLoader(manager: LoadingManager = definedExternally) : Loader__1<BVH> {
    open var animateBonePositions: Boolean
    open var animateBoneRotations: Boolean
    open fun parse(text: String): BVH
}