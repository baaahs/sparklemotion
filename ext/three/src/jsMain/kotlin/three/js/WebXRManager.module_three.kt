@file:JsModule("three")
@file:JsNonModule
package three.js

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

external interface `T$26` {
    var data: XRPlane
}

external interface `T$27` {
    var data: XRPlaneSet
}

external interface WebXRManagerEventMap {
    var sessionstart: Any
    var sessionend: Any
    var planeadded: `T$26`
    var planeremoved: `T$26`
    var planechanged: `T$26`
    var planesdetected: `T$27`
}

open external class WebXRManager(renderer: WebGLRenderer, gl: WebGLRenderingContext) : EventDispatcher/*<WebXRManagerEventMap>*/ {
    open var cameraAutoUpdate: Boolean
    open var enabled: Boolean
    open var isPresenting: Boolean
    open var getController: (index: Number) -> XRTargetRaySpace
    open var getControllerGrip: (index: Number) -> XRGripSpace
    open var getHand: (index: Number) -> XRHandSpace
    open var setFramebufferScaleFactor: (value: Number) -> Unit
    open var setReferenceSpaceType: (value: String /* "viewer" | "local" | "local-floor" | "bounded-floor" | "unbounded" */) -> Unit
    open var getReferenceSpace: () -> XRReferenceSpace?
    open var setReferenceSpace: (value: XRReferenceSpace) -> Unit
    open var getBaseLayer: () -> dynamic
    open var getBinding: () -> XRWebGLBinding
    open var getFrame: () -> XRFrame
    open var getSession: () -> XRSession?
    open var setSession: (value: XRSession?) -> Promise<Unit>
    open var getEnvironmentBlendMode: () -> String
    open var getDepthTexture: () -> Texture?
    open var updateCamera: (camera: PerspectiveCamera) -> Unit
    open var getCamera: () -> dynamic
    open var getFoveation: () -> Number?
    open var setFoveation: (value: Number) -> Unit
    open var hasDepthSensing: () -> Boolean
    open var getDepthSensingMesh: () -> Mesh<BufferGeometry<NormalBufferAttributes>, *>?
    open var setAnimationLoop: (callback: XRFrameRequestCallback?) -> Unit
    open var dispose: () -> Unit
}