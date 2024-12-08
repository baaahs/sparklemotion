package three.addons

import three.IUniform__0

external interface `T$147` {
    var PERSPECTIVE_CAMERA: Number
    var KERNEL_SIZE: Number
}

external interface `T$148` {
    var tNormal: IUniform__0
    var tDepth: IUniform__0
    var tNoise: IUniform__0
    var kernel: IUniform__0
    var cameraNear: IUniform__0
    var cameraFar: IUniform__0
    var resolution: IUniform__0
    var cameraProjectionMatrix: IUniform__0
    var cameraInverseProjectionMatrix: IUniform__0
    var kernelRadius: IUniform__0
    var minDistance: IUniform__0
    var maxDistance: IUniform__0
}

external object SSAOShader {
    var name: String
    var defines: `T$147`
    var uniforms: `T$148`
    var vertexShader: String
    var fragmentShader: String
}

external object SSAODepthShader {
    var name: String
    var defines: `T$126`
    var uniforms: `T$127`
    var vertexShader: String
    var fragmentShader: String
}

external object SSAOBlurShader {
    var name: String
    var uniforms: `T$118`
    var vertexShader: String
    var fragmentShader: String
}