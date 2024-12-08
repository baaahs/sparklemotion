package three.addons

import three.IUniform__0

external interface `T$111` {
    var tDiffuse: IUniform__0
    var tDisp: IUniform__0
    var byp: IUniform__0
    var amount: IUniform__0
    var angle: IUniform__0
    var seed: IUniform__0
    var seed_x: IUniform__0
    var seed_y: IUniform__0
    var distortion_x: IUniform__0
    var distortion_y: IUniform__0
    var col_s: IUniform__0
}

external object DigitalGlitch {
    var uniforms: `T$111`
    var vertexShader: String
    var fragmentShader: String
}