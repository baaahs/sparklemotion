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

external open class BokehPass(scene: Scene, camera: Camera, params: BokehPassParamters) : Pass {
    open var scene: Scene
    open var camera: Camera
    open var renderTargetColor: WebGLRenderTarget__0
    open var renderTargetDepth: WebGLRenderTarget__0
    open var materialDepth: MeshDepthMaterial
    open var materialBokeh: ShaderMaterial
    open var uniforms: Any?
    open var fsQuad: FullScreenQuad
    open var oldClearColor: Color
}