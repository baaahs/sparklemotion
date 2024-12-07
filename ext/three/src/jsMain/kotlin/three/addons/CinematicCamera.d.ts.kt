package three.addons

import three.OrthographicCamera
import three.PerspectiveCamera
import three.Scene
import three.ShaderMaterial
import three.WebGLRenderTarget
import three.WebGLRenderer

external interface `T$43` {
    var enabled: Boolean
    var scene: Scene
    var camera: OrthographicCamera
    var rtTextureDepth: WebGLRenderTarget<*>
    var rtTextureColor: WebGLRenderTarget<*>
    var bokeh_uniforms: BokehShaderUniforms
}

external interface `T$44` {
    var rings: Number
    var samples: Number
}

open external class CinematicCamera(fov: Number, aspect: Number, near: Number, far: Number) : PerspectiveCamera {
    open var postprocessing: `T$43`
    open var shaderSettings: `T$44`
    open var materialDepth: ShaderMaterial
    open var coc: Number
    open var aperture: Number
    open var fNumber: Number
    open var hyperFocal: Number
    override var filmGauge: Number
    open fun linearize(depth: Number): Number
    open fun smoothstep(near: Number, far: Number, depth: Number): Number
    open fun saturate(x: Number): Number
    open fun focusAt(focusDistance: Number)
    open fun initPostProcessing()
    open fun renderCinematic(scene: Scene, renderer: WebGLRenderer)
    open fun setLens(focalLength: Number, frameHeight: Number = definedExternally, fNumber: Number = definedExternally, coc: Number = definedExternally)
}