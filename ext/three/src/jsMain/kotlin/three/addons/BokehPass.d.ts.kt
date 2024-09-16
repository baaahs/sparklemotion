@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

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