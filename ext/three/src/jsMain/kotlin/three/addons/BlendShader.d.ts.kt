package three.addons

import three.IUniform__0

external interface `T$101` {
    var tDiffuse1: IUniform__0
    var tDiffuse2: IUniform__0
    var mixRatio: IUniform__0
    var opacity: IUniform__0
}

external object BlendShader {
    var name: String
    var uniforms: `T$101`
    var vertexShader: String
    var fragmentShader: String
}