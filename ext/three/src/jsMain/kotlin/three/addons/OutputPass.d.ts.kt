package three.addons

import three.ShaderMaterial

open external class OutputPass : Pass {
    open var uniforms: Any?
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}