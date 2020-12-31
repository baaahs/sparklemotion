@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

external interface Renderer {
    var domElement: HTMLCanvasElement
    fun render(scene: Object3D, camera: Camera)
    fun setSize(width: Number, height: Number, updateStyle: Boolean = definedExternally)
}

external interface WebGLRendererParameters {
    var canvas: dynamic /* HTMLCanvasElement? | OffscreenCanvas? */
        get() = definedExternally
        set(value) = definedExternally
    var context: WebGLRenderingContext?
        get() = definedExternally
        set(value) = definedExternally
    var precision: String?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var premultipliedAlpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var antialias: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencil: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preserveDrawingBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var powerPreference: String?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var logarithmicDepthBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WebGLDebug {
    var checkShaderErrors: Boolean
}

open external class WebGLRenderer(parameters: WebGLRendererParameters = definedExternally) : Renderer {
    override var domElement: HTMLCanvasElement
    open var context: WebGLRenderingContext
    open var autoClear: Boolean
    open var autoClearColor: Boolean
    open var autoClearDepth: Boolean
    open var autoClearStencil: Boolean
    open var debug: WebGLDebug
    open var sortObjects: Boolean
    open var clippingPlanes: Array<Any>
    open var localClippingEnabled: Boolean
    open var extensions: WebGLExtensions
    open var outputEncoding: TextureEncoding
    open var physicallyCorrectLights: Boolean
    open var toneMapping: ToneMapping
    open var toneMappingExposure: Number
    open var shadowMapDebug: Boolean
    open var maxMorphTargets: Number
    open var maxMorphNormals: Number
    open var info: WebGLInfo
    open var shadowMap: WebGLShadowMap
    open var pixelRatio: Number
    open var capabilities: WebGLCapabilities
    open var properties: WebGLProperties
    open var renderLists: WebGLRenderLists
    open var state: WebGLState
    open var xr: WebXRManager
    open fun getContext(): WebGLRenderingContext
    open fun getContextAttributes(): Any
    open fun forceContextLoss()
    open fun getMaxAnisotropy(): Number
    open fun getPrecision(): String
    open fun getPixelRatio(): Number
    open fun setPixelRatio(value: Number)
    open fun getDrawingBufferSize(target: Vector2): Vector2
    open fun setDrawingBufferSize(width: Number, height: Number, pixelRatio: Number)
    open fun getSize(target: Vector2): Vector2
    override fun setSize(width: Number, height: Number, updateStyle: Boolean)
    open fun getCurrentViewport(target: Vector4): Vector4
    open fun getViewport(target: Vector4): Vector4
    open fun setViewport(x: Vector4, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setViewport(x: Number, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun getScissor(target: Vector4): Vector4
    open fun setScissor(x: Vector4, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setScissor(x: Number, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun getScissorTest(): Boolean
    open fun setScissorTest(enable: Boolean)
    open fun setOpaqueSort(method: Function<*>)
    open fun setTransparentSort(method: Function<*>)
    open fun getClearColor(): Color
    open fun setClearColor(color: Color, alpha: Number = definedExternally)
    open fun setClearColor(color: String, alpha: Number = definedExternally)
    open fun setClearColor(color: Number, alpha: Number = definedExternally)
    open fun getClearAlpha(): Number
    open fun setClearAlpha(alpha: Number)
    open fun clear(color: Boolean = definedExternally, depth: Boolean = definedExternally, stencil: Boolean = definedExternally)
    open fun clearColor()
    open fun clearDepth()
    open fun clearStencil()
    open fun clearTarget(renderTarget: WebGLRenderTarget, color: Boolean, depth: Boolean, stencil: Boolean)
    open fun resetGLState()
    open fun dispose()
    open fun renderBufferImmediate(obj: Object3D, program: WebGLProgram)
    open fun renderBufferDirect(camera: Camera, scene: Scene, geometry: Geometry, material: Material, obj: Object3D, geometryGroup: Any)
    open fun renderBufferDirect(camera: Camera, scene: Scene, geometry: BufferGeometry, material: Material, obj: Object3D, geometryGroup: Any)
    open fun setAnimationLoop(callback: Function<*>?)
    open fun animate(callback: Function<*>)
    open fun compile(scene: Object3D, camera: Camera)
    override fun render(scene: Object3D, camera: Camera)
    open fun getActiveCubeFace(): Number
    open fun getActiveMipmapLevel(): Number
    open fun setFramebuffer(value: WebGLFramebuffer)
    open fun getRenderTarget(): RenderTarget?
    open fun getCurrentRenderTarget(): RenderTarget?
    open fun setRenderTarget(renderTarget: RenderTarget?, activeCubeFace: Number = definedExternally, activeMipmapLevel: Number = definedExternally)
    open fun readRenderTargetPixels(renderTarget: RenderTarget, x: Number, y: Number, width: Number, height: Number, buffer: Any, activeCubeFaceIndex: Number = definedExternally)
    open fun copyFramebufferToTexture(position: Vector2, texture: Texture, level: Number = definedExternally)
    open fun copyTextureToTexture(position: Vector2, srcTexture: Texture, dstTexture: Texture, level: Number = definedExternally)
    open fun initTexture(texture: Texture)
    open var gammaFactor: Number
    open var vr: Boolean
    open var shadowMapEnabled: Boolean
    open var shadowMapType: ShadowMapType
    open var shadowMapCullFace: CullFace
    open fun supportsFloatTextures(): Any
    open fun supportsHalfFloatTextures(): Any
    open fun supportsStandardDerivatives(): Any
    open fun supportsCompressedTextureS3TC(): Any
    open fun supportsCompressedTexturePVRTC(): Any
    open fun supportsBlendMinMax(): Any
    open fun supportsVertexTextures(): Any
    open fun supportsInstancedArrays(): Any
    open fun enableScissorTest(boolean: Any): Any
}