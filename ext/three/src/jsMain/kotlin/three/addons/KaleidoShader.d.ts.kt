package three.addons

import three.IUniform__0

external interface `T$133` {
    var tDiffuse: IUniform__0
    var sides: IUniform__0
    var angle: IUniform__0
}

external object KaleidoShader {
    var name: String
    var uniforms: `T$133`
    var vertexShader: String
    var fragmentShader: String
}