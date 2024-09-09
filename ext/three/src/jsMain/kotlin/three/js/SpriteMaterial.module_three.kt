@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface SpriteMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var rotation: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sizeAttenuation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class SpriteMaterial(parameters: SpriteMaterialParameters = definedExternally) : Material {
    override var type: String
    open var color: Color
    open var map: Texture?
    open var alphaMap: Texture?
    open var rotation: Number
    open var sizeAttenuation: Boolean
    override var transparent: Boolean
    open var isSpriteMaterial: Boolean
    open fun setValues(parameters: SpriteMaterialParameters)
    override fun setValues(values: MaterialParameters)
    open fun copy(source: SpriteMaterial): SpriteMaterial /* this */
    override fun copy(material: Material): Material /* this */
}