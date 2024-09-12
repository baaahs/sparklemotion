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

external interface LineMaterialParameters : ShaderMaterialParameters {
    override var alphaToCoverage: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var dashed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var dashScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var resolution: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var worldUnits: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class LineMaterial(parameters: LineMaterialParameters = definedExternally) : ShaderMaterial {
    open var color: Color
    open var dashed: Boolean
    open var dashScale: Number
    open var dashSize: Number
    open var dashOffset: Number
    open var gapSize: Number
    override var opacity: Number
    open val isLineMaterial: Boolean
    override var linewidth: Number
    open var resolution: Vector2
    override var alphaToCoverage: Boolean
    open var worldUnits: Boolean
}