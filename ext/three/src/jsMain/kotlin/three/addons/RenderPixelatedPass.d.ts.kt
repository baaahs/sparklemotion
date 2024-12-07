package three.addons

import three.*

external interface RenderPixelatedPassParameters {
    var normalEdgeStrength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthEdgeStrength: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class RenderPixelatedPass(pixelSize: Number, scene: Scene, camera: Camera, options: RenderPixelatedPassParameters = definedExternally) : Pass {
    open var pixelSize: Number
    open var resolution: Vector2
    open var renderResolution: Vector2
    open var pixelatedMaterial: ShaderMaterial
    open var normalMaterial: MeshNormalMaterial
    open var fsQuad: FullScreenQuad
    open var scene: Scene
    open var camera: Camera
    open var normalEdgeStrength: Number
    open var depthEdgeStrength: Number
    open var beautyRenderTarget: WebGLRenderTarget<*>
    open var normalRenderTarget: WebGLRenderTarget<*>
    open fun setPixelSize(pixelSize: Number)
}