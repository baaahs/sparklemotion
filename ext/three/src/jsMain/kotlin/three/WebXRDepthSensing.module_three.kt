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

open external class WebXRDepthSensing {
    open var texture: Texture?
    open var mesh: Mesh<BufferGeometry<NormalBufferAttributes>, *>?
    open var depthNear: Number
    open var depthFar: Number
    open fun init(renderer: WebGLRenderer, depthData: XRWebGLDepthInformation, renderState: XRRenderState)
    open fun getMesh(cameraXR: dynamic /* typealias Omit = dynamic */): Mesh<BufferGeometry<NormalBufferAttributes>, *>?
    open fun reset()
    open fun getDepthTexture(): Texture?
}