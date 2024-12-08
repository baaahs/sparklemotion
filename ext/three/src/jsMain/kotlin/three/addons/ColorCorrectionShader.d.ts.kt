package three.addons

import three.IUniform__0

external interface `T$105` {
    var tDiffuse: IUniform__0
    var powRGB: IUniform__0
    var mulRGB: IUniform__0
    var addRGB: IUniform__0
}

external object ColorCorrectionShader {
    var name: String
    var uniforms: `T$105`
    var vertexShader: String
    var fragmentShader: String
}