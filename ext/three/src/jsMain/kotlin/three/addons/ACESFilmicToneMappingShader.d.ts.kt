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