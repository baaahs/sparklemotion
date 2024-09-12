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

external enum class SSAOPassOUTPUT {
    Default,
    SSAO,
    Blur,
    Depth,
    Normal
}

external open class SSAOPass(scene: Scene, camera: Camera, width: Number = definedExternally, height: Number = definedExternally, kernelSize: Number = definedExternally) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var width: Number
    open var height: Number
    override var clear: Boolean
    open var kernelRadius: Number
    open var kernel: Array<Vector3>
    open var noiseTexture: DataTexture
    open var output: SSAOPassOUTPUT
    open var minDistance: Number
    open var maxDistance: Number
    open var normalRenderTarget: WebGLRenderTarget__0
    open var ssaoRenderTarget: WebGLRenderTarget__0
    open var blurRenderTarget: WebGLRenderTarget__0
    open var ssaoMaterial: ShaderMaterial
    open var normalMaterial: MeshNormalMaterial
    open var blurMaterial: ShaderMaterial
    open var depthRenderMaterial: ShaderMaterial
    open var copyMaterial: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var originalClearColor: Color
    open fun dipose()
    open fun generateSampleKernel(kernelSize: Number): Array<Vector3>
    open fun generateRandomKernelRotations()
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