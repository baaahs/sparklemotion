@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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