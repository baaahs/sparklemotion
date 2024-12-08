package three.addons

import three.IUniform__0

external interface `T$115` {
    var tDiffuse: IUniform__0
    var time: IUniform__0
    var intensity: IUniform__0
    var grayscale: IUniform__0
}

external object FilmShader {
    var uniforms: `T$115`
    var vertexShader: String
    var fragmentShader: String
}