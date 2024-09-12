@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Material
import three.WebGLRenderTarget
import three.WebGLRenderer

open external class Pass {
    open var isPass: Boolean
    open var enabled: Boolean
    open var needsSwap: Boolean
    open var clear: Boolean
    open var renderToScreen: Boolean
    open fun setSize(width: Number, height: Number)
    open fun render(renderer: WebGLRenderer, writeBuffer: WebGLRenderTarget<*>, readBuffer: WebGLRenderTarget<*>, deltaTime: Number, maskActive: Boolean)
    open fun dispose()
}

open external class FullScreenQuad(material: Material = definedExternally) {
    open fun render(renderer: WebGLRenderer)
    open fun dispose()
    open var material: Material
}