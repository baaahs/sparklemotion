@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Camera
import three.Color
import three.Scene

open external class TAARenderPass : SSAARenderPass {
    constructor(scene: Scene, camera: Camera, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera)
    constructor(scene: Scene, camera: Camera, clearColor: Color = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: String = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: Number = definedExternally)
    open var accumulate: Boolean
}