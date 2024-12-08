package three.addons

import three.IUniform__0

external interface `T$157` {
    var texture: IUniform__0
    var delta: IUniform__0
}

external object TriangleBlurShader {
    var name: String
    var uniforms: `T$157`
    var vertexShader: String
    var fragmentShader: String
}