@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

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

external interface Renderer {
    var domElement: HTMLCanvasElement
    fun render(scene: Object3D__0, camera: Camera)
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
    var failIfMajorPerformanceCaveat: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WebGLDebug {
    var checkShaderErrors: Boolean
    var onShaderError: ((gl: WebGLRenderingContext, program: WebGLProgram, glVertexShader: WebGLShader, glFragmentShader: WebGLShader) -> Unit)?
}

external open class WebGLRenderer(parameters: WebGLRendererParameters = definedExternally) : Renderer {
    override var domElement: HTMLCanvasElement
    open var autoClear: Boolean
    open var autoClearColor: Boolean
    open var autoClearDepth: Boolean
    open var autoClearStencil: Boolean
    open var debug: WebGLDebug
    open var sortObjects: Boolean
    open var clippingPlanes: Array<Plane>
    open var localClippingEnabled: Boolean
    open var extensions: WebGLExtensions
    open var toneMapping: Any
    open var toneMappingExposure: Number
    open var info: WebGLInfo
    open var shadowMap: WebGLShadowMap
    open var pixelRatio: Number
    open var capabilities: WebGLCapabilities
    open var properties: WebGLProperties
    open var renderLists: WebGLRenderLists
    open var state: WebGLState
    open var xr: WebXRManager
    open fun getContext(): dynamic /* WebGLRenderingContext | WebGL2RenderingContext */
    open fun getContextAttributes(): Any
    open fun forceContextLoss()
    open fun forceContextRestore()
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
    open fun setViewport(x: Vector4)
    open fun setViewport(x: Vector4, y: Number = definedExternally)
    open fun setViewport(x: Vector4, y: Number = definedExternally, width: Number = definedExternally)
    open fun setViewport(x: Number, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setViewport(x: Number)
    open fun setViewport(x: Number, y: Number = definedExternally)
    open fun setViewport(x: Number, y: Number = definedExternally, width: Number = definedExternally)
    open fun getScissor(target: Vector4): Vector4
    open fun setScissor(x: Vector4, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setScissor(x: Vector4)
    open fun setScissor(x: Vector4, y: Number = definedExternally)
    open fun setScissor(x: Vector4, y: Number = definedExternally, width: Number = definedExternally)
    open fun setScissor(x: Number, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setScissor(x: Number)
    open fun setScissor(x: Number, y: Number = definedExternally)
    open fun setScissor(x: Number, y: Number = definedExternally, width: Number = definedExternally)
    open fun getScissorTest(): Boolean
    open fun setScissorTest(enable: Boolean)
    open fun setOpaqueSort(method: (a: Any, b: Any) -> Number)
    open fun setTransparentSort(method: (a: Any, b: Any) -> Number)
    open fun getClearColor(target: Color): Color
    open fun setClearColor(color: Color, alpha: Number = definedExternally)
    open fun setClearColor(color: Color)
    open fun setClearColor(color: String, alpha: Number = definedExternally)
    open fun setClearColor(color: String)
    open fun setClearColor(color: Number, alpha: Number = definedExternally)
    open fun setClearColor(color: Number)
    open fun getClearAlpha(): Number
    open fun setClearAlpha(alpha: Number)
    open fun clear(color: Boolean = definedExternally, depth: Boolean = definedExternally, stencil: Boolean = definedExternally)
    open fun clearColor()
    open fun clearDepth()
    open fun clearStencil()
    open fun clearTarget(renderTarget: WebGLRenderTarget__0, color: Boolean, depth: Boolean, stencil: Boolean)
    open fun resetGLState()
    open fun dispose()
    open fun renderBufferDirect(camera: Camera, scene: Scene, geometry: BufferGeometry__0, material: Material, obj: Object3D__0, geometryGroup: Any)
    open fun setAnimationLoop(callback: XRFrameRequestCallback?)
    open fun animate(callback: () -> Unit)
    open var compile: (scene: Object3D__0, camera: Camera, targetScene: Scene?) -> Set<Material>
    open var compileAsync: (scene: Object3D__0, camera: Camera, targetScene: Scene?) -> Promise<Object3D__0>
    override fun render(scene: Object3D__0, camera: Camera)
    open fun getActiveCubeFace(): Number
    open fun getActiveMipmapLevel(): Number
    open fun getRenderTarget(): WebGLRenderTarget__0?
    open fun getCurrentRenderTarget(): WebGLRenderTarget__0?
    open fun setRenderTarget(renderTarget: WebGLRenderTarget__0?, activeCubeFace: Number = definedExternally, activeMipmapLevel: Number = definedExternally)
    open fun setRenderTarget(renderTarget: WebGLRenderTarget__0?)
    open fun setRenderTarget(renderTarget: WebGLRenderTarget__0?, activeCubeFace: Number = definedExternally)
    open fun setRenderTarget(renderTarget: WebGLRenderTarget<Array<Texture>>?, activeCubeFace: Number = definedExternally, activeMipmapLevel: Number = definedExternally)
    open fun setRenderTarget(renderTarget: WebGLRenderTarget<Array<Texture>>?)
    open fun setRenderTarget(renderTarget: WebGLRenderTarget<Array<Texture>>?, activeCubeFace: Number = definedExternally)
    open fun readRenderTargetPixels(renderTarget: WebGLRenderTarget__0, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */, activeCubeFaceIndex: Number = definedExternally)
    open fun readRenderTargetPixels(renderTarget: WebGLRenderTarget__0, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */)
    open fun readRenderTargetPixels(renderTarget: WebGLRenderTarget<Array<Texture>>, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */, activeCubeFaceIndex: Number = definedExternally)
    open fun readRenderTargetPixels(renderTarget: WebGLRenderTarget<Array<Texture>>, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */)
    open fun readRenderTargetPixelsAsync(renderTarget: WebGLRenderTarget__0, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */, activeCubeFaceIndex: Number = definedExternally): Promise<dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */>
    open fun readRenderTargetPixelsAsync(renderTarget: WebGLRenderTarget__0, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */): Promise<dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */>
    open fun readRenderTargetPixelsAsync(renderTarget: WebGLRenderTarget<Array<Texture>>, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */, activeCubeFaceIndex: Number = definedExternally): Promise<dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */>
    open fun readRenderTargetPixelsAsync(renderTarget: WebGLRenderTarget<Array<Texture>>, x: Number, y: Number, width: Number, height: Number, buffer: Any /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */): Promise<dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */>
    open fun copyFramebufferToTexture(texture: Texture, position: Vector2? = definedExternally, level: Number = definedExternally)
    open fun copyTextureToTexture(srcTexture: Texture, dstTexture: Texture, srcRegion: Box2? = definedExternally, dstPosition: Vector2? = definedExternally, level: Number = definedExternally)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: Data3DTexture, srcRegion: Box3? = definedExternally, dstPosition: Vector3? = definedExternally, level: Number = definedExternally)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: Data3DTexture)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: Data3DTexture, srcRegion: Box3? = definedExternally)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: Data3DTexture, srcRegion: Box3? = definedExternally, dstPosition: Vector3? = definedExternally)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: DataArrayTexture, srcRegion: Box3? = definedExternally, dstPosition: Vector3? = definedExternally, level: Number = definedExternally)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: DataArrayTexture)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: DataArrayTexture, srcRegion: Box3? = definedExternally)
    open fun copyTextureToTexture3D(srcTexture: Texture, dstTexture: DataArrayTexture, srcRegion: Box3? = definedExternally, dstPosition: Vector3? = definedExternally)
    open fun initRenderTarget(target: WebGLRenderTarget__0)
    open fun initTexture(texture: Texture)
    open fun resetState()
    open var vr: Boolean
    open var shadowMapEnabled: Boolean
    open var shadowMapType: Any
    open var shadowMapCullFace: Any
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