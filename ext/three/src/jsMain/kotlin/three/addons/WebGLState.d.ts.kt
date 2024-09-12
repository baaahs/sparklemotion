@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external open class WebGLColorBuffer {
    open fun setMask(colorMask: Boolean)
    open fun setLocked(lock: Boolean)
    open fun setClear(r: Number, g: Number, b: Number, a: Number, premultipliedAlpha: Boolean)
    open fun reset()
}

external open class WebGLDepthBuffer {
    open fun setTest(depthTest: Boolean)
    open fun setMask(depthMask: Boolean)
    open fun setFunc(depthFunc: Any)
    open fun setLocked(lock: Boolean)
    open fun setClear(depth: Number)
    open fun reset()
}

external open class WebGLStencilBuffer {
    open fun setTest(stencilTest: Boolean)
    open fun setMask(stencilMask: Number)
    open fun setFunc(stencilFunc: Number, stencilRef: Number, stencilMask: Number)
    open fun setOp(stencilFail: Number, stencilZFail: Number, stencilZPass: Number)
    open fun setLocked(lock: Boolean)
    open fun setClear(stencil: Number)
    open fun reset()
}

external interface `T$25` {
    var color: WebGLColorBuffer
    var depth: WebGLDepthBuffer
    var stencil: WebGLStencilBuffer
}

external open class WebGLState(gl: WebGLRenderingContext) {
    open var buffers: `T$25`
    open fun enable(id: Number)
    open fun disable(id: Number)
    open fun bindFramebuffer(target: Number, framebuffer: WebGLFramebuffer?)
    open fun drawBuffers(renderTarget: WebGLRenderTarget__0?, framebuffer: WebGLFramebuffer?)
    open fun useProgram(program: Any): Boolean
    open fun setBlending(blending: Any, blendEquation: Any = definedExternally, blendSrc: Any = definedExternally, blendDst: Any = definedExternally, blendEquationAlpha: Any = definedExternally, blendSrcAlpha: Any = definedExternally, blendDstAlpha: Any = definedExternally, premultiplyAlpha: Boolean = definedExternally)
    open fun setMaterial(material: Material, frontFaceCW: Boolean)
    open fun setFlipSided(flipSided: Boolean)
    open fun setCullFace(cullFace: Any)
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