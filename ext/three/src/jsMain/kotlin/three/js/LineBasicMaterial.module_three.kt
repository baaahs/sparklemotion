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

external interface LineBasicMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var linewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var linecap: String?
        get() = definedExternally
        set(value) = definedExternally
    var linejoin: String?
        get() = definedExternally
        set(value) = definedExternally
}

external open class LineBasicMaterial(parameters: LineBasicMaterialParameters = definedExternally) : Material {
    open val isLineBasicMaterial: Boolean
    override var type: String
    open var color: Color
    open var fog: Boolean
    open var linewidth: Number
    open var linecap: String
    open var linejoin: String
    open var map: Texture?
    open fun setValues(parameters: LineBasicMaterialParameters)
    override fun setValues(values: MaterialParameters)
}