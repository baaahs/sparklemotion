@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class SessionLightProbe(xrLight: XREstimatedLight, renderer: WebGLRenderer, lightProbe: LightProbe, environmentEstimation: Boolean, estimationStartCallback: () -> Unit) {
    open var xrLight: XREstimatedLight
    open var renderer: WebGLRenderer
    open var lightProbe: LightProbe
    open var xrWebGLBinding: XRWebGLBinding?
    open var estimationStartCallback: () -> Unit
    open var frameCallback: (self: SessionLightProbe, time: Number, xrFrame: XRFrame) -> Unit
    open var updateReflection: () -> Unit
    open var onXRFrame: XRFrameRequestCallback
    open var dispose: () -> Unit
}

external interface XREstimatedLightEventMap : Object3DEventMap {
    var estimationstart: Any
    var estimationend: Any
}

open external class XREstimatedLight(renderer: WebGLRenderer, environmentEstimation: Boolean = definedExternally) : Group {
    open var lightProbe: LightProbe
    open var directionalLight: DirectionalLight
    open var environment: Texture
}