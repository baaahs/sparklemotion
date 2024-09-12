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

external open class Pass {
    open var isPass: Boolean
    open var enabled: Boolean
    open var needsSwap: Boolean
    open var clear: Boolean
    open var renderToScreen: Boolean
    open fun setSize(width: Number, height: Number)
    open fun render(renderer: WebGLRenderer, writeBuffer: WebGLRenderTarget__0, readBuffer: WebGLRenderTarget__0, deltaTime: Number, maskActive: Boolean)
    open fun dispose()
}

external open class FullScreenQuad(material: Material = definedExternally) {
    open fun render(renderer: WebGLRenderer)
    open fun dispose()
    open var material: Material
}