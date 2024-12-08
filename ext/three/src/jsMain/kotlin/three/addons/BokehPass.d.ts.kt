package three.addons

import three.Camera
import three.Color
import three.MeshDepthMaterial
import three.Scene
import three.ShaderMaterial
import three.WebGLRenderTarget

external interface BokehPassParamters {
    var focus: Number?
        get() = definedExternally
        set(value) = definedExternally
    var aspect: Number?
        get() = definedExternally
        set(value) = definedExternally
    var aperture: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxblur: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class BokehPass(scene: Scene, camera: Camera, params: BokehPassParamters) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var renderTargetColor: WebGLRenderTarget<*>
    open var renderTargetDepth: WebGLRenderTarget<*>
    open var materialDepth: MeshDepthMaterial
    open var materialBokeh: ShaderMaterial
    open var uniforms: Any?
    open var fsQuad: FullScreenQuad
    open var oldClearColor: Color
}