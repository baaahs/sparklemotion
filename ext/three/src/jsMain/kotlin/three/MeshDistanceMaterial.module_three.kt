@file:JsModule("three")
@file:JsNonModule
package three

import kotlin.js.*
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
    open val isMeshDistanceMaterial: Boolean
    override var type: String
    open var map: Texture?
    open var alphaMap: Texture?
    open var displacementMap: Texture?
    open var displacementScale: Number
    open var displacementBias: Number
    open var fog: Boolean
    open fun setValues(parameters: MeshDistanceMaterialParameters)
    override fun setValues(values: MaterialParameters)
}