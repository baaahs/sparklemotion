package three.addons

import three.ShaderMaterial
import three.WebGLRenderTarget

open external class SavePass(renderTarget: WebGLRenderTarget<*> = definedExternally) : Pass {
    open var textureID: String
    open var renderTarget: WebGLRenderTarget<*>
    open var uniforms: Any?
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}