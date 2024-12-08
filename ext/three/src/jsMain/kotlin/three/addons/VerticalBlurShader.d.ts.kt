package three.addons

import three.IUniform__0

external interface `T$159` {
    var tDiffuse: IUniform__0
    var v: IUniform__0
}

external object VerticalBlurShader {
    var uniforms: `T$159`
    var vertexShader: String
    var fragmentShader: String
}