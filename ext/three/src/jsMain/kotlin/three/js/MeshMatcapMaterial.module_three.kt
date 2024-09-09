@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import kotlin.js.Json

external interface MeshMatcapMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var matcap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var bumpMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var bumpScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var normalMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var normalMapType: NormalMapTypes?
        get() = definedExternally
        set(value) = definedExternally
    var normalScale: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var displacementMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var displacementScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var displacementBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var skinning: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var morphTargets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var morphNormals: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshMatcapMaterial(parameters: MeshMatcapMaterialParameters = definedExternally) : Material {
    override var type: String
    override var defines: Json
    open var color: Color
    open var matcap: Texture?
    open var map: Texture?
    open var bumpMap: Texture?
    open var bumpScale: Number
    open var normalMap: Texture?
    open var normalMapType: NormalMapTypes
    open var normalScale: Vector2
    open var displacementMap: Texture?
    open var displacementScale: Number
    open var displacementBias: Number
    open var alphaMap: Texture?
    open var skinning: Boolean
    open var morphTargets: Boolean
    open var morphNormals: Boolean
    open fun setValues(parameters: MeshMatcapMaterialParameters)
    override fun setValues(values: MaterialParameters)
}