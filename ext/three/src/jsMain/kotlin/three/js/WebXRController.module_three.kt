package three.js

import js.objects.Record
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

open external class XRJointSpace : Group__0 {
    open val jointRadius: Number?
}

typealias XRHandJoints = Record<String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */, XRJointSpace>

external interface XRHandInputState {
    var pinching: Boolean
}

external interface `T$20` {
    var data: XRInputSource
}

external interface `T$21` {
    var handedness: String /* "none" | "left" | "right" */
    var target: WebXRController
}

external interface WebXRSpaceEventMap : Object3DEventMap {
    var select: `T$20`
    var selectstart: `T$20`
    var selectend: `T$20`
    var squeeze: `T$20`
    var squeezestart: `T$20`
    var squeezeend: `T$20`
    var connected: `T$20`
    var disconnected: `T$20`
    var pinchend: `T$21`
    var pinchstart: `T$21`
    var move: Any
}

open external class XRHandSpace : Group<WebXRSpaceEventMap> {
    open val joints: Any
    open val inputState: XRHandInputState
}

open external class XRTargetRaySpace : Group<WebXRSpaceEventMap> {
    open var hasLinearVelocity: Boolean
    open val linearVelocity: Vector3
    open var hasAngularVelocity: Boolean
    open val angularVelocity: Vector3
}

open external class XRGripSpace : Group<WebXRSpaceEventMap> {
    open var hasLinearVelocity: Boolean
    open val linearVelocity: Vector3
    open var hasAngularVelocity: Boolean
    open val angularVelocity: Vector3
}

external interface `T$22` {
    var type: String /* "end" | "visibilitychange" | "frameratechange" | "select" | "selectend" | "selectstart" | "squeeze" | "squeezeend" | "squeezestart" | "disconnected" | "connected" */
    var data: XRInputSource?
        get() = definedExternally
        set(value) = definedExternally
}

open external class WebXRController {
    open fun getHandSpace(): XRHandSpace
    open fun getTargetRaySpace(): XRTargetRaySpace
    open fun getGripSpace(): XRGripSpace
    open fun dispatchEvent(event: `T$22`): WebXRController /* this */
    open fun connect(inputSource: XRInputSource): WebXRController /* this */
    open fun disconnect(inputSource: XRInputSource): WebXRController /* this */
    open fun update(inputSource: XRInputSource, frame: XRFrame, referenceSpace: XRReferenceSpace): WebXRController /* this */
}