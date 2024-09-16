@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Scene
import three.WebGLRenderer

external interface AsciiEffectOptions {
    var resolution: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var color: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var block: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var invert: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class AsciiEffect(renderer: WebGLRenderer, charSet: String = definedExternally, options: AsciiEffectOptions = definedExternally) {
    open var domElement: HTMLElement
    open fun render(scene: Scene, camera: Camera)
    open fun setSize(width: Number, height: Number)
}