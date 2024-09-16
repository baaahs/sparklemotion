@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.DataTexture
import three.ShaderMaterial

open external class GlitchPass(dt_size: Number = definedExternally) : Pass {
    open var uniforms: Any?
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var goWild: Boolean
    open var curF: Number
    open var randX: Number
    open fun generateTrigger()
    open fun generateHeightmap(dt_size: Number): DataTexture
}