@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface LineBasicMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
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
    var morphTargets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LineBasicMaterial(parameters: LineBasicMaterialParameters = definedExternally) : Material {
    override var type: String
    open var color: dynamic /* Color | String | Number */
    open var linewidth: Number
    open var linecap: String
    open var linejoin: String
    open var morphTargets: Boolean
    open fun setValues(parameters: LineBasicMaterialParameters)
    override fun setValues(values: MaterialParameters)
}