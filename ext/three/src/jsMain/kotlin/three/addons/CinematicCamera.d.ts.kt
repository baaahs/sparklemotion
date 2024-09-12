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

external interface `T$43` {
    var enabled: Boolean
    var scene: Scene
    var camera: OrthographicCamera
    var rtTextureDepth: WebGLRenderTarget__0
    var rtTextureColor: WebGLRenderTarget__0
    var bokeh_uniforms: BokehShaderUniforms
}

external interface `T$44` {
    var rings: Number
    var samples: Number
}

external open class CinematicCamera(fov: Number, aspect: Number, near: Number, far: Number) : PerspectiveCamera {
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