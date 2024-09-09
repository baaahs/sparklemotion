@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface MeshDistanceMaterialParameters : MaterialParameters {
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
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
    var farDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var nearDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var referencePosition: Vector3?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshDistanceMaterial(parameters: MeshDistanceMaterialParameters = definedExternally) : Material {
    override var type: String
    open var map: Texture?
    open var alphaMap: Texture?
    open var displacementMap: Texture?
    open var displacementScale: Number
    open var displacementBias: Number
    open var farDistance: Number
    open var nearDistance: Number
    open var referencePosition: Vector3
    open var skinning: Boolean
    open var morphTargets: Boolean
    override var fog: Boolean
    open fun setValues(parameters: MeshDistanceMaterialParameters)
    override fun setValues(values: MaterialParameters)
}