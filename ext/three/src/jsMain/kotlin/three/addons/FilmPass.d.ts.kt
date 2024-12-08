package three.addons

import three.ShaderMaterial

open external class FilmPass(intensity: Number = definedExternally, grayscale: Boolean = definedExternally) : Pass {
    open var uniforms: Any?
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}