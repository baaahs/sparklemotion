@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

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