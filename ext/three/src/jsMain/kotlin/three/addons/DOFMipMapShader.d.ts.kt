package three.addons

import three.IUniform__0

external interface `T$112` {
    var tColor: IUniform__0
    var tDepth: IUniform__0
    var focus: IUniform__0
    var maxblur: IUniform__0
}

external object DOFMipMapShader {
    var name: String
    var uniforms: `T$112`
    var vertexShader: String
    var fragmentShader: String
}