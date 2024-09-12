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

external interface `T$109` {
    var KERNEL_RADIUS: Number
    var DEPTH_PACKING: Number
    var PERSPECTIVE_CAMERA: Number
}

external interface `T$110` {
    var tDiffuse: IUniform__0
    var size: IUniform__0
    var sampleUvOffsets: IUniform__0
    var sampleWeights: IUniform__0
    var tDepth: IUniform__0
    var cameraNear: IUniform__0
    var cameraFar: IUniform__0
    var depthCutoff: IUniform__0
}

external object DepthLimitedBlurShader {
    var name: String
    var defines: `T$109`
    var uniforms: `T$110`
    var vertexShader: String
    var fragmentShader: String
}

external object BlurShaderUtils {
    fun createSampleWeights(kernelRadius: Number, stdDev: Number): Array<Number>
    fun createSampleOffsets(kernelRadius: Number, uvIncrement: Vector2): Array<Vector2>
    fun configure(configure: Material, kernelRadius: Number, stdDev: Number, uvIncrement: Vector2)
}