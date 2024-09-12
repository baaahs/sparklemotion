@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import js.objects.Record
import three.IUniform__0
import three.ShaderMaterial
import three.WebGLRenderTarget

open external class AfterimagePass(damp: Number = definedExternally) : Pass {
    open var shader: Any?
    open var uniforms: Record<String, IUniform__0>
    open var textureComp: WebGLRenderTarget<*>
    open var textureOld: WebGLRenderTarget<*>
    open var shaderMaterial: ShaderMaterial
    open var compFsQuad: FullScreenQuad
    open var copyFsQuad: FullScreenQuad
}