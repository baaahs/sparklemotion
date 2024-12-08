package three.addons

import three.*

external object OUTPUT {
    val Default: OUTPUT
    val SAO: OUTPUT
    val Normal: OUTPUT
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

open external class SAOPass(scene: Scene, camera: Camera, resolution: Vector2 = definedExternally) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var originalClearColor: Color
    open var oldClearColor: Color
    open var oldClearAlpha: Number
    open var resolution: Vector2
    open var saoRenderTarget: WebGLRenderTarget<*>
    open var blurIntermediateRenderTarget: WebGLRenderTarget<*>
    open var normalRenderTarget: WebGLRenderTarget<*>
    open var normalMaterial: MeshNormalMaterial
    open var saoMaterial: ShaderMaterial
    open var vBlurMaterial: ShaderMaterial
    open var hBlurMaterial: ShaderMaterial
    open var materialCopy: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var params: SAOPassParams
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