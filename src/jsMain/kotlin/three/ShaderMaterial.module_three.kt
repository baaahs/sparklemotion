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

external interface `T$42` {
    var derivatives: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fragDepth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var drawBuffers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var shaderTextureLOD: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ShaderMaterialParameters : MaterialParameters {
    var uniforms: `T$8`?
        get() = definedExternally
        set(value) = definedExternally
    var vertexShader: String?
        get() = definedExternally
        set(value) = definedExternally
    var fragmentShader: String?
        get() = definedExternally
        set(value) = definedExternally
    var linewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wireframe: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lights: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var clipping: Boolean?
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
    var extensions: `T$42`?
        get() = definedExternally
        set(value) = definedExternally
    var glslVersion: GLSLVersion?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$43` {
    var derivatives: Boolean
    var fragDepth: Boolean
    var drawBuffers: Boolean
    var shaderTextureLOD: Boolean
}

open external class ShaderMaterial(parameters: ShaderMaterialParameters = definedExternally) : Material {
    override var type: String
    override var defines: Json
    open var uniforms: `T$8`
    open var vertexShader: String
    open var fragmentShader: String
    open var linewidth: Number
    open var wireframe: Boolean
    open var wireframeLinewidth: Number
    override var fog: Boolean
    open var lights: Boolean
    open var clipping: Boolean
    open var skinning: Boolean
    open var morphTargets: Boolean
    open var morphNormals: Boolean
    open var derivatives: Any
    open var extensions: `T$43`
    open var defaultAttributeValues: Any
    open var index0AttributeName: String?
    open var uniformsNeedUpdate: Boolean
    open var glslVersion: GLSLVersion?
    open fun setValues(parameters: ShaderMaterialParameters)
    override fun setValues(values: MaterialParameters)
    override fun toJSON(meta: Any): Any
}