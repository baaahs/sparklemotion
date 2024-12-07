package three.addons

import three.Camera
import three.Scene
import three.WebGLRenderer

open external class StereoEffect(renderer: WebGLRenderer) {
    open fun setEyeSeparation(eyeSep: Number)
    open fun render(scene: Scene, camera: Camera)
    open fun setSize(width: Number, height: Number)
}