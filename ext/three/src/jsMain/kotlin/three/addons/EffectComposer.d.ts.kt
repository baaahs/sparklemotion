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

external open class EffectComposer(renderer: WebGLRenderer, renderTarget: WebGLRenderTarget__0 = definedExternally) {
    open var renderer: WebGLRenderer
    open var renderTarget1: WebGLRenderTarget__0
    open var renderTarget2: WebGLRenderTarget__0
    open var writeBuffer: WebGLRenderTarget__0
    open var readBuffer: WebGLRenderTarget__0
    open var passes: Array<Pass>
    open var copyPass: ShaderPass
    open var clock: Clock
    open var renderToScreen: Boolean
    open fun swapBuffers()
    open fun addPass(pass: Pass)
    open fun insertPass(pass: Pass, index: Number)
    open fun removePass(pass: Pass)
    open fun isLastEnabledPass(passIndex: Number): Boolean
    open fun render(deltaTime: Number = definedExternally)
    open fun reset(renderTarget: WebGLRenderTarget__0 = definedExternally)
    open fun setSize(width: Number, height: Number)
    open fun setPixelRatio(pixelRatio: Number)
    open fun dispose()
}