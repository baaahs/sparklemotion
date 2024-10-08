@file:JsModule("three")
@file:JsNonModule
package three

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

external interface LineDashedMaterialParameters : LineBasicMaterialParameters {
    var scale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LineDashedMaterial(parameters: LineDashedMaterialParameters = definedExternally) : LineBasicMaterial {
    open val isLineDashedMaterial: Boolean
    override var type: String
    open var scale: Number
    open var dashSize: Number
    open var gapSize: Number
    open fun setValues(parameters: LineDashedMaterialParameters)
    override fun setValues(parameters: LineBasicMaterialParameters)
    override fun setValues(values: MaterialParameters)
}