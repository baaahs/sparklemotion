package three.addons

import three.IUniform
import three.Texture

external interface `T$114` {
    var tDiffuse: IUniform<Texture?>
    var exposure: IUniform<Number>
}

external object ExposureShader {
    var name: String /* "ExposureShader" */
    var uniforms: `T$114`
    var vertexShader: String
    var fragmentShader: String
}