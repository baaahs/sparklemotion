package three.addons

import three.Camera
import three.Scene
import three.WebGLRenderer

open external class PeppersGhostEffect(renderer: WebGLRenderer) {
    open var cameraDistance: Number
    open var reflectFromAbove: Boolean
    open fun render(scene: Scene, camera: Camera)
    open fun setSize(width: Number, height: Number)
}