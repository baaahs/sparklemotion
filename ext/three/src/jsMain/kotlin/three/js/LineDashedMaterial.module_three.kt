@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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
    override var type: String
    open var scale: Number
    open var dashSize: Number
    open var gapSize: Number
    open var isLineDashedMaterial: Boolean
    open fun setValues(parameters: LineDashedMaterialParameters)
    override fun setValues(parameters: LineBasicMaterialParameters)
}