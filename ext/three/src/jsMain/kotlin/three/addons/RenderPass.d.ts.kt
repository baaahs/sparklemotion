@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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