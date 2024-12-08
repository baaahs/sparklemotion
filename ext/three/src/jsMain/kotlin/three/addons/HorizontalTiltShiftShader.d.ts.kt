package three.addons

import three.IUniform__0

external interface `T$131` {
    var tDiffuse: IUniform__0
    var h: IUniform__0
    var r: IUniform__0
}

external object HorizontalTiltShiftShader {
    var name: String
    var uniforms: `T$131`
    var vertexShader: String
    var fragmentShader: String
}