@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface ShadowMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
}

open external class ShadowMaterial(parameters: ShadowMaterialParameters = definedExternally) : Material {
    override var type: String
    open var color: Color
    override var transparent: Boolean
}