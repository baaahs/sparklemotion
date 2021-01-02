@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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