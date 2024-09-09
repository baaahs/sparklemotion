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

external interface MaterialParameters {
    var alphaHash: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alphaTest: Number?
        get() = definedExternally
        set(value) = definedExternally
    var alphaToCoverage: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var blendAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var blendDst: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendDstAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendEquation: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendEquationAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blending: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendSrc: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendSrcAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clipIntersection: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var clippingPlanes: Array<Plane>?
        get() = definedExternally
        set(value) = definedExternally
    var clipShadows: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var colorWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var defines: Any?
        get() = definedExternally
        set(value) = definedExternally
    var depthFunc: Any?
        get() = definedExternally
        set(value) = definedExternally
    var depthTest: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depthWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var opacity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffset: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetFactor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetUnits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var precision: String? /* "highp" | "mediump" | "lowp" */
        get() = definedExternally
        set(value) = definedExternally
    var premultipliedAlpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var forceSinglePass: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var dithering: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var side: Any?
        get() = definedExternally
        set(value) = definedExternally
    var shadowSide: Any?
        get() = definedExternally
        set(value) = definedExternally
    var toneMapped: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var transparent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var vertexColors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var visible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var format: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFunc: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilRef: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilWriteMask: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFuncMask: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFail: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilZFail: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilZPass: Any?
        get() = definedExternally
        set(value) = definedExternally
    var userData: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MaterialJSON {
    var metadata: `T$0`
    var uuid: String
    var type: String
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var color: Number?
        get() = definedExternally
        set(value) = definedExternally
    var roughness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var metalness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sheen: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sheenColor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sheenRoughness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var emissive: Number?
        get() = definedExternally
        set(value) = definedExternally
    var emissiveIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var specular: Number?
        get() = definedExternally
        set(value) = definedExternally
    var specularIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var specularColor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shininess: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoat: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughnessMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalScale: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var dispersion: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescence: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceIOR: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceThicknessRange: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceThicknessMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropy: Number?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropyRotation: Number?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropyMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var map: String?
        get() = definedExternally
        set(value) = definedExternally
    var matcap: String?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var lightMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var lightMapIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var aoMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var aoMapIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bumpMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var bumpScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var normalMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var normalMapType: Any?
        get() = definedExternally
        set(value) = definedExternally
    var normalScale: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var displacementMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var displacementScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var displacementBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var roughnessMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var metalnessMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var emissiveMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var specularMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var specularIntensityMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var specularColorMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var envMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var combine: Any?
        get() = definedExternally
        set(value) = definedExternally
    var envMapRotation: dynamic /* JsTuple<x, Number, y, Number, z, Number, Any, String?> */
        get() = definedExternally
        set(value) = definedExternally
    var envMapIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var reflectivity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var refractionRatio: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gradientMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var transmission: Number?
        get() = definedExternally
        set(value) = definedExternally
    var transmissionMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var thickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var thicknessMap: String?
        get() = definedExternally
        set(value) = definedExternally
    var attenuationDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var attenuationColor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowSide: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sizeAttenuation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var blending: Any?
        get() = definedExternally
        set(value) = definedExternally
    var side: Any?
        get() = definedExternally
        set(value) = definedExternally
    var vertexColors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var opacity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var transparent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var blendSrc: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendDst: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendEquation: Any?
        get() = definedExternally
        set(value) = definedExternally
    var blendSrcAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendDstAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendEquationAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendColor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthFunc: Any?
        get() = definedExternally
        set(value) = definedExternally
    var depthTest: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depthWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var colorWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencilWriteMask: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFunc: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilRef: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFuncMask: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFail: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilZFail: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilZPass: Any?
        get() = definedExternally
        set(value) = definedExternally
    var stencilWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var rotation: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffset: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetFactor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetUnits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var linewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dithering: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alphaTest: Number?
        get() = definedExternally
        set(value) = definedExternally
    var alphaHash: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alphaToCoverage: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var premultipliedAlpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var forceSinglePass: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wireframe: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinecap: String?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinejoin: String?
        get() = definedExternally
        set(value) = definedExternally
    var flatShading: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var visible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var toneMapped: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var userData: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
    var textures: Array<Omit<TextureJSON, String? /* "metadata" */>>?
        get() = definedExternally
        set(value) = definedExternally
    var images: Array<SourceJSON>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$8` {
    var dispose: Any
}

external open class Material : EventDispatcher<`T$8`> {
    open val isMaterial: Boolean
    open var alphaHash: Boolean
    open var alphaToCoverage: Boolean
    open var blendAlpha: Number
    open var blendColor: Color
    open var blendDst: Any
    open var blendDstAlpha: Number?
    open var blendEquation: Any
    open var blendEquationAlpha: Number?
    open var blending: Any
    open var blendSrc: Any
    open var blendSrcAlpha: Number?
    open var clipIntersection: Boolean
    open var clippingPlanes: Array<Plane>?
    open var clipShadows: Boolean
    open var colorWrite: Boolean
    open var defines: Json?
    open var depthFunc: Any
    open var depthTest: Boolean
    open var depthWrite: Boolean
    open var id: Number
    open var stencilWrite: Boolean
    open var stencilFunc: Any
    open var stencilRef: Number
    open var stencilWriteMask: Number
    open var stencilFuncMask: Number
    open var stencilFail: Any
    open var stencilZFail: Any
    open var stencilZPass: Any
    open var name: String
    open var opacity: Number
    open var polygonOffset: Boolean
    open var polygonOffsetFactor: Number
    open var polygonOffsetUnits: Number
    open var precision: String /* "highp" | "mediump" | "lowp" */
    open var premultipliedAlpha: Boolean
    open var forceSinglePass: Boolean
    open var dithering: Boolean
    open var side: Any
    open var shadowSide: Any?
    open var toneMapped: Boolean
    open var transparent: Boolean
    open var type: String
    open var uuid: String
    open var vertexColors: Boolean
    open var visible: Boolean
    open var userData: Record<String, Any>
    open var version: Number
    open fun onBeforeRender(renderer: WebGLRenderer, scene: Scene, camera: Camera, geometry: BufferGeometry__0, obj: Object3D__0, group: Group__0)
    open fun onBeforeCompile(parameters: WebGLProgramParametersWithUniforms, renderer: WebGLRenderer)
    open fun customProgramCacheKey(): String
    open fun setValues(values: MaterialParameters)
    open fun toJSON(meta: JSONMeta = definedExternally): MaterialJSON
    open fun clone(): Material /* this */
    open fun copy(material: Material): Material /* this */
    open fun dispose()
    open fun onBuild(obj: Object3D__0, parameters: WebGLProgramParametersWithUniforms, renderer: WebGLRenderer)
}