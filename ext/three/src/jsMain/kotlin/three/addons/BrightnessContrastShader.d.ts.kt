package three.addons

import three.IUniform__0

external interface `T$104` {
    var tDiffuse: IUniform__0
    var brightness: IUniform__0
    var contrast: IUniform__0
}

external object BrightnessContrastShader {
    var name: String
    var uniforms: `T$104`
    var vertexShader: String
    var fragmentShader: String
}