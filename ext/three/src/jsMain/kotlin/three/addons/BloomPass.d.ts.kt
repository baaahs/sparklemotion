@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.ShaderMaterial
import three.WebGLRenderTarget

open external class BloomPass(strength: Number = definedExternally, kernelSize: Number = definedExternally, sigma: Number = definedExternally) : Pass {
    open var renderTargetX: WebGLRenderTarget<*>
    open var renderTargetY: WebGLRenderTarget<*>
    open var copyUniforms: Any?
    open var materialCopy: ShaderMaterial
    open var convolutionUniforms: Any?
    open var materialConvolution: ShaderMaterial
    open var fsQuad: FullScreenQuad
}