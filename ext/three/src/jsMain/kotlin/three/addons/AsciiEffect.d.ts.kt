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

external open class AsciiEffect(renderer: WebGLRenderer, charSet: String = definedExternally, options: AsciiEffectOptions = definedExternally) {
    open var domElement: HTMLElement
    open fun render(scene: Scene, camera: Camera)
    open fun setSize(width: Number, height: Number)
}