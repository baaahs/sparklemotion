package three.addons

import three.Camera
import three.Scene

open external class MaskPass(scene: Scene, camera: Camera) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var inverse: Boolean
}

open external class ClearMaskPass : Pass