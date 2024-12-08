package three.addons

import three.IUniform__0

external interface `T$163` {
    var color: IUniform__0
    var time: IUniform__0
    var tDiffuse: IUniform__0
    var tDudv: IUniform__0
    var textureMatrix: IUniform__0
}

external object WaterRefractionShader {
    var name: String
    var uniforms: `T$163`
    var vertexShader: String
    var fragmentShader: String
}