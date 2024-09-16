@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Camera
import three.Scene
import three.WebGLRenderer

open external class StereoEffect(renderer: WebGLRenderer) {
    open fun setEyeSeparation(eyeSep: Number)
    open fun render(scene: Scene, camera: Camera)
    open fun setSize(width: Number, height: Number)
}