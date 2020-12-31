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