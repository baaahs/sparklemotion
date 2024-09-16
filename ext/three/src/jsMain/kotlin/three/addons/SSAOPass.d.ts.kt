@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

external enum class SSAOPassOUTPUT {
    Default,
    SSAO,
    Blur,
    Depth,
    Normal
}

open external class SSAOPass(scene: Scene, camera: Camera, width: Number = definedExternally, height: Number = definedExternally, kernelSize: Number = definedExternally) : Pass {
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
    open var normalRenderTarget: WebGLRenderTarget<*>
    open var ssaoRenderTarget: WebGLRenderTarget<*>
    open var blurRenderTarget: WebGLRenderTarget<*>
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
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Color = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: String = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Color = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: String = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: Number = definedExternally)

    companion object {
        var OUTPUT: Any
    }
}