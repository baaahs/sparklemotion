package three.addons

import three.Camera
import three.Color
import three.Material
import three.Scene

open external class RenderPass(scene: Scene, camera: Camera, overrideMaterial: Material? = definedExternally, clearColor: Color? = definedExternally, clearAlpha: Number? = definedExternally) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var overrideMaterial: Material?
    open var clearColor: Color?
    open var clearAlpha: Number?
    open var clearDepth: Boolean
}