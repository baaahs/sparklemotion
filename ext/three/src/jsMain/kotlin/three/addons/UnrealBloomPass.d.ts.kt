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

external open class UnrealBloomPass(resolution: Vector2, strength: Number, radius: Number, threshold: Number) : Pass {
    open var resolution: Vector2
    open var strength: Number
    open var radius: Number
    open var threshold: Number
    open var clearColor: Color
    open var renderTargetsHorizontal: Array<WebGLRenderTarget__0>
    open var renderTargetsVertical: Array<WebGLRenderTarget__0>
    open var nMips: Number
    open var renderTargetBright: WebGLRenderTarget__0
    open var highPassUniforms: Any?
    open var materialHighPassFilter: ShaderMaterial
    open var separableBlurMaterials: Array<ShaderMaterial>
    open var compositeMaterial: ShaderMaterial
    open var bloomTintColors: Array<Vector3>
    open var copyUniforms: Any?
    open var blendMaterial: ShaderMaterial
    open var oldClearColor: Color
    open var oldClearAlpha: Number
    open var basic: MeshBasicMaterial
    open var fsQuad: FullScreenQuad
    override fun dispose()
    open fun getSeperableBlurMaterial(): ShaderMaterial
    open fun getCompositeMaterial(): ShaderMaterial
}