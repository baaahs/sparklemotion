package three.addons

import three.ShaderMaterial
import three.`T$54`

external interface `T$88`

open external class ShaderPass(shader: Any?, textureID: String = definedExternally) : Pass {
    open var textureID: String
    open var uniforms: `T$88`
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}