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

external interface OutlineEffectParameters {
    var defaultThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var defaultColor: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var defaultAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var defaultKeepAlive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class OutlineEffect(renderer: WebGLRenderer, parameters: OutlineEffectParameters = definedExternally) {
    open var enabled: Boolean
    open var autoClear: Boolean
    open var domElement: HTMLElement
    open var shadowMap: WebGLShadowMap
    open fun clear(color: Boolean = definedExternally, depth: Boolean = definedExternally, stencil: Boolean = definedExternally)
    open fun getPixelRatio(): Number
    open fun getSize(target: Vector2): Vector2
    open fun render(scene: Scene, camera: Camera)
    open fun renderOutline(scene: Scene, camera: Camera)
    open fun setRenderTarget(renderTarget: WebGLRenderTarget__0?)
    open fun setPixelRatio(value: Number)
    open fun setScissor(x: Vector4, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setScissor(x: Vector4)
    open fun setScissor(x: Vector4, y: Number = definedExternally)
    open fun setScissor(x: Vector4, y: Number = definedExternally, width: Number = definedExternally)
    open fun setScissor(x: Number, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setScissor(x: Number)
    open fun setScissor(x: Number, y: Number = definedExternally)
    open fun setScissor(x: Number, y: Number = definedExternally, width: Number = definedExternally)
    open fun setScissorTest(enable: Boolean)
    open fun setSize(width: Number, height: Number, updateStyle: Boolean = definedExternally)
    open fun setViewport(x: Vector4, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setViewport(x: Vector4)
    open fun setViewport(x: Vector4, y: Number = definedExternally)
    open fun setViewport(x: Vector4, y: Number = definedExternally, width: Number = definedExternally)
    open fun setViewport(x: Number, y: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    open fun setViewport(x: Number)
    open fun setViewport(x: Number, y: Number = definedExternally)
    open fun setViewport(x: Number, y: Number = definedExternally, width: Number = definedExternally)
}