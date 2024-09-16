@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class UnrealBloomPass(resolution: Vector2, strength: Number, radius: Number, threshold: Number) : Pass {
    open var resolution: Vector2
    open var strength: Number
    open var radius: Number
    open var threshold: Number
    open var clearColor: Color
    open var renderTargetsHorizontal: Array<WebGLRenderTarget<*>>
    open var renderTargetsVertical: Array<WebGLRenderTarget<*>>
    open var nMips: Number
    open var renderTargetBright: WebGLRenderTarget<*>
    open var highPassUniforms: Any?
    open var materialHighPassFilter: ShaderMaterial
    open var separableBlurMaterials: Array<ShaderMaterial>
    open var compositeMaterial: ShaderMaterial
    open var bloomTintColors: Array<Vector3>
    open var copyUniforms: Any?
    open var blendMaterial: ShaderMaterial
    open var oldClearColor: Color
    open var oldClearAlpha: Number
    open var basic: MeshBasicMaterial
    open var fsQuad: FullScreenQuad
    override fun dispose()
    open fun getSeperableBlurMaterial(): ShaderMaterial
    open fun getCompositeMaterial(): ShaderMaterial
}