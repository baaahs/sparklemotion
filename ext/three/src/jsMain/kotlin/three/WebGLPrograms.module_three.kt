@file:JsModule("three")
@file:JsNonModule
package three

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

external interface `T$16` {
    @nativeGetter
    operator fun get(define: String): dynamic /* String? | Number? | Boolean? */
    @nativeSetter
    operator fun set(define: String, value: String)
    @nativeSetter
    operator fun set(define: String, value: Number)
    @nativeSetter
    operator fun set(define: String, value: Boolean)
}

external interface WebGLProgramParameters {
    var shaderID: String
    var shaderType: String
    var shaderName: String
    var vertexShader: String
    var fragmentShader: String
    var defines: `T$16`?
    var customVertexShaderID: String?
    var customFragmentShaderID: String?
    var isRawShaderMaterial: Boolean
    var glslVersion: Any?
    var precision: String /* "lowp" | "mediump" | "highp" */
    var batching: Boolean
    var batchingColor: Boolean
    var instancing: Boolean
    var instancingColor: Boolean
    var instancingMorph: Boolean
    var supportsVertexTextures: Boolean
    var outputColorSpace: Any
    var alphaToCoverage: Boolean
    var map: Boolean
    var matcap: Boolean
    var envMap: Boolean
    var envMapMode: dynamic /* Any | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var envMapCubeUVHeight: Number?
    var aoMap: Boolean
    var lightMap: Boolean
    var bumpMap: Boolean
    var normalMap: Boolean
    var displacementMap: Boolean
    var emissiveMap: Boolean
    var normalMapObjectSpace: Boolean
    var normalMapTangentSpace: Boolean
    var metalnessMap: Boolean
    var roughnessMap: Boolean
    var anisotropy: Boolean
    var anisotropyMap: Boolean
    var clearcoat: Boolean
    var clearcoatMap: Boolean
    var clearcoatNormalMap: Boolean
    var clearcoatRoughnessMap: Boolean
    var dispersion: Boolean
    var iridescence: Boolean
    var iridescenceMap: Boolean
    var iridescenceThicknessMap: Boolean
    var sheen: Boolean
    var sheenColorMap: Boolean
    var sheenRoughnessMap: Boolean
    var specularMap: Boolean
    var specularColorMap: Boolean
    var specularIntensityMap: Boolean
    var transmission: Boolean
    var transmissionMap: Boolean
    var thicknessMap: Boolean
    var gradientMap: Boolean
    var opaque: Boolean
    var alphaMap: Boolean
    var alphaTest: Boolean
    var alphaHash: Boolean
    var combine: Any?
    var mapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var aoMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var lightMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var bumpMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var normalMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var displacementMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var emissiveMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var metalnessMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var roughnessMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var anisotropyMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughnessMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceThicknessMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var sheenColorMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var sheenRoughnessMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var specularMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var specularColorMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var specularIntensityMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var transmissionMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var thicknessMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var alphaMapUv: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var vertexTangents: Boolean
    var vertexColors: Boolean
    var vertexAlphas: Boolean
    var vertexUv1s: Boolean
    var vertexUv2s: Boolean
    var vertexUv3s: Boolean
    var pointsUvs: Boolean
    var fog: Boolean
    var useFog: Boolean
    var fogExp2: Boolean
    var flatShading: Boolean
    var sizeAttenuation: Boolean
    var logarithmicDepthBuffer: Boolean
    var skinning: Boolean
    var morphTargets: Boolean
    var morphNormals: Boolean
    var morphColors: Boolean
    var morphTargetsCount: Number
    var morphTextureStride: Number
    var numDirLights: Number
    var numPointLights: Number
    var numSpotLights: Number
    var numSpotLightMaps: Number
    var numRectAreaLights: Number
    var numHemiLights: Number
    var numDirLightShadows: Number
    var numPointLightShadows: Number
    var numSpotLightShadows: Number
    var numSpotLightShadowsWithMaps: Number
    var numLightProbes: Number
    var numClippingPlanes: Number
    var numClipIntersection: Number
    var dithering: Boolean
    var shadowMapEnabled: Boolean
    var shadowMapType: Any
    var toneMapping: Any
    var decodeVideoTexture: Boolean
    var premultipliedAlpha: Boolean
    var doubleSided: Boolean
    var flipSided: Boolean
    var useDepthPacking: Boolean
    var depthPacking: dynamic /* Any | 0 */
        get() = definedExternally
        set(value) = definedExternally
    var index0AttributeName: String?
    var extensionClipCullDistance: Boolean
    var extensionMultiDraw: Boolean
    var rendererExtensionParallelShaderCompile: Boolean
    var customProgramCacheKey: String
}

external interface `T$17` {
    @nativeGetter
    operator fun get(uniform: String): IUniform__0?
    @nativeSetter
    operator fun set(uniform: String, value: IUniform__0)
}

external interface WebGLProgramParametersWithUniforms : WebGLProgramParameters {
    var uniforms: `T$17`
}

open external class WebGLPrograms(renderer: WebGLRenderer, cubemaps: WebGLCubeMaps, extensions: WebGLExtensions, capabilities: WebGLCapabilities, bindingStates: WebGLBindingStates, clipping: WebGLClipping) {
    open var programs: Array<WebGLProgram>
    open fun getParameters(material: Material, lights: WebGLLightsState, shadows: Array<Light__0>, scene: Scene, obj: Object3D/*<Object3DEventMap>*/): WebGLProgramParameters
    open fun getProgramCacheKey(parameters: WebGLProgramParameters): String
    open fun getUniforms(material: Material): `T$17`
    open fun acquireProgram(parameters: WebGLProgramParametersWithUniforms, cacheKey: String): WebGLProgram
    open fun releaseProgram(program: WebGLProgram)
}