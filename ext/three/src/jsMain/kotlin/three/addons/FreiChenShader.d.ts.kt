package three.addons

import three.IUniform__0

external interface `T$117` {
    var tDiffuse: IUniform__0
    var aspect: IUniform__0
}

external object FreiChenShader {
    var name: String
    var uniforms: `T$117`
    var vertexShader: String
    var fragmentShader: String
}