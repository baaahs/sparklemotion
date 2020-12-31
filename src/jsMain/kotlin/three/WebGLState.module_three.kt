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

open external class WebGLColorBuffer {
    open fun setMask(colorMask: Boolean)
    open fun setLocked(lock: Boolean)
    open fun setClear(r: Number, g: Number, b: Number, a: Number, premultipliedAlpha: Boolean)
    open fun reset()
}

open external class WebGLDepthBuffer {
    open fun setTest(depthTest: Boolean)
    open fun setMask(depthMask: Boolean)
    open fun setFunc(depthFunc: DepthModes)
    open fun setLocked(lock: Boolean)
    open fun setClear(depth: Number)
    open fun reset()
}

open external class WebGLStencilBuffer {
    open fun setTest(stencilTest: Boolean)
    open fun setMask(stencilMask: Number)
    open fun setFunc(stencilFunc: Number, stencilRef: Number, stencilMask: Number)
    open fun setOp(stencilFail: Number, stencilZFail: Number, stencilZPass: Number)
    open fun setLocked(lock: Boolean)
    open fun setClear(stencil: Number)
    open fun reset()
}

external interface `T$15` {
    var color: WebGLColorBuffer
    var depth: WebGLDepthBuffer
    var stencil: WebGLStencilBuffer
}

open external class WebGLState(gl: WebGLRenderingContext, extensions: WebGLExtensions, capabilities: WebGLCapabilities) {
    open var buffers: `T$15`
    open fun initAttributes()
    open fun enableAttribute(attribute: Number)
    open fun enableAttributeAndDivisor(attribute: Number, meshPerAttribute: Number)
    open fun disableUnusedAttributes()
    open fun vertexAttribPointer(index: Number, size: Number, type: Number, normalized: Boolean, stride: Number, offset: Number)
    open fun enable(id: Number)
    open fun disable(id: Number)
    open fun useProgram(program: Any): Boolean
    open fun setBlending(blending: Blending, blendEquation: BlendingEquation = definedExternally, blendSrc: BlendingSrcFactor = definedExternally, blendDst: BlendingDstFactor = definedExternally, blendEquationAlpha: BlendingEquation = definedExternally, blendSrcAlpha: BlendingSrcFactor = definedExternally, blendDstAlpha: BlendingDstFactor = definedExternally, premultiplyAlpha: Boolean = definedExternally)
    open fun setMaterial(material: Material, frontFaceCW: Boolean)
    open fun setFlipSided(flipSided: Boolean)
    open fun setCullFace(cullFace: CullFace)
    open fun setLineWidth(width: Number)
    open fun setPolygonOffset(polygonoffset: Boolean, factor: Number = definedExternally, units: Number = definedExternally)
    open fun setScissorTest(scissorTest: Boolean)
    open fun activeTexture(webglSlot: Number)
    open fun bindTexture(webglType: Number, webglTexture: Any)
    open fun unbindTexture()
    open fun compressedTexImage2D(target: Number, level: Number, internalformat: Number, width: Number, height: Number, border: Number, data: ArrayBufferView)
    open fun texImage2D(target: Number, level: Number, internalformat: Number, width: Number, height: Number, border: Number, format: Number, type: Number, pixels: ArrayBufferView?)
    open fun texImage2D(target: Number, level: Number, internalformat: Number, format: Number, type: Number, source: Any)
    open fun texImage3D(target: Number, level: Number, internalformat: Number, width: Number, height: Number, depth: Number, border: Number, format: Number, type: Number, pixels: Any)
    open fun scissor(scissor: Vector4)
    open fun viewport(viewport: Vector4)
    open fun reset()
}