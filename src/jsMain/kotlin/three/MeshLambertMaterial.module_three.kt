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

external interface MeshLambertMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var emissive: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var emissiveIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var emissiveMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var lightMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var lightMapIntensity: Number?
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
    var morphNormals: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshLambertMaterial(parameters: MeshLambertMaterialParameters = definedExternally) : Material {
    override var type: String
    open var color: Color
    open var emissive: Color
    open var emissiveIntensity: Number
    open var emissiveMap: Texture?
    open var map: Texture?
    open var lightMap: Texture?
    open var lightMapIntensity: Number
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
    open var morphNormals: Boolean
    open fun setValues(parameters: MeshLambertMaterialParameters)
    override fun setValues(values: MaterialParameters)
}