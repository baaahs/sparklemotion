package three.js

external interface LightShadowJSON {
    var intensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var normalBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var mapSize: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
//    var camera: Omit<Object3DJSONObject, String /* "matrix" */>
}

open external class LightShadow<TCamera : Camera>(camera: TCamera) {
    open var camera: TCamera
    open var intensity: Number
    open var bias: Number
    open var normalBias: Number
    open var radius: Number
    open var blurSamples: Number
    open var mapSize: Vector2
    open var map: WebGLRenderTarget<Texture>?
    open var mapPass: WebGLRenderTarget<Texture>?
    open var matrix: Matrix4
    open var autoUpdate: Boolean
    open var needsUpdate: Boolean
    open fun getViewportCount(): Number
    open fun copy(source: LightShadow<Camera>): LightShadow<TCamera> /* this */
    open fun clone(recursive: Boolean = definedExternally): LightShadow<TCamera> /* this */
    open fun toJSON(): LightShadowJSON
    open fun getFrustum(): Frustum
    open fun updateMatrices(light: Light__0)
    open fun getViewport(viewportIndex: Number): Vector4
    open fun getFrameExtents(): Vector2
    open fun dispose()
}
