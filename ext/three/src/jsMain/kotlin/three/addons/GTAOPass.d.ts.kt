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

external interface `T$89` {
    var depthTexture: DepthTexture?
        get() = definedExternally
        set(value) = definedExternally
    var normalTexture: Texture?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$90` {
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var distanceExponent: Number?
        get() = definedExternally
        set(value) = definedExternally
    var thickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var distanceFallOff: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var samples: Number?
        get() = definedExternally
        set(value) = definedExternally
    var screenSpaceRadius: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$91` {
    var lumaPhi: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthPhi: Number?
        get() = definedExternally
        set(value) = definedExternally
    var normalPhi: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radiusExponent: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rings: Number?
        get() = definedExternally
        set(value) = definedExternally
    var samples: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$92` {
    var Off: String /* "-1" */
    var Default: Number /* 0 */
    var Diffuse: Number /* 1 */
    var Depth: Number /* 2 */
    var Normal: Number /* 3 */
    var AO: Number /* 4 */
    var Denoise: Number /* 5 */
}

external open class GTAOPass(scene: Scene, camera: Camera, width: Number? = definedExternally, height: Number? = definedExternally, parameters: `T$89`? = definedExternally) : Pass {
    open var width: Number
    open var height: Number
    override var clear: Boolean
    open var camera: Camera
    open var scene: Scene
    open var output: Number
    open var blendIntensity: Number
    open var pdRings: Number
    open var pdRadiusExponent: Number
    open var pdSamples: Number
    open var gtaoNoiseTexture: DataTexture
    open var pdNoiseTexture: DataTexture
    open var gtaoRenderTarget: WebGLRenderTarget__0
    open var pdRenderTarget: WebGLRenderTarget__0
    open var gtaoMaterial: ShaderMaterial
    open var normalMaterial: MeshNormalMaterial
    open var pdMaterial: ShaderMaterial
    open var depthRenderMaterial: ShaderMaterial
    open var copyMaterial: ShaderMaterial
    open var blendMaterial: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var originalClearColor: Color
    open var depthTexture: DepthTexture
    open var normalTexture: Texture
    open fun setGBuffer(depthTexture: DepthTexture? = definedExternally, normalTexture: Texture? = definedExternally)
    open fun setSceneClipBox(box: Box3)
    open fun updateGtaoMaterial(parameters: `T$90`)
    open fun updatePdMaterial(parameters: `T$91`)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Color? = definedExternally, clearAlpha: Number? = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Color? = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: String? = definedExternally, clearAlpha: Number? = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: String? = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Number? = definedExternally, clearAlpha: Number? = definedExternally)
    open fun renderPass(renderer: WebGLRenderer, passMaterial: ShaderMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Number? = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Color? = definedExternally, clearAlpha: Number? = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Color? = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: String? = definedExternally, clearAlpha: Number? = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: String? = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Number? = definedExternally, clearAlpha: Number? = definedExternally)
    open fun renderOverride(renderer: WebGLRenderer, overrideMaterial: MeshNormalMaterial, renderTarget: WebGLRenderTarget__0?, clearColor: Number? = definedExternally)
    open fun overrideVisibility()
    open fun restoreVisibility()
    open fun generateNoise(size: Number = definedExternally): DataTexture

    companion object {
        var OUTPUT: `T$92`
    }
}