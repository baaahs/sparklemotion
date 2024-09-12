@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class SSAARenderPass : Pass {
    constructor(scene: Scene, camera: Camera, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera)
    constructor(scene: Scene, camera: Camera, clearColor: Color = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: String = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: Number = definedExternally)
    open var scene: Scene
    open var camera: Camera
    open var sampleLevel: Number
    open var unbiased: Boolean
    open var stencilBuffer: Boolean
    open var clearColor: dynamic /* Color | String | Number */
    open var clearAlpha: Number
    open var copyUniforms: Any?
    open var copyMaterial: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var sampleRenderTarget: WebGLRenderTarget<*>?
}