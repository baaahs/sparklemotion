package three.addons

import three.IUniform__0

external interface `T$106` {
    var tDiffuse: IUniform__0
    var color: IUniform__0
}

external object ColorifyShader {
    var uniforms: `T$106`
    var vertexShader: String
    var fragmentShader: String
}