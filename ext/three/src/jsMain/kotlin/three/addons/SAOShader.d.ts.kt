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

external interface `T$140` {
    var NUM_SAMPLES: Number
    var NUM_RINGS: Number
    var DIFFUSE_TEXTURE: Number
    var PERSPECTIVE_CAMERA: Number
}

external interface `T$141` {
    var tDepth: IUniform__0
    var tDiffuse: IUniform__0
    var tNormal: IUniform__0
    var size: IUniform__0
    var cameraNear: IUniform__0
    var cameraFar: IUniform__0
    var cameraProjectionMatrix: IUniform__0
    var cameraInverseProjectionMatrix: IUniform__0
    var scale: IUniform__0
    var intensity: IUniform__0
    var bias: IUniform__0
    var minResolution: IUniform__0
    var kernelRadius: IUniform__0
    var randomSeed: IUniform__0
}

external object SAOShader {
    var name: String
    var defines: `T$140`
    var uniforms: `T$141`
    var vertexShader: String
    var fragmentShader: String
}