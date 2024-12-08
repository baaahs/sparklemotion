package three.addons

import three.IUniform__0

external interface `T$161` {
    var tDiffuse: IUniform__0
    var offset: IUniform__0
    var darkness: IUniform__0
}

external object VignetteShader {
    var uniforms: `T$161`
    var vertexShader: String
    var fragmentShader: String
}