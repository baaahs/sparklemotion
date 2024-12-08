package three.addons

import three.IUniform__0

external interface `T$139` {
    var tDiffuse: IUniform__0
    var amount: IUniform__0
    var angle: IUniform__0
}

external object RGBShiftShader {
    var uniforms: `T$139`
    var vertexShader: String
    var fragmentShader: String
}