@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface MeshBasicMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    override var opacity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var aoMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var aoMapIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var specularMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var envMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var combine: Combine?
        get() = definedExternally
        set(value) = definedExternally
    var reflectivity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var refractionRatio: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wireframe: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinecap: String?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinejoin: String?
        get() = definedExternally
        set(value) = definedExternally
    var skinning: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var morphTargets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshBasicMaterial(parameters: MeshBasicMaterialParameters = definedExternally) : Material {
    override var type: String
    open var color: Color
    open var map: Texture?
    open var aoMap: Texture?
    open var aoMapIntensity: Number
    open var specularMap: Texture?
    open var alphaMap: Texture?
    open var envMap: Texture?
    open var combine: Combine
    open var reflectivity: Number
    open var refractionRatio: Number
    open var wireframe: Boolean
    open var wireframeLinewidth: Number
    open var wireframeLinecap: String
    open var wireframeLinejoin: String
    open var skinning: Boolean
    open var morphTargets: Boolean
    open fun setValues(parameters: MeshBasicMaterialParameters)
    override fun setValues(values: MaterialParameters)
}