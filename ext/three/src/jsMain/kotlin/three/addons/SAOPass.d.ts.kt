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

external enum class OUTPUT {
    Default,
    SAO,
    Normal
}

external interface SAOPassParams {
    var output: OUTPUT
    var saoBias: Number
    var saoIntensity: Number
    var saoScale: Number
    var saoKernelRadius: Number
    var saoMinResolution: Number
    var saoBlur: Boolean
    var saoBlurRadius: Number
    var saoBlurStdDev: Number
    var saoBlurDepthCutoff: Number
}

external open class SAOPass(scene: Scene, camera: Camera, resolution: Vector2 = definedExternally) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var originalClearColor: Color
    open var oldClearColor: Color
    open var oldClearAlpha: Number
    open var resolution: Vector2
    open var saoRenderTarget: WebGLRenderTarget__0
    open var blurIntermediateRenderTarget: WebGLRenderTarget__0
    open var normalRenderTarget: WebGLRenderTarget__0
    open var normalMaterial: MeshNormalMaterial
    open var saoMaterial: ShaderMaterial
    open var vBlurMaterial: ShaderMaterial
    open var hBlurMaterial: ShaderMaterial
    open var materialCopy: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var params: SAOPassParams
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Color = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: String = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Color = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: String = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget__0, clearColor: Number = definedExternally)

    companion object {
        var OUTPUT: Any
    }
}