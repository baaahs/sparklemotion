@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Camera
import three.Scene

open external class MaskPass(scene: Scene, camera: Camera) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var inverse: Boolean
}

open external class ClearMaskPass : Pass