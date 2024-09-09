@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import kotlin.js.Json

external interface MeshPhysicalMaterialParameters : MeshStandardMaterialParameters {
    var clearcoat: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughnessMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalScale: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var reflectivity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sheen: Color?
        get() = definedExternally
        set(value) = definedExternally
    var transmission: Number?
        get() = definedExternally
        set(value) = definedExternally
    var transmissionMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshPhysicalMaterial(parameters: MeshPhysicalMaterialParameters) : MeshStandardMaterial {
    override var type: String
    override var defines: Json
    open var clearcoat: Number
    open var clearcoatMap: Texture?
    open var clearcoatRoughness: Number
    open var clearcoatRoughnessMap: Texture?
    open var clearcoatNormalScale: Vector2
    open var clearcoatNormalMap: Texture?
    open var reflectivity: Number
    open var sheen: Color?
    open var transmission: Number
    open var transmissionMap: Texture?
}