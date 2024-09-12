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

external interface RenderPixelatedPassParameters {
    var normalEdgeStrength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthEdgeStrength: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class RenderPixelatedPass(pixelSize: Number, scene: Scene, camera: Camera, options: RenderPixelatedPassParameters = definedExternally) : Pass {
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
    open var beautyRenderTarget: WebGLRenderTarget__0
    open var normalRenderTarget: WebGLRenderTarget__0
    open fun setPixelSize(pixelSize: Number)
}