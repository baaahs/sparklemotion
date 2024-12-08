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