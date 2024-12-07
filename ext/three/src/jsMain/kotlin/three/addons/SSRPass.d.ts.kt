package three.addons

import three.*

external interface SSRPassParams {
    var renderer: WebGLRenderer
    var scene: Scene
    var camera: Camera
    var width: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var selects: Array<Mesh<*, *>>?
    var isPerspectiveCamera: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var isBouncing: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var groundReflector: ReflectorForSSRPass__0?
}

external interface `T$94` {
    var Default: Number /* 0 */
    var SSR: Number /* 1 */
    var Beauty: Number /* 3 */
    var Depth: Number /* 4 */
    var Normal: Number /* 5 */
    var Metalness: Number /* 7 */
}

open external class SSRPass(params: SSRPassParams) : Pass {
    open var width: Number
    open var height: Number
    override var clear: Boolean
    open var renderer: WebGLRenderer
    open var scene: Scene
    open var camera: Camera
    open var groundReflector: ReflectorForSSRPass__0?
    open var opacity: Number
    open var output: Number
    open var maxDistance: Number
    open var thickness: Number
    open var tempColor: Color
    open var selective: Boolean
    open var blur: Boolean
    open var beautyRenderTarget: WebGLRenderTarget<*>
    open var prevRenderTarget: WebGLRenderTarget<*>
    open var normalRenderTarget: WebGLRenderTarget<*>
    open var metalnessRenderTarget: WebGLRenderTarget<*>
    open var ssrRenderTarget: WebGLRenderTarget<*>
    open var blurRenderTarget: WebGLRenderTarget<*>
    open var blurRenderTarget2: WebGLRenderTarget<*>
    open var ssrMaterial: ShaderMaterial
    open var normalMaterial: MeshNormalMaterial
    open var metalnessOnMaterial: MeshBasicMaterial
    open var metalnessOffMaterial: MeshBasicMaterial
    open var blurMaterial: ShaderMaterial
    open var blurMaterial2: ShaderMaterial
    open var depthRenderMaterial: ShaderMaterial
    open var copyMaterial: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open var originalClearColor: Color
    override fun dispose()
    open var renderPass: (renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: dynamic /* Color | String | Number */, clearAlpha: dynamic /* Color | String | Number */) -> Unit
    open var renderOverride: (renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: dynamic /* Color | String | Number */, clearAlpha: dynamic /* Color | String | Number */) -> Unit
    open var renderMetalness: (renderer: WebGLRenderer, passMaterial: Material, renderTarget: WebGLRenderTarget<*>, clearColor: dynamic /* Color | String | Number */, clearAlpha: dynamic /* Color | String | Number */) -> Unit

    companion object {
        var OUTPUT: `T$94`
    }
}