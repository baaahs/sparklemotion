package three.addons

import three.Camera
import three.Scene
import three.WebGLRenderer

open external class ParallaxBarrierEffect(renderer: WebGLRenderer) {
    open var setSize: (width: Number, height: Number) -> Unit
    open var render: (scene: Scene, camera: Camera) -> Unit
    open var dispose: () -> Unit
}