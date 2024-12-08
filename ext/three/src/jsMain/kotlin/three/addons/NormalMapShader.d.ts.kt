package three.addons

import three.IUniform__0

external interface `T$137` {
    var heightMap: IUniform__0
    var resolution: IUniform__0
    var scale: IUniform__0
    var height: IUniform__0
}

external object NormalMapShader {
    var name: String
    var uniforms: `T$137`
    var vertexShader: String
    var fragmentShader: String
}