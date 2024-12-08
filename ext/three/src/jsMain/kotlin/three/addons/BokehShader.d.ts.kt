package three.addons

import three.IUniform__0

external interface `T$102` {
    var DEPTH_PACKING: Number
    var PERSPECTIVE_CAMERA: Number
}

external interface `T$103` {
    var tColor: IUniform__0
    var tDepth: IUniform__0
    var focus: IUniform__0
    var aspect: IUniform__0
    var aperture: IUniform__0
    var maxblur: IUniform__0
    var nearClip: IUniform__0
    var farClip: IUniform__0
}

external object BokehShader {
    var name: String
    var defines: `T$102`
    var uniforms: `T$103`
    var vertexShader: String
    var fragmentShader: String
}