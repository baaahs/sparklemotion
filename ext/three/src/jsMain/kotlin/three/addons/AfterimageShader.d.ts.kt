package three.addons

import three.IUniform__0

external interface `T$99` {
    var damp: IUniform__0
    var tOld: IUniform__0
    var tNew: IUniform__0
}

external object AfterimageShader {
    var name: String
    var uniforms: `T$99`
    var vertexShader: String
    var fragmentShader: String
}