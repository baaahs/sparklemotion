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

external open class SSAARenderPass : Pass {
    constructor(scene: Scene, camera: Camera, clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera)
    constructor(scene: Scene, camera: Camera, clearColor: Color = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: String = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    constructor(scene: Scene, camera: Camera, clearColor: Number = definedExternally)
    open var scene: Scene
    open var camera: Camera
    open var sampleLevel: Number
    open var unbiased: Boolean
    open var stencilBuffer: Boolean
    open var clearColor: dynamic /* Color | String | Number */
    open var clearAlpha: Number
    open var copyUniforms: Any?
    open var copyMaterial: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var sampleRenderTarget: WebGLRenderTarget__0?
}