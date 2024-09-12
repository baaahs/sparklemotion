@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform
import three.Texture

external interface `T$98` {
    var tDiffuse: IUniform<Texture>
    var exposure: IUniform<Number>
}

external object ACESFilmicToneMappingShader {
    var name: String
    var uniforms: `T$98`
    var vertexShader: String
    var fragmentShader: String
}