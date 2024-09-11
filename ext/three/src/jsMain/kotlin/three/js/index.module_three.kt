@file:JsModule("three")
@file:JsNonModule
package three.js

import org.khronos.webgl.*
import org.w3c.dom.DOMPointInit
import org.w3c.dom.DOMPointReadOnly
import org.w3c.dom.Element
import org.w3c.dom.EventInit
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import web.gamepad.Gamepad
import web.gl.GLint
import web.gl.GLsizei
import web.gl.WebGL2RenderingContext
import web.time.DOMHighResTimeStamp
import kotlin.js.Promise

external class XRSystemDeviceChangeEvent : Event {
    override var type: String /* "devicechange" */
}

external interface XRSystemDeviceChangeEventHandler {
    @nativeInvoke
    operator fun invoke(event: XRSystemDeviceChangeEvent): Any
}

external interface XRSystemEventMap {
    var sessiongranted: XRSystemSessionGrantedEvent
    var devicechange: XRSystemDeviceChangeEvent
}

open external class XRSystem : EventTarget {
//    open var onsessiongranted: XRSystemSessionGrantedEventHandler?
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun requestSession(mode: String /* "inline" | "immersive-vr" | "immersive-ar" */, options: XRSessionInit = definedExternally): Promise<XRSession>
//    open fun isSessionSupported(mode: String /* "inline" | "immersive-vr" | "immersive-ar" */): Promise<Boolean>
//    open var ondevicechange: XRSystemDeviceChangeEventHandler?
//    open fun <K : String> addEventListener(type: K, listener: (self: XRSystem, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRSystem, ev: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRSystem, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRSystem, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRSystem, ev: Any) -> Any)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRSystem, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

open external class XRViewport {
    open val x: Number
    open val y: Number
    open val width: Number
    open val height: Number
}

open external class XRSpace : EventTarget {
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
}

external interface XRRenderStateInit {
    var layers: Array<XRLayer>?
        get() = definedExternally
        set(value) = definedExternally
    var baseLayer: XRWebGLLayer?
        get() = definedExternally
        set(value) = definedExternally
    var depthFar: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthNear: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inlineVerticalFieldOfView: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRRenderState {
    open val layers: Array<XRLayer>?
    open val baseLayer: XRWebGLLayer?
    open val depthFar: Number
    open val depthNear: Number
    open val inlineVerticalFieldOfView: Number?
}

external interface XRReferenceSpaceEventInit : EventInit {
    var referenceSpace: XRReferenceSpace?
        get() = definedExternally
        set(value) = definedExternally
    var transform: XRRigidTransform?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRReferenceSpaceEvent(type: String /* "reset" */, eventInitDict: XRReferenceSpaceEventInit = definedExternally) : Event {
//    open val bubbles: Boolean
//    open var cancelBubble: Boolean
//    open val cancelable: Boolean
//    open val composed: Boolean
//    open val currentTarget: EventTarget?
//    open val defaultPrevented: Boolean
//    open val eventPhase: Number
//    open val isTrusted: Boolean
//    open var returnValue: Boolean
//    open val srcElement: EventTarget?
//    open val target: EventTarget?
//    open val timeStamp: Number
//    open val type: String /* "reset" */
//    open fun composedPath(): Array<EventTarget>
//    open fun initEvent(type: String, bubbles: Boolean = definedExternally, cancelable: Boolean = definedExternally)
//    open fun preventDefault()
//    open fun stopImmediatePropagation()
//    open fun stopPropagation()
//    open val AT_TARGET: Number
//    open val BUBBLING_PHASE: Number
//    open val CAPTURING_PHASE: Number
//    open val NONE: Number
//    open val referenceSpace: XRReferenceSpace
//    open val transform: XRRigidTransform?
}

external interface XRReferenceSpaceEventHandler {
    @nativeInvoke
    operator fun invoke(event: XRReferenceSpaceEvent): Any
}

external interface XRReferenceSpaceEventMap {
    var reset: XRReferenceSpaceEvent
}

open external class XRReferenceSpace {
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun getOffsetReferenceSpace(originOffset: XRRigidTransform): XRReferenceSpace
//    open var onreset: XRReferenceSpaceEventHandler
//    open fun <K : String> addEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

open external class XRBoundedReferenceSpace {
//    open fun getOffsetReferenceSpace(originOffset: XRRigidTransform): XRReferenceSpace
//    open var onreset: XRReferenceSpaceEventHandler
//    open fun <K : String> addEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener)
//    open fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRReferenceSpace, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener)
//    open fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open val boundsGeometry: Array<DOMPointReadOnly>
}

open external class XRInputSource {
    open val handedness: String /* "none" | "left" | "right" */
    open val targetRayMode: String /* "gaze" | "tracked-pointer" | "screen" | "transient-pointer" */
    open val targetRaySpace: XRSpace
    open val gripSpace: XRSpace?
    open val gamepad: Gamepad?
    open val profiles: Array<String>
    open val hand: XRHand?
}

open external class XRInputSourceArray {
//    @nativeGetter
//    open operator fun get(n: Number): XRInputSource?
//    @nativeSetter
//    open operator fun set(n: Number, value: XRInputSource)
//    open var length: Number
//    open fun entries(): IterableIterator<dynamic /* JsTuple<Number, XRInputSource> */>
//    open fun keys(): IterableIterator<Number>
//    open fun values(): IterableIterator<XRInputSource>
//    open fun forEach(callbackfn: (value: XRInputSource, index: Number, array: Array<XRInputSource>) -> Unit, thisArg: Any = definedExternally)
}

open external class XRPose {
    open val transform: XRRigidTransform
    open val linearVelocity: DOMPointReadOnly?
    open val angularVelocity: DOMPointReadOnly?
    open val emulatedPosition: Boolean
}

open external class XRFrame {
    open fun getDepthInformation(view: XRView): XRCPUDepthInformation?
    open var getJointPose: ((joint: XRJointSpace, baseSpace: XRSpace) -> XRJointPose?)?
    open val detectedMeshes: XRMeshSet?
    open val detectedPlanes: XRPlaneSet?
    open fun getHitTestResults(hitTestSource: XRHitTestSource): Array<XRHitTestResult>
    open fun getHitTestResultsForTransientInput(hitTestSource: XRTransientInputHitTestSource): Array<XRTransientInputHitTestResult>
    open var trackedAnchors: XRAnchorSet?
    open var createAnchor: ((pose: XRRigidTransform, space: XRSpace) -> Promise<XRAnchor>?)?
    open val session: XRSession
    open val predictedDisplayTime: DOMHighResTimeStamp
    open fun getPose(space: XRSpace, baseSpace: XRSpace): XRPose?
    open fun getViewerPose(referenceSpace: XRReferenceSpace): XRViewerPose?
}

external interface XRInputSourceEventInit : EventInit {
    var frame: XRFrame?
        get() = definedExternally
        set(value) = definedExternally
    var inputSource: XRInputSource?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRInputSourceEvent(type: String /* "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart" */, eventInitDict: XRInputSourceEventInit = definedExternally) : Event {
    override val type: String /* "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart" */
    open val frame: XRFrame
    open val inputSource: XRInputSource
}

external interface XRInputSourceEventHandler {
    @nativeInvoke
    operator fun invoke(evt: XRInputSourceEvent): Any
}

external interface XRSessionEventInit : EventInit {
    var session: XRSession
}

open external class XRSessionEvent(type: String /* "end" | "visibilitychange" | "frameratechange" */, eventInitDict: XRSessionEventInit = definedExternally) : Event {
    open val session: XRSession
}

external interface XRSessionEventHandler {
    @nativeInvoke
    operator fun invoke(evt: XRSessionEvent): Any
}

external interface XRSessionInit {
    var depthSensing: XRDepthStateInit?
        get() = definedExternally
        set(value) = definedExternally
    var domOverlay: XRDOMOverlayInit?
        get() = definedExternally
        set(value) = definedExternally
    var optionalFeatures: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var requiredFeatures: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface XRSessionEventMap {
    var inputsourceschange: XRInputSourcesChangeEvent
    var end: XRSessionEvent
    var visibilitychange: XRSessionEvent
    var frameratechange: XRSessionEvent
    var select: XRInputSourceEvent
    var selectstart: XRInputSourceEvent
    var selectend: XRInputSourceEvent
    var squeeze: XRInputSourceEvent
    var squeezestart: XRInputSourceEvent
    var squeezeend: XRInputSourceEvent
}

open external class XRSession : EventTarget {
//    open val depthUsage: String? /* "cpu-optimized" | "gpu-optimized" */
//    open val depthDataFormat: String? /* "luminance-alpha" | "float32" | "unsigned-short" */
//    open val domOverlayState: XRDOMOverlayState?
//    open val initiateRoomCapture: (() -> Promise<Nothing?>)?
//    open var requestHitTestSource: ((options: XRHitTestOptionsInit) -> Promise<XRHitTestSource>?)?
//    open var requestHitTestSourceForTransientInput: ((options: XRTransientInputHitTestOptionsInit) -> Promise<XRTransientInputHitTestSource>?)?
//    open var requestHitTest: ((ray: XRRay, referenceSpace: XRReferenceSpace) -> Promise<Array<XRHitResult>>?)?
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open val inputSources: XRInputSourceArray
//    open val renderState: XRRenderState
//    open val environmentBlendMode: String /* "opaque" | "additive" | "alpha-blend" */
//    open val visibilityState: String /* "visible" | "visible-blurred" | "hidden" */
//    open val frameRate: Number?
//    open val supportedFrameRates: Float32Array?
//    open val enabledFeatures: Array<String>?
//    open val isSystemKeyboardSupported: Boolean
//    open fun cancelAnimationFrame(id: Number)
//    open fun end(): Promise<Unit>
//    open fun requestAnimationFrame(callback: XRFrameRequestCallback): Number
//    open fun requestReferenceSpace(type: String /* "viewer" | "local" | "local-floor" | "bounded-floor" | "unbounded" */): Promise<dynamic /* XRReferenceSpace | XRBoundedReferenceSpace */>
//    open fun updateRenderState(renderStateInit: XRRenderStateInit = definedExternally): Promise<Unit>
//    open fun updateTargetFrameRate(rate: Number): Promise<Unit>
//    open var onend: XRSessionEventHandler
//    open var oninputsourceschange: XRInputSourcesChangeEventHandler
//    open var onselect: XRInputSourceEventHandler
//    open var onselectstart: XRInputSourceEventHandler
//    open var onselectend: XRInputSourceEventHandler
//    open var onsqueeze: XRInputSourceEventHandler
//    open var onsqueezestart: XRInputSourceEventHandler
//    open var onsqueezeend: XRInputSourceEventHandler
//    open var onvisibilitychange: XRSessionEventHandler
//    open var onframeratechange: XRSessionEventHandler
//    open fun <K : String> addEventListener(type: K, listener: (self: XRSession, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRSession, ev: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, listener: (self: XRSession, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRSession, ev: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRSession, ev: Any) -> Any)
//    open fun <K : String> removeEventListener(type: K, listener: (self: XRSession, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

open external class XRViewerPose {
    open val transform: XRRigidTransform
    open val linearVelocity: DOMPointReadOnly?
    open val angularVelocity: DOMPointReadOnly?
    open val emulatedPosition: Boolean
    open val views: Array<XRView>
}

open external class XRRigidTransform(position: DOMPointInit = definedExternally, direction: DOMPointInit = definedExternally) {
    open val position: DOMPointReadOnly
    open val orientation: DOMPointReadOnly
    open val matrix: Float32Array
    open val inverse: XRRigidTransform
}

open external class XRView {
    open val eye: String /* "none" | "left" | "right" */
    open val projectionMatrix: Float32Array
    open val transform: XRRigidTransform
    open val recommendedViewportScale: Number?
    open fun requestViewportScale(scale: Number)
}

external class XRInputSourcesChangeEvent : XRSessionEvent {
    val removed: Array<XRInputSource>
    val added: Array<XRInputSource>
}

//external interface XRInputSourcesChangeEventHandler {
//    @nativeInvoke
//    operator fun invoke(evt: XRInputSourcesChangeEvent): Any
//}

open external class XRAnchor {
    open var anchorSpace: XRSpace
    open fun delete()
}

open external class XRRay {
    open val origin: DOMPointReadOnly
    open val direction: DOMPointReadOnly
    open val matrix: Float32Array
    constructor(transformOrOrigin: XRRigidTransform = definedExternally, direction: DOMPointInit = definedExternally)
    constructor()
    constructor(transformOrOrigin: XRRigidTransform = definedExternally)
    constructor(transformOrOrigin: DOMPointInit = definedExternally, direction: DOMPointInit = definedExternally)
    constructor(transformOrOrigin: DOMPointInit = definedExternally)
}

open external class XRTransientInputHitTestResult {
    open val inputSource: XRInputSource
    open val results: Array<XRHitTestResult>
    open var prototype: XRTransientInputHitTestResult
}

open external class XRHitTestResult {
    open fun getPose(baseSpace: XRSpace): XRPose?
    open var createAnchor: ((pose: XRRigidTransform) -> Promise<XRAnchor>?)?
}

open external class XRHitTestSource {
    open fun cancel()
}

open external class XRTransientInputHitTestSource {
    open fun cancel()
}

external interface XRHitTestOptionsInit {
    var space: XRSpace
    var entityTypes: Array<String? /* "point" | "plane" | "mesh" */>?
        get() = definedExternally
        set(value) = definedExternally
    var offsetRay: XRRay?
        get() = definedExternally
        set(value) = definedExternally
}

external interface XRTransientInputHitTestOptionsInit {
    var profile: String
    var entityTypes: Array<String? /* "point" | "plane" | "mesh" */>?
        get() = definedExternally
        set(value) = definedExternally
    var offsetRay: XRRay?
        get() = definedExternally
        set(value) = definedExternally
}

external interface XRHitResult {
    var hitMatrix: Float32Array
}

open external class XRPlane {
    open var orientation: String /* "horizontal" | "vertical" */
    open var planeSpace: XRSpace
    open var polygon: Array<DOMPointReadOnly>
    open var lastChangedTime: DOMHighResTimeStamp
    open var semanticLabel: String?
}

open external class XRMesh {
    open var meshSpace: XRSpace
    open var vertices: Float32Array
    open var indices: Uint32Array
    open var lastChangedTime: DOMHighResTimeStamp
    open var semanticLabel: String?
}

//open external class XRJointSpace {
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    open val jointName: String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */
//}

open external class XRJointPose {
    open val transform: XRRigidTransform
    open val linearVelocity: DOMPointReadOnly?
    open val angularVelocity: DOMPointReadOnly?
    open val emulatedPosition: Boolean
    open val radius: Number?
}

open external class XRHand /*: Map<String *//* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip", XRJointSpace> */ {
//    open fun clear()
//    open fun delete(key: String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */): Boolean
//    open fun forEach(callbackfn: (value: XRJointSpace, key: String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */, map: Map<String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */, XRJointSpace>) -> Unit, thisArg: Any = definedExternally)
//    open fun get(key: String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */): XRJointSpace?
//    open fun has(key: String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */): Boolean
//    open fun set(key: String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */, value: XRJointSpace): XRHand /* this */
//    open val size: Number
//    open val WRIST: Number
//    open val THUMB_METACARPAL: Number
//    open val THUMB_PHALANX_PROXIMAL: Number
//    open val THUMB_PHALANX_DISTAL: Number
//    open val THUMB_PHALANX_TIP: Number
//    open val INDEX_METACARPAL: Number
//    open val INDEX_PHALANX_PROXIMAL: Number
//    open val INDEX_PHALANX_INTERMEDIATE: Number
//    open val INDEX_PHALANX_DISTAL: Number
//    open val INDEX_PHALANX_TIP: Number
//    open val MIDDLE_METACARPAL: Number
//    open val MIDDLE_PHALANX_PROXIMAL: Number
//    open val MIDDLE_PHALANX_INTERMEDIATE: Number
//    open val MIDDLE_PHALANX_DISTAL: Number
//    open val MIDDLE_PHALANX_TIP: Number
//    open val RING_METACARPAL: Number
//    open val RING_PHALANX_PROXIMAL: Number
//    open val RING_PHALANX_INTERMEDIATE: Number
//    open val RING_PHALANX_DISTAL: Number
//    open val RING_PHALANX_TIP: Number
//    open val LITTLE_METACARPAL: Number
//    open val LITTLE_PHALANX_PROXIMAL: Number
//    open val LITTLE_PHALANX_INTERMEDIATE: Number
//    open val LITTLE_PHALANX_DISTAL: Number
//    open val LITTLE_PHALANX_TIP: Number
}

open external class XRLayer : EventTarget {
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
}

external interface XRWebGLLayerInit {
    var antialias: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencil: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreDepthValues: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var framebufferScaleFactor: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRWebGLLayer : XRLayer {
    constructor(session: XRSession, context: WebGLRenderingContext, layerInit: XRWebGLLayerInit = definedExternally)
    constructor(session: XRSession, context: WebGLRenderingContext)
    constructor(session: XRSession, context: WebGL2RenderingContext, layerInit: XRWebGLLayerInit = definedExternally)
    constructor(session: XRSession, context: WebGL2RenderingContext)
    open val antialias: Boolean
    open val ignoreDepthValues: Boolean
    open var fixedFoveation: Number?
    open val framebuffer: WebGLFramebuffer
    open val framebufferWidth: Number
    open val framebufferHeight: Number
    open fun getViewport(view: XRView): XRViewport?

    companion object {
        fun getNativeFramebufferScaleFactor(session: XRSession): Number
    }
}

external class XRLayerEvent : Event {
    override val type: String /* "redraw" */
    val layer: XRLayer
}

external interface XRCompositionLayerEventMap {
    var redraw: XRLayerEvent
}

open external class XRCompositionLayer {
//    open fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener?)
//    open fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListener?)
//    open fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open val layout: String /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
//    open var blendTextureSourceAlpha: Boolean
//    open var chromaticAberrationCorrection: Boolean?
//    open val mipLevels: Number
//    open var quality: String /* "default" | "text-optimized" | "graphics-optimized" */
//    open val needsRedraw: Boolean
//    open fun destroy()
//    open var space: XRSpace
//    open var onredraw: (evt: XRLayerEvent) -> Any
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface XRProjectionLayerInit {
    var scaleFactor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textureType: String? /* "texture" | "texture-array" */
        get() = definedExternally
        set(value) = definedExternally
    var colorFormat: GLenum?
        get() = definedExternally
        set(value) = definedExternally
    var depthFormat: GLenum?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRProjectionLayer {
//    open val layout: String /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
//    open var blendTextureSourceAlpha: Boolean
//    open var chromaticAberrationCorrection: Boolean?
//    open val mipLevels: Number
//    open var quality: String /* "default" | "text-optimized" | "graphics-optimized" */
//    open val needsRedraw: Boolean
//    open fun destroy()
//    open var space: XRSpace
//    open var onredraw: (evt: XRLayerEvent) -> Any
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener)
//    open fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener)
//    open fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open val textureWidth: Number
//    open val textureHeight: Number
//    open val textureArrayLength: Number
//    open val ignoreDepthValues: Number
//    open var fixedFoveation: Number
}

external interface XRLayerInit {
    var mipLevels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var viewPixelWidth: Number
    var viewPixelHeight: Number
    var isStatic: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var colorFormat: GLenum?
        get() = definedExternally
        set(value) = definedExternally
    var depthFormat: GLenum?
        get() = definedExternally
        set(value) = definedExternally
    var space: XRSpace
    var layout: String? /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface XRCylinderLayerInit : XRLayerInit {
    var textureType: String? /* "texture" | "texture-array" */
        get() = definedExternally
        set(value) = definedExternally
    var transform: XRRigidTransform
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var centralAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var aspectRatio: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRCylinderLayer {
//    open val layout: String /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
//    open var blendTextureSourceAlpha: Boolean
//    open var chromaticAberrationCorrection: Boolean?
//    open val mipLevels: Number
//    open var quality: String /* "default" | "text-optimized" | "graphics-optimized" */
//    open val needsRedraw: Boolean
//    open fun destroy()
//    open var space: XRSpace
//    open var onredraw: (evt: XRLayerEvent) -> Any
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener)
//    open fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener)
//    open fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open var transform: XRRigidTransform
//    open var radius: Number
//    open var centralAngle: Number
//    open var aspectRatio: Number
}

external interface XRQuadLayerInit : XRLayerInit {
    var textureType: String? /* "texture" | "texture-array" */
        get() = definedExternally
        set(value) = definedExternally
    var transform: XRRigidTransform?
        get() = definedExternally
        set(value) = definedExternally
    var width: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRQuadLayer {
//    open val layout: String /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
//    open var blendTextureSourceAlpha: Boolean
//    open var chromaticAberrationCorrection: Boolean?
//    open val mipLevels: Number
//    open var quality: String /* "default" | "text-optimized" | "graphics-optimized" */
//    open val needsRedraw: Boolean
//    open fun destroy()
//    open var space: XRSpace
//    open var onredraw: (evt: XRLayerEvent) -> Any
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener)
//    open fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener)
//    open fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open var transform: XRRigidTransform
//    open var width: Number
//    open var height: Number
}

external interface XREquirectLayerInit : XRLayerInit {
    var textureType: String? /* "texture" | "texture-array" */
        get() = definedExternally
        set(value) = definedExternally
    var transform: XRRigidTransform?
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var centralHorizontalAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var upperVerticalAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lowerVerticalAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XREquirectLayer {
//    open val layout: String /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
//    open var blendTextureSourceAlpha: Boolean
//    open var chromaticAberrationCorrection: Boolean?
//    open val mipLevels: Number
//    open var quality: String /* "default" | "text-optimized" | "graphics-optimized" */
//    open val needsRedraw: Boolean
//    open fun destroy()
//    open var space: XRSpace
//    open var onredraw: (evt: XRLayerEvent) -> Any
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener)
//    open fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener)
//    open fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open var transform: XRRigidTransform
//    open var radius: Number
//    open var centralHorizontalAngle: Number
//    open var upperVerticalAngle: Number
//    open var lowerVerticalAngle: Number
}

external interface XRCubeLayerInit : XRLayerInit {
    var orientation: DOMPointReadOnly?
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRCubeLayer {
//    open val layout: String /* "default" | "mono" | "stereo" | "stereo-left-right" | "stereo-top-bottom" */
//    open var blendTextureSourceAlpha: Boolean
//    open var chromaticAberrationCorrection: Boolean?
//    open val mipLevels: Number
//    open var quality: String /* "default" | "text-optimized" | "graphics-optimized" */
//    open val needsRedraw: Boolean
//    open fun destroy()
//    open var space: XRSpace
//    open var onredraw: (evt: XRLayerEvent) -> Any
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: Boolean = definedExternally)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun <K : String> addEventListener(type: K, callback: (evt: Any) -> Any, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun addEventListener(type: String, listener: EventListener)
//    open fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject)
//    override fun addEventListener(type: String, listener: EventListenerObject?)
//    open fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
//    open fun <K : String> removeEventListener(type: K, callback: (evt: Any) -> Any)
//    open fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListener)
//    open fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
//    open fun removeEventListener(type: String, listener: EventListenerObject)
//    override fun removeEventListener(type: String, callback: EventListenerObject?)
//    open fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open fun addEventListener(type: String, listener: EventListenerObject?)
//    override fun addEventListener(type: String, listener: EventListenerObject)
//    open fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
//    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
//    open fun dispatchEvent(event: Event): Boolean
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
//    open fun removeEventListener(type: String, callback: EventListenerObject?)
//    override fun removeEventListener(type: String, listener: EventListenerObject)
//    open fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
//    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
//    open var orientation: DOMPointReadOnly
}

open external class XRSubImage {
    open val viewport: XRViewport
}

open external class XRWebGLSubImage {
    open val viewport: XRViewport
    open val colorTexture: WebGLTexture
    open val depthStencilTexture: WebGLTexture
    open val imageIndex: Number
    open val textureWidth: Number
    open val textureHeight: Number
}

open external class XRWebGLBinding(session: XRSession, context: WebGLRenderingContext) {
    open fun getDepthInformation(view: XRView): XRWebGLDepthInformation?
    open val nativeProjectionScaleFactor: Number
    open fun createProjectionLayer(init: XRProjectionLayerInit = definedExternally): XRProjectionLayer
    open fun createQuadLayer(init: XRQuadLayerInit = definedExternally): XRQuadLayer
    open fun createCylinderLayer(init: XRCylinderLayerInit = definedExternally): XRCylinderLayer
    open fun createEquirectLayer(init: XREquirectLayerInit = definedExternally): XREquirectLayer
    open fun createCubeLayer(init: XRCubeLayerInit = definedExternally): XRCubeLayer
    open fun getSubImage(layer: XRCompositionLayer, frame: XRFrame, eye: String /* "none" | "left" | "right" */ = definedExternally): XRWebGLSubImage
    open fun getViewSubImage(layer: XRProjectionLayer, view: XRView): XRWebGLSubImage
}

open external class OVR_multiview2

external interface XRSessionGrant {
    var mode: String /* "inline" | "immersive-vr" | "immersive-ar" */
}

external class XRSystemSessionGrantedEvent : Event {
    override var type: String /* "sessiongranted" */
    var session: XRSessionGrant
}

external interface XRSystemSessionGrantedEventHandler {
    @nativeInvoke
    operator fun invoke(event: XRSystemSessionGrantedEvent): Any
}

open external class OCULUS_multiview {
    open fun framebufferTextureMultisampleMultiviewOVR(target: GLenum, attachment: GLenum, texture: WebGLTexture?, level: GLint, samples: GLsizei, baseViewIndex: GLint, numViews: GLsizei)
}

external interface XRDOMOverlayInit {
    var root: Element
}

external interface XRDOMOverlayState {
    var type: String /* "screen" | "floating" | "head-locked" */
}

external interface XRDepthStateInit {
    var usagePreference: Array<String /* "cpu-optimized" | "gpu-optimized" */>
    var dataFormatPreference: Array<String /* "luminance-alpha" | "float32" | "unsigned-short" */>
}

external interface XRDepthInformation {
    val width: Number
    val height: Number
    val normDepthBufferFromNormView: XRRigidTransform
    val rawValueToMeters: Number
}

external interface XRCPUDepthInformation : XRDepthInformation {
    val data: ArrayBuffer
    fun getDepthInMeters(x: Number, y: Number): Number
}

external interface XRWebGLDepthInformation : XRDepthInformation {
    val texture: WebGLTexture
    val textureType: String /* "texture" | "texture-array" */
    val imageIndex: Number?
        get() = definedExternally
}