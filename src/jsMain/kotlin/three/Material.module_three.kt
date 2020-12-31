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

external interface MaterialParameters {
    var alphaTest: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendDst: BlendingDstFactor?
        get() = definedExternally
        set(value) = definedExternally
    var blendDstAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendEquation: BlendingEquation?
        get() = definedExternally
        set(value) = definedExternally
    var blendEquationAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blending: Blending?
        get() = definedExternally
        set(value) = definedExternally
    var blendSrc: dynamic /* BlendingSrcFactor? | BlendingDstFactor? */
        get() = definedExternally
        set(value) = definedExternally
    var blendSrcAlpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clipIntersection: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var clippingPlanes: Array<Plane>?
        get() = definedExternally
        set(value) = definedExternally
    var clipShadows: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var colorWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var defines: Any?
        get() = definedExternally
        set(value) = definedExternally
    var depthFunc: DepthModes?
        get() = definedExternally
        set(value) = definedExternally
    var depthTest: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depthWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var opacity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffset: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetFactor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetUnits: Number?
        get() = definedExternally
        set(value) = definedExternally
    var precision: String? /* 'highp' | 'mediump' | 'lowp' */
        get() = definedExternally
        set(value) = definedExternally
    var premultipliedAlpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var dithering: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var flatShading: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var side: Side?
        get() = definedExternally
        set(value) = definedExternally
    var shadowSide: Side?
        get() = definedExternally
        set(value) = definedExternally
    var toneMapped: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var transparent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var vertexColors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var visible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencilWrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFunc: StencilFunc?
        get() = definedExternally
        set(value) = definedExternally
    var stencilRef: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilMask: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stencilFail: StencilOp?
        get() = definedExternally
        set(value) = definedExternally
    var stencilZFail: StencilOp?
        get() = definedExternally
        set(value) = definedExternally
    var stencilZPass: StencilOp?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Material : EventDispatcher {
    open var alphaTest: Number
    open var blendDst: BlendingDstFactor
    open var blendDstAlpha: Number?
    open var blendEquation: BlendingEquation
    open var blendEquationAlpha: Number?
    open var blending: Blending
    open var blendSrc: dynamic /* BlendingSrcFactor | BlendingDstFactor */
    open var blendSrcAlpha: Number?
    open var clipIntersection: Boolean
    open var clippingPlanes: Any
    open var clipShadows: Boolean
    open var colorWrite: Boolean
    open var defines: Json
    open var depthFunc: DepthModes
    open var depthTest: Boolean
    open var depthWrite: Boolean
    open var fog: Boolean
    open var id: Number
    open var stencilWrite: Boolean
    open var stencilFunc: StencilFunc
    open var stencilRef: Number
    open var stencilMask: Number
    open var stencilFail: StencilOp
    open var stencilZFail: StencilOp
    open var stencilZPass: StencilOp
    open var isMaterial: Boolean
    open var name: String
    open var needsUpdate: Boolean
    open var opacity: Number
    open var polygonOffset: Boolean
    open var polygonOffsetFactor: Number
    open var polygonOffsetUnits: Number
    open var precision: String /* 'highp' | 'mediump' | 'lowp' */
    open var premultipliedAlpha: Boolean
    open var dithering: Boolean
    open var flatShading: Boolean
    open var side: Side
    open var shadowSide: Side
    open var toneMapped: Boolean
    open var transparent: Boolean
    open var type: String
    open var uuid: String
    open var vertexColors: Boolean
    open var visible: Boolean
    open var userData: Any
    open var version: Number
    open fun clone(): Material /* this */
    open fun copy(material: Material): Material /* this */
    open fun dispose()
    open fun onBeforeCompile(shader: Shader, renderer: WebGLRenderer)
    open fun customProgramCacheKey(): String
    open fun setValues(values: MaterialParameters)
    open fun toJSON(meta: Any = definedExternally): Any
}