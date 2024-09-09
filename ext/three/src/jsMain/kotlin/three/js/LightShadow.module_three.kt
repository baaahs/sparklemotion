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
    var camera: Omit<Object3DJSONObject, String /* "matrix" */>
}

external open class LightShadow<TCamera : Camera>(camera: TCamera) {
    open var camera: TCamera
    open var intensity: Number
    open var bias: Number
    open var normalBias: Number
    open var radius: Number
    open var blurSamples: Number
    open var mapSize: Vector2
    open var map: WebGLRenderTarget__0?
    open var mapPass: WebGLRenderTarget__0?
    open var matrix: Matrix4
    open var autoUpdate: Boolean
    open var needsUpdate: Boolean
    open fun getViewportCount(): Number
    open fun copy(source: LightShadow__0): LightShadow<TCamera> /* this */
    open fun clone(recursive: Boolean = definedExternally): LightShadow<TCamera> /* this */
    open fun toJSON(): LightShadowJSON
    open fun getFrustum(): Frustum
    open fun updateMatrices(light: Light__0)
    open fun getViewport(viewportIndex: Number): Vector4
    open fun getFrameExtents(): Vector2
    open fun dispose()
}

external open class LightShadow__0 : LightShadow<Camera>