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

external interface HalftonePassParameters {
    var shape: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotateR: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotateB: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotateG: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scatter: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blending: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendingMode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var greyscale: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var disable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$93` {
    var tDiffuse: IUniform__0
    var shape: IUniform<Number>
    var radius: IUniform<Number>
    var rotateR: IUniform<Number>
    var rotateG: IUniform<Number>
    var rotateB: IUniform<Number>
    var scatter: IUniform<Number>
    var width: IUniform<Number>
    var height: IUniform<Number>
    var blending: IUniform<Number>
    var blendingMode: IUniform<Number>
    var greyscale: IUniform<Boolean>
    var disable: IUniform<Boolean>
}

external open class HalftonePass(width: Number, height: Number, params: HalftonePassParameters) : Pass {
    open var uniforms: `T$93`
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}