package three.addons

import three.ShaderMaterial
import three.Texture
import three.WebGLRenderTarget

open external class SMAAPass(width: Number, height: Number) : Pass {
    open var edgesRT: WebGLRenderTarget<*>
    open var weightsRT: WebGLRenderTarget<*>
    open var areaTexture: Texture
    open var searchTexture: Texture
    open var uniformsEdges: Any?
    open var materialEdges: ShaderMaterial
    open var uniformsWeights: Any?
    open var materialWeights: ShaderMaterial
    open var uniformsBlend: Any?
    open var materialBlend: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open fun getAreaTexture(): String
    open fun getSearchTexture(): String
}