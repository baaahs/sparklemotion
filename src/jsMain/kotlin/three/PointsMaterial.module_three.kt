@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface PointsMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sizeAttenuation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var morphTargets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class PointsMaterial(parameters: PointsMaterialParameters = definedExternally) : Material {
    override var type: String
    open var color: Color
    open var map: Texture?
    open var alphaMap: Texture?
    open var size: Number
    open var sizeAttenuation: Boolean
    open var morphTargets: Boolean
    open fun setValues(parameters: PointsMaterialParameters)
    override fun setValues(values: MaterialParameters)
}