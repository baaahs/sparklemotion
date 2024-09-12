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

external interface AddEventListenerOptions : EventListenerOptions {
    var once: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var passive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesCbcParams : Algorithm {
    var iv: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesCtrParams : Algorithm {
    var counter: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var length: Number
}

external interface AesDerivedKeyParams : Algorithm {
    var length: Number
}

external interface AesGcmParams : Algorithm {
    var additionalData: dynamic /* Int8Array? | Int16Array? | Int32Array? | Uint8Array? | Uint16Array? | Uint32Array? | Uint8ClampedArray? | Float32Array? | Float64Array? | DataView? | ArrayBuffer? */
        get() = definedExternally
        set(value) = definedExternally
    var iv: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var tagLength: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesKeyAlgorithm : KeyAlgorithm {
    var length: Number
}

external interface AesKeyGenParams : Algorithm {
    var length: Number
}

external interface Algorithm {
    var name: String
}

external interface AnalyserOptions : AudioNodeOptions {
    var fftSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxDecibels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var minDecibels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var smoothingTimeConstant: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AnimationEventInit : EventInit {
    var animationName: String?
        get() = definedExternally
        set(value) = definedExternally
    var elapsedTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var pseudoElement: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AnimationPlaybackEventInit : EventInit {
    var currentTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var timelineTime: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AssignedNodesOptions {
    var flatten: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AudioBufferOptions {
    var length: Number
    var numberOfChannels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sampleRate: Number
}

external interface AudioBufferSourceOptions {
    var buffer: AudioBuffer?
        get() = definedExternally
        set(value) = definedExternally
    var detune: Number?
        get() = definedExternally
        set(value) = definedExternally
    var loop: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var loopEnd: Number?
        get() = definedExternally
        set(value) = definedExternally
    var loopStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var playbackRate: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AudioContextOptions {
    var latencyHint: dynamic /* "balanced" | "interactive" | "playback" | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var sampleRate: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AudioNodeOptions {
    var channelCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var channelCountMode: String? /* "clamped-max" | "explicit" | "max" */
        get() = definedExternally
        set(value) = definedExternally
    var channelInterpretation: String? /* "discrete" | "speakers" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AudioProcessingEventInit : EventInit {
    var inputBuffer: AudioBuffer
    var outputBuffer: AudioBuffer
    var playbackTime: Number
}

external interface AudioTimestamp {
    var contextTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var performanceTime: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AuthenticationExtensionsClientInputs {
    var appid: String?
        get() = definedExternally
        set(value) = definedExternally
    var authnSel: AuthenticatorSelectionList?
        get() = definedExternally
        set(value) = definedExternally
    var exts: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var loc: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var txAuthGeneric: txAuthGenericArg?
        get() = definedExternally
        set(value) = definedExternally
    var txAuthSimple: String?
        get() = definedExternally
        set(value) = definedExternally
    var uvi: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var uvm: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AuthenticatorSelectionCriteria {
    var authenticatorAttachment: String? /* "cross-platform" | "platform" */
        get() = definedExternally
        set(value) = definedExternally
    var requireResidentKey: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var userVerification: String? /* "discouraged" | "preferred" | "required" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BiquadFilterOptions : AudioNodeOptions {
    var Q: Number?
        get() = definedExternally
        set(value) = definedExternally
    var detune: Number?
        get() = definedExternally
        set(value) = definedExternally
    var frequency: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gain: Number?
        get() = definedExternally
        set(value) = definedExternally
    var type: String? /* "allpass" | "bandpass" | "highpass" | "highshelf" | "lowpass" | "lowshelf" | "notch" | "peaking" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BlobPropertyBag {
    var endings: String? /* "native" | "transparent" */
        get() = definedExternally
        set(value) = definedExternally
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CacheQueryOptions {
    var ignoreMethod: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreSearch: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreVary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CanvasRenderingContext2DSettings {
    var alpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var desynchronized: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ChannelMergerOptions : AudioNodeOptions {
    var numberOfInputs: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ChannelSplitterOptions : AudioNodeOptions {
    var numberOfOutputs: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ClipboardEventInit : EventInit {
    var clipboardData: DataTransfer?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CloseEventInit : EventInit {
    var code: Number?
        get() = definedExternally
        set(value) = definedExternally
    var reason: String?
        get() = definedExternally
        set(value) = definedExternally
    var wasClean: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CompositionEventInit : UIEventInit {
    var data: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ComputedEffectTiming : EffectTiming {
    var activeDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var currentIteration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var endTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var localTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var progress: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConfirmSiteSpecificExceptionsInformation : ExceptionInformation {
    var arrayOfDomainStrings: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConstantSourceOptions {
    var offset: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConstrainBooleanParameters {
    var exact: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ideal: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConstrainDOMStringParameters {
    var exact: dynamic /* String? | Array<String>? */
        get() = definedExternally
        set(value) = definedExternally
    var ideal: dynamic /* String? | Array<String>? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConstrainDoubleRange : DoubleRange {
    var exact: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ideal: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConstrainULongRange : ULongRange {
    var exact: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ideal: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConvolverOptions : AudioNodeOptions {
    var buffer: AudioBuffer?
        get() = definedExternally
        set(value) = definedExternally
    var disableNormalization: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CredentialCreationOptions {
    var publicKey: PublicKeyCredentialCreationOptions?
        get() = definedExternally
        set(value) = definedExternally
    var signal: AbortSignal?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CredentialRequestOptions {
    var mediation: String? /* "optional" | "required" | "silent" */
        get() = definedExternally
        set(value) = definedExternally
    var publicKey: PublicKeyCredentialRequestOptions?
        get() = definedExternally
        set(value) = definedExternally
    var signal: AbortSignal?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CustomEventInit<T> : EventInit {
    var detail: T?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DOMMatrix2DInit {
    var a: Number?
        get() = definedExternally
        set(value) = definedExternally
    var b: Number?
        get() = definedExternally
        set(value) = definedExternally
    var c: Number?
        get() = definedExternally
        set(value) = definedExternally
    var d: Number?
        get() = definedExternally
        set(value) = definedExternally
    var e: Number?
        get() = definedExternally
        set(value) = definedExternally
    var f: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m11: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m12: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m21: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m22: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m41: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m42: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DOMMatrixInit : DOMMatrix2DInit {
    var is2D: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var m13: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m14: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m23: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m24: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m31: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m32: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m33: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m34: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m43: Number?
        get() = definedExternally
        set(value) = definedExternally
    var m44: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DOMPointInit {
    var w: Number?
        get() = definedExternally
        set(value) = definedExternally
    var x: Number?
        get() = definedExternally
        set(value) = definedExternally
    var y: Number?
        get() = definedExternally
        set(value) = definedExternally
    var z: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DOMRectInit {
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var width: Number?
        get() = definedExternally
        set(value) = definedExternally
    var x: Number?
        get() = definedExternally
        set(value) = definedExternally
    var y: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DelayOptions : AudioNodeOptions {
    var delayTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxDelayTime: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DeviceLightEventInit : EventInit {
    var value: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DeviceMotionEventAccelerationInit {
    var x: Number?
        get() = definedExternally
        set(value) = definedExternally
    var y: Number?
        get() = definedExternally
        set(value) = definedExternally
    var z: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DeviceMotionEventInit : EventInit {
    var acceleration: DeviceMotionEventAccelerationInit?
        get() = definedExternally
        set(value) = definedExternally
    var accelerationIncludingGravity: DeviceMotionEventAccelerationInit?
        get() = definedExternally
        set(value) = definedExternally
    var interval: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotationRate: DeviceMotionEventRotationRateInit?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DeviceMotionEventRotationRateInit {
    var alpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var beta: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gamma: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DeviceOrientationEventInit : EventInit {
    var absolute: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var beta: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gamma: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DevicePermissionDescriptor : PermissionDescriptor {
    var deviceId: String?
        get() = definedExternally
        set(value) = definedExternally
    override var name: String /* "camera" | "microphone" | "speaker" */
}

external interface DocumentTimelineOptions {
    var originTime: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DoubleRange {
    var max: Number?
        get() = definedExternally
        set(value) = definedExternally
    var min: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DragEventInit : MouseEventInit {
    var dataTransfer: DataTransfer?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DynamicsCompressorOptions : AudioNodeOptions {
    var attack: Number?
        get() = definedExternally
        set(value) = definedExternally
    var knee: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ratio: Number?
        get() = definedExternally
        set(value) = definedExternally
    var release: Number?
        get() = definedExternally
        set(value) = definedExternally
    var threshold: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EcKeyGenParams : Algorithm {
    var namedCurve: NamedCurve
}

external interface EcKeyImportParams : Algorithm {
    var namedCurve: NamedCurve
}

external interface EcdhKeyDeriveParams : Algorithm {
    var public: CryptoKey
}

external interface EcdsaParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}

external interface EffectTiming {
    var delay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var direction: String? /* "alternate" | "alternate-reverse" | "normal" | "reverse" */
        get() = definedExternally
        set(value) = definedExternally
    var duration: dynamic /* Number? | String? */
        get() = definedExternally
        set(value) = definedExternally
    var easing: String?
        get() = definedExternally
        set(value) = definedExternally
    var endDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var fill: String? /* "auto" | "backwards" | "both" | "forwards" | "none" */
        get() = definedExternally
        set(value) = definedExternally
    var iterationStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iterations: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ElementCreationOptions {
    var `is`: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ElementDefinitionOptions {
    var extends: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ErrorEventInit : EventInit {
    var colno: Number?
        get() = definedExternally
        set(value) = definedExternally
    var error: Any?
        get() = definedExternally
        set(value) = definedExternally
    var filename: String?
        get() = definedExternally
        set(value) = definedExternally
    var lineno: Number?
        get() = definedExternally
        set(value) = definedExternally
    var message: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EventInit {
    var bubbles: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var cancelable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var composed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EventListenerOptions {
    var capture: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EventModifierInit : UIEventInit {
    var altKey: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ctrlKey: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var metaKey: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierAltGraph: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierCapsLock: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierFn: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierFnLock: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierHyper: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierNumLock: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierScrollLock: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierSuper: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierSymbol: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifierSymbolLock: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var shiftKey: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExceptionInformation {
    var domain: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FilePropertyBag : BlobPropertyBag {
    var lastModified: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FocusEventInit : UIEventInit {
    var relatedTarget: EventTarget?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FocusNavigationEventInit : EventInit {
    var navigationReason: String?
        get() = definedExternally
        set(value) = definedExternally
    var originHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originLeft: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originTop: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FocusNavigationOrigin {
    var originHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originLeft: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originTop: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FocusOptions {
    var preventScroll: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FullscreenOptions {
    var navigationUI: String? /* "auto" | "hide" | "show" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface GainOptions : AudioNodeOptions {
    var gain: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GamepadEventInit : EventInit {
    var gamepad: Gamepad
}

external interface GetNotificationOptions {
    var tag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GetRootNodeOptions {
    var composed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface HashChangeEventInit : EventInit {
    var newURL: String?
        get() = definedExternally
        set(value) = definedExternally
    var oldURL: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface HmacImportParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface HmacKeyGenParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IDBIndexParameters {
    var multiEntry: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var unique: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IDBObjectStoreParameters {
    var autoIncrement: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var keyPath: dynamic /* String? | Array<String>? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface IDBVersionChangeEventInit : EventInit {
    var newVersion: Number?
        get() = definedExternally
        set(value) = definedExternally
    var oldVersion: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IIRFilterOptions : AudioNodeOptions {
    var feedback: Array<Number>
    var feedforward: Array<Number>
}

external interface ImageBitmapOptions {
    var colorSpaceConversion: String? /* "default" | "none" */
        get() = definedExternally
        set(value) = definedExternally
    var imageOrientation: String? /* "flipY" | "none" */
        get() = definedExternally
        set(value) = definedExternally
    var premultiplyAlpha: String? /* "default" | "none" | "premultiply" */
        get() = definedExternally
        set(value) = definedExternally
    var resizeHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var resizeQuality: String? /* "high" | "low" | "medium" | "pixelated" */
        get() = definedExternally
        set(value) = definedExternally
    var resizeWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ImageBitmapRenderingContextSettings {
    var alpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ImageEncodeOptions {
    var quality: Number?
        get() = definedExternally
        set(value) = definedExternally
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface InputEventInit : UIEventInit {
    var data: String?
        get() = definedExternally
        set(value) = definedExternally
    var inputType: String?
        get() = definedExternally
        set(value) = definedExternally
    var isComposing: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface JsonWebKey {
    var alg: String?
        get() = definedExternally
        set(value) = definedExternally
    var crv: String?
        get() = definedExternally
        set(value) = definedExternally
    var d: String?
        get() = definedExternally
        set(value) = definedExternally
    var dp: String?
        get() = definedExternally
        set(value) = definedExternally
    var dq: String?
        get() = definedExternally
        set(value) = definedExternally
    var e: String?
        get() = definedExternally
        set(value) = definedExternally
    var ext: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var k: String?
        get() = definedExternally
        set(value) = definedExternally
    var key_ops: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var kty: String?
        get() = definedExternally
        set(value) = definedExternally
    var n: String?
        get() = definedExternally
        set(value) = definedExternally
    var oth: Array<RsaOtherPrimesInfo>?
        get() = definedExternally
        set(value) = definedExternally
    var p: String?
        get() = definedExternally
        set(value) = definedExternally
    var q: String?
        get() = definedExternally
        set(value) = definedExternally
    var qi: String?
        get() = definedExternally
        set(value) = definedExternally
    var use: String?
        get() = definedExternally
        set(value) = definedExternally
    var x: String?
        get() = definedExternally
        set(value) = definedExternally
    var y: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface KeyAlgorithm {
    var name: String
}

external interface KeyboardEventInit : EventModifierInit {
    var code: String?
        get() = definedExternally
        set(value) = definedExternally
    var isComposing: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var key: String?
        get() = definedExternally
        set(value) = definedExternally
    var location: Number?
        get() = definedExternally
        set(value) = definedExternally
    var repeat: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Keyframe {
    var composite: String? /* "accumulate" | "add" | "auto" | "replace" */
        get() = definedExternally
        set(value) = definedExternally
    var easing: String?
        get() = definedExternally
        set(value) = definedExternally
    var offset: Number?
        get() = definedExternally
        set(value) = definedExternally
    @nativeGetter
    operator fun get(property: String): dynamic /* String? | Number? */
    @nativeSetter
    operator fun set(property: String, value: String?)
    @nativeSetter
    operator fun set(property: String, value: Number?)
}

external interface KeyframeAnimationOptions : KeyframeEffectOptions {
    var id: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface KeyframeEffectOptions : EffectTiming {
    var composite: String? /* "accumulate" | "add" | "replace" */
        get() = definedExternally
        set(value) = definedExternally
    var iterationComposite: String? /* "accumulate" | "replace" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaElementAudioSourceOptions {
    var mediaElement: HTMLMediaElement
}

external interface MediaEncryptedEventInit : EventInit {
    var initData: ArrayBuffer?
        get() = definedExternally
        set(value) = definedExternally
    var initDataType: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaKeyMessageEventInit : EventInit {
    var message: ArrayBuffer
    var messageType: String /* "individualization-request" | "license-release" | "license-renewal" | "license-request" */
}

external interface MediaKeySystemConfiguration {
    var audioCapabilities: Array<MediaKeySystemMediaCapability>?
        get() = definedExternally
        set(value) = definedExternally
    var distinctiveIdentifier: String? /* "not-allowed" | "optional" | "required" */
        get() = definedExternally
        set(value) = definedExternally
    var initDataTypes: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var label: String?
        get() = definedExternally
        set(value) = definedExternally
    var persistentState: String? /* "not-allowed" | "optional" | "required" */
        get() = definedExternally
        set(value) = definedExternally
    var sessionTypes: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var videoCapabilities: Array<MediaKeySystemMediaCapability>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaKeySystemMediaCapability {
    var contentType: String?
        get() = definedExternally
        set(value) = definedExternally
    var robustness: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaQueryListEventInit : EventInit {
    var matches: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var media: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaStreamAudioSourceOptions {
    var mediaStream: MediaStream
}

external interface MediaStreamConstraints {
    var audio: dynamic /* Boolean? | MediaTrackConstraints? */
        get() = definedExternally
        set(value) = definedExternally
    var peerIdentity: String?
        get() = definedExternally
        set(value) = definedExternally
    var video: dynamic /* Boolean? | MediaTrackConstraints? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaStreamErrorEventInit : EventInit {
    var error: MediaStreamError?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaStreamEventInit : EventInit {
    var stream: MediaStream?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaStreamTrackAudioSourceOptions {
    var mediaStreamTrack: MediaStreamTrack
}

external interface MediaStreamTrackEventInit : EventInit {
    var track: MediaStreamTrack
}

external interface MediaTrackCapabilities {
    var aspectRatio: DoubleRange?
        get() = definedExternally
        set(value) = definedExternally
    var autoGainControl: Array<Boolean>?
        get() = definedExternally
        set(value) = definedExternally
    var channelCount: ULongRange?
        get() = definedExternally
        set(value) = definedExternally
    var deviceId: String?
        get() = definedExternally
        set(value) = definedExternally
    var echoCancellation: Array<Boolean>?
        get() = definedExternally
        set(value) = definedExternally
    var facingMode: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var frameRate: DoubleRange?
        get() = definedExternally
        set(value) = definedExternally
    var groupId: String?
        get() = definedExternally
        set(value) = definedExternally
    var height: ULongRange?
        get() = definedExternally
        set(value) = definedExternally
    var latency: DoubleRange?
        get() = definedExternally
        set(value) = definedExternally
    var noiseSuppression: Array<Boolean>?
        get() = definedExternally
        set(value) = definedExternally
    var resizeMode: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var sampleRate: ULongRange?
        get() = definedExternally
        set(value) = definedExternally
    var sampleSize: ULongRange?
        get() = definedExternally
        set(value) = definedExternally
    var width: ULongRange?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaTrackConstraintSet {
    var aspectRatio: dynamic /* Number? | ConstrainDoubleRange? */
        get() = definedExternally
        set(value) = definedExternally
    var autoGainControl: dynamic /* Boolean? | ConstrainBooleanParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var channelCount: dynamic /* Number? | ConstrainULongRange? */
        get() = definedExternally
        set(value) = definedExternally
    var deviceId: dynamic /* String? | Array<String>? | ConstrainDOMStringParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var echoCancellation: dynamic /* Boolean? | ConstrainBooleanParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var facingMode: dynamic /* String? | Array<String>? | ConstrainDOMStringParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var frameRate: dynamic /* Number? | ConstrainDoubleRange? */
        get() = definedExternally
        set(value) = definedExternally
    var groupId: dynamic /* String? | Array<String>? | ConstrainDOMStringParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var height: dynamic /* Number? | ConstrainULongRange? */
        get() = definedExternally
        set(value) = definedExternally
    var latency: dynamic /* Number? | ConstrainDoubleRange? */
        get() = definedExternally
        set(value) = definedExternally
    var noiseSuppression: dynamic /* Boolean? | ConstrainBooleanParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var resizeMode: dynamic /* String? | Array<String>? | ConstrainDOMStringParameters? */
        get() = definedExternally
        set(value) = definedExternally
    var sampleRate: dynamic /* Number? | ConstrainULongRange? */
        get() = definedExternally
        set(value) = definedExternally
    var sampleSize: dynamic /* Number? | ConstrainULongRange? */
        get() = definedExternally
        set(value) = definedExternally
    var width: dynamic /* Number? | ConstrainULongRange? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaTrackConstraints : MediaTrackConstraintSet {
    var advanced: Array<MediaTrackConstraintSet>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaTrackSettings {
    var aspectRatio: Number?
        get() = definedExternally
        set(value) = definedExternally
    var autoGainControl: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var channelCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var deviceId: String?
        get() = definedExternally
        set(value) = definedExternally
    var echoCancellation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var facingMode: String?
        get() = definedExternally
        set(value) = definedExternally
    var frameRate: Number?
        get() = definedExternally
        set(value) = definedExternally
    var groupId: String?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var latency: Number?
        get() = definedExternally
        set(value) = definedExternally
    var noiseSuppression: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var resizeMode: String?
        get() = definedExternally
        set(value) = definedExternally
    var sampleRate: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sampleSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var width: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MediaTrackSupportedConstraints {
    var aspectRatio: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var autoGainControl: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var channelCount: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var deviceId: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var echoCancellation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var facingMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var frameRate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var groupId: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var height: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var latency: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var noiseSuppression: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var resizeMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var sampleRate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var sampleSize: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var width: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MessageEventInit : EventInit {
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var lastEventId: String?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String?
        get() = definedExternally
        set(value) = definedExternally
    var ports: Array<MessagePort>?
        get() = definedExternally
        set(value) = definedExternally
    var source: dynamic /* WindowProxy? | MessagePort? | ServiceWorker? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface MidiPermissionDescriptor : PermissionDescriptor {
    override var name: String /* "midi" */
    var sysex: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MouseEventInit : EventModifierInit {
    var button: Number?
        get() = definedExternally
        set(value) = definedExternally
    var buttons: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clientX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clientY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var movementX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var movementY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var relatedTarget: EventTarget?
        get() = definedExternally
        set(value) = definedExternally
    var screenX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var screenY: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MultiCacheQueryOptions : CacheQueryOptions {
    var cacheName: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface NavigationPreloadState {
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var headerValue: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface NotificationAction {
    var action: String
    var icon: String?
        get() = definedExternally
        set(value) = definedExternally
    var title: String
}

external interface NotificationOptions {
    var actions: Array<NotificationAction>?
        get() = definedExternally
        set(value) = definedExternally
    var badge: String?
        get() = definedExternally
        set(value) = definedExternally
    var body: String?
        get() = definedExternally
        set(value) = definedExternally
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var dir: String? /* "auto" | "ltr" | "rtl" */
        get() = definedExternally
        set(value) = definedExternally
    var icon: String?
        get() = definedExternally
        set(value) = definedExternally
    var image: String?
        get() = definedExternally
        set(value) = definedExternally
    var lang: String?
        get() = definedExternally
        set(value) = definedExternally
    var renotify: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var requireInteraction: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var silent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var tag: String?
        get() = definedExternally
        set(value) = definedExternally
    var timestamp: Number?
        get() = definedExternally
        set(value) = definedExternally
    var vibrate: dynamic /* Number? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface OfflineAudioCompletionEventInit : EventInit {
    var renderedBuffer: AudioBuffer
}

external interface OptionalEffectTiming {
    var delay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var direction: String? /* "alternate" | "alternate-reverse" | "normal" | "reverse" */
        get() = definedExternally
        set(value) = definedExternally
    var duration: dynamic /* Number? | String? */
        get() = definedExternally
        set(value) = definedExternally
    var easing: String?
        get() = definedExternally
        set(value) = definedExternally
    var endDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var fill: String? /* "auto" | "backwards" | "both" | "forwards" | "none" */
        get() = definedExternally
        set(value) = definedExternally
    var iterationStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iterations: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface OscillatorOptions : AudioNodeOptions {
    var detune: Number?
        get() = definedExternally
        set(value) = definedExternally
    var frequency: Number?
        get() = definedExternally
        set(value) = definedExternally
    var periodicWave: PeriodicWave?
        get() = definedExternally
        set(value) = definedExternally
    var type: String? /* "custom" | "sawtooth" | "sine" | "square" | "triangle" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface PageTransitionEventInit : EventInit {
    var persisted: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PannerOptions : AudioNodeOptions {
    var coneInnerAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var coneOuterAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var coneOuterGain: Number?
        get() = definedExternally
        set(value) = definedExternally
    var distanceModel: String? /* "exponential" | "inverse" | "linear" */
        get() = definedExternally
        set(value) = definedExternally
    var maxDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var orientationX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var orientationY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var orientationZ: Number?
        get() = definedExternally
        set(value) = definedExternally
    var panningModel: String? /* "HRTF" | "equalpower" */
        get() = definedExternally
        set(value) = definedExternally
    var positionX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var positionY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var positionZ: Number?
        get() = definedExternally
        set(value) = definedExternally
    var refDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rolloffFactor: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PaymentCurrencyAmount {
    var currency: String
    var currencySystem: String?
        get() = definedExternally
        set(value) = definedExternally
    var value: String
}

external interface PaymentDetailsBase {
    var displayItems: Array<PaymentItem>?
        get() = definedExternally
        set(value) = definedExternally
    var modifiers: Array<PaymentDetailsModifier>?
        get() = definedExternally
        set(value) = definedExternally
    var shippingOptions: Array<PaymentShippingOption>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PaymentDetailsModifier {
    var additionalDisplayItems: Array<PaymentItem>?
        get() = definedExternally
        set(value) = definedExternally
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var supportedMethods: dynamic /* String | Array<String> */
        get() = definedExternally
        set(value) = definedExternally
    var total: PaymentItem?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PaymentDetailsUpdate : PaymentDetailsBase {
    var error: String?
        get() = definedExternally
        set(value) = definedExternally
    var total: PaymentItem?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PaymentItem {
    var amount: PaymentCurrencyAmount
    var label: String
    var pending: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PaymentRequestUpdateEventInit : EventInit

external interface PaymentShippingOption {
    var amount: PaymentCurrencyAmount
    var id: String
    var label: String
    var selected: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Pbkdf2Params : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var iterations: Number
    var salt: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface PeriodicWaveConstraints {
    var disableNormalization: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PeriodicWaveOptions : PeriodicWaveConstraints {
    var imag: dynamic /* Array<Number>? | Float32Array? */
        get() = definedExternally
        set(value) = definedExternally
    var real: dynamic /* Array<Number>? | Float32Array? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface PermissionDescriptor {
    var name: String /* "accelerometer" | "ambient-light-sensor" | "background-sync" | "bluetooth" | "camera" | "clipboard" | "device-info" | "geolocation" | "gyroscope" | "magnetometer" | "microphone" | "midi" | "notifications" | "persistent-storage" | "push" | "speaker" */
}

external interface PipeOptions {
    var preventAbort: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preventCancel: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preventClose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var signal: AbortSignal?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PointerEventInit : MouseEventInit {
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var isPrimary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var pointerId: Number?
        get() = definedExternally
        set(value) = definedExternally
    var pointerType: String?
        get() = definedExternally
        set(value) = definedExternally
    var pressure: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tangentialPressure: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tiltX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tiltY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var twist: Number?
        get() = definedExternally
        set(value) = definedExternally
    var width: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PopStateEventInit : EventInit {
    var state: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PositionOptions {
    var enableHighAccuracy: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maximumAge: Number?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PostMessageOptions {
    var transfer: Array<Any>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ProgressEventInit : EventInit {
    var lengthComputable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var loaded: Number?
        get() = definedExternally
        set(value) = definedExternally
    var total: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PromiseRejectionEventInit : EventInit {
    var promise: Promise<Any>
    var reason: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PropertyIndexedKeyframes {
    var composite: dynamic /* "accumulate" | "add" | "auto" | "replace" | Array<String /* "accumulate" | "add" | "auto" | "replace" */>? */
        get() = definedExternally
        set(value) = definedExternally
    var easing: dynamic /* String? | Array<String>? */
        get() = definedExternally
        set(value) = definedExternally
    var offset: dynamic /* Number? | Array<Number?>? */
        get() = definedExternally
        set(value) = definedExternally
    @nativeGetter
    operator fun get(property: String): dynamic /* String? | Array<String>? | Number? | Array<Number?>? */
    @nativeSetter
    operator fun set(property: String, value: String?)
    @nativeSetter
    operator fun set(property: String, value: Array<String>?)
    @nativeSetter
    operator fun set(property: String, value: Number?)
    @nativeSetter
    operator fun set(property: String, value: Array<Number?>?)
}

external interface PublicKeyCredentialCreationOptions {
    var attestation: String? /* "direct" | "indirect" | "none" */
        get() = definedExternally
        set(value) = definedExternally
    var authenticatorSelection: AuthenticatorSelectionCriteria?
        get() = definedExternally
        set(value) = definedExternally
    var challenge: dynamic /* ArrayBufferView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var excludeCredentials: Array<PublicKeyCredentialDescriptor>?
        get() = definedExternally
        set(value) = definedExternally
    var extensions: AuthenticationExtensionsClientInputs?
        get() = definedExternally
        set(value) = definedExternally
    var pubKeyCredParams: Array<PublicKeyCredentialParameters>
    var rp: PublicKeyCredentialRpEntity
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var user: PublicKeyCredentialUserEntity
}

external interface PublicKeyCredentialDescriptor {
    var id: dynamic /* ArrayBufferView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var transports: Array<String? /* "ble" | "internal" | "nfc" | "usb" */>?
        get() = definedExternally
        set(value) = definedExternally
    var type: String /* "public-key" */
}

external interface PublicKeyCredentialEntity {
    var icon: String?
        get() = definedExternally
        set(value) = definedExternally
    var name: String
}

external interface PublicKeyCredentialParameters {
    var alg: COSEAlgorithmIdentifier
    var type: String /* "public-key" */
}

external interface PublicKeyCredentialRequestOptions {
    var allowCredentials: Array<PublicKeyCredentialDescriptor>?
        get() = definedExternally
        set(value) = definedExternally
    var challenge: dynamic /* ArrayBufferView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var extensions: AuthenticationExtensionsClientInputs?
        get() = definedExternally
        set(value) = definedExternally
    var rpId: String?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var userVerification: String? /* "discouraged" | "preferred" | "required" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface PublicKeyCredentialRpEntity : PublicKeyCredentialEntity {
    var id: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PublicKeyCredentialUserEntity : PublicKeyCredentialEntity {
    var displayName: String
    var id: dynamic /* ArrayBufferView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface PushPermissionDescriptor : PermissionDescriptor {
    override var name: String /* "push" */
    var userVisibleOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PushSubscriptionJSON {
    var endpoint: String?
        get() = definedExternally
        set(value) = definedExternally
    var expirationTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var keys: Record<String, String>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PushSubscriptionOptionsInit {
    var applicationServerKey: dynamic /* ArrayBufferView? | ArrayBuffer? | String? */
        get() = definedExternally
        set(value) = definedExternally
    var userVisibleOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface QueuingStrategy<T> {
    var highWaterMark: Number?
        get() = definedExternally
        set(value) = definedExternally
    var size: QueuingStrategySizeCallback<T>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCDTMFToneChangeEventInit : EventInit {
    var tone: String
}

external interface RTCDataChannelEventInit : EventInit {
    var channel: RTCDataChannel
}

external interface RTCErrorEventInit : EventInit {
    var error: RTCError
}

external interface RTCErrorInit {
    var errorDetail: String /* "data-channel-failure" | "dtls-failure" | "fingerprint-failure" | "hardware-encoder-error" | "hardware-encoder-not-available" | "idp-bad-script-failure" | "idp-execution-failure" | "idp-load-failure" | "idp-need-login" | "idp-timeout" | "idp-tls-failure" | "idp-token-expired" | "idp-token-invalid" | "sctp-failure" | "sdp-syntax-error" */
    var httpRequestStatusCode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var receivedAlert: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sctpCauseCode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sdpLineNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sentAlert: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCIceCandidateComplete

external interface RTCIceCandidateDictionary {
    var foundation: String?
        get() = definedExternally
        set(value) = definedExternally
    var ip: String?
        get() = definedExternally
        set(value) = definedExternally
    var msMTurnSessionId: String?
        get() = definedExternally
        set(value) = definedExternally
    var port: Number?
        get() = definedExternally
        set(value) = definedExternally
    var priority: Number?
        get() = definedExternally
        set(value) = definedExternally
    var protocol: String? /* "tcp" | "udp" */
        get() = definedExternally
        set(value) = definedExternally
    var relatedAddress: String?
        get() = definedExternally
        set(value) = definedExternally
    var relatedPort: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tcpType: String? /* "active" | "passive" | "so" */
        get() = definedExternally
        set(value) = definedExternally
    var type: String? /* "host" | "prflx" | "relay" | "srflx" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCIceCandidateInit {
    var candidate: String?
        get() = definedExternally
        set(value) = definedExternally
    var sdpMLineIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sdpMid: String?
        get() = definedExternally
        set(value) = definedExternally
    var usernameFragment: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCIceCandidatePair {
    var local: RTCIceCandidate?
        get() = definedExternally
        set(value) = definedExternally
    var remote: RTCIceCandidate?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCIceParameters {
    var password: String?
        get() = definedExternally
        set(value) = definedExternally
    var usernameFragment: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCPeerConnectionIceErrorEventInit : EventInit {
    var errorCode: Number
    var hostCandidate: String?
        get() = definedExternally
        set(value) = definedExternally
    var statusText: String?
        get() = definedExternally
        set(value) = definedExternally
    var url: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCPeerConnectionIceEventInit : EventInit {
    var candidate: RTCIceCandidate?
        get() = definedExternally
        set(value) = definedExternally
    var url: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtcpParameters {
    var cname: String?
        get() = definedExternally
        set(value) = definedExternally
    var reducedSize: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtpCapabilities {
    var codecs: Array<RTCRtpCodecCapability>
    var headerExtensions: Array<RTCRtpHeaderExtensionCapability>
}

external interface RTCRtpCodecCapability {
    var channels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clockRate: Number
    var mimeType: String
    var sdpFmtpLine: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtpCodecParameters {
    var channels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clockRate: Number
    var mimeType: String
    var payloadType: Number
    var sdpFmtpLine: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtpCodingParameters {
    var rid: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtpContributingSource {
    var audioLevel: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rtpTimestamp: Number
    var source: Number
    var timestamp: Number
}

external interface RTCRtpDecodingParameters : RTCRtpCodingParameters

external interface RTCRtpEncodingParameters : RTCRtpCodingParameters {
    var active: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var codecPayloadType: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dtx: String? /* "disabled" | "enabled" */
        get() = definedExternally
        set(value) = definedExternally
    var maxBitrate: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxFramerate: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ptime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scaleResolutionDownBy: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtpHeaderExtensionCapability {
    var uri: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCRtpHeaderExtensionParameters {
    var encrypted: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var id: Number
    var uri: String
}

external interface RTCRtpParameters {
    var codecs: Array<RTCRtpCodecParameters>
    var headerExtensions: Array<RTCRtpHeaderExtensionParameters>
    var rtcp: RTCRtcpParameters
}

external interface RTCRtpReceiveParameters : RTCRtpParameters {
    var encodings: Array<RTCRtpDecodingParameters>
}

external interface RTCRtpSendParameters : RTCRtpParameters {
    var degradationPreference: String? /* "balanced" | "maintain-framerate" | "maintain-resolution" */
        get() = definedExternally
        set(value) = definedExternally
    var encodings: Array<RTCRtpEncodingParameters>
    var priority: String? /* "high" | "low" | "medium" | "very-low" */
        get() = definedExternally
        set(value) = definedExternally
    var transactionId: String
}

external interface RTCRtpSynchronizationSource : RTCRtpContributingSource {
    var voiceActivityFlag: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RTCStatsEventInit : EventInit {
    var report: RTCStatsReport
}

external interface RTCStatsReport : ReadonlyMap<String, Any> {
    fun forEach(callbackfn: (value: Any, key: String, parent: RTCStatsReport) -> Unit, thisArg: Any = definedExternally)
}

external interface RTCTrackEventInit : EventInit {
    var receiver: RTCRtpReceiver
    var streams: Array<MediaStream>?
        get() = definedExternally
        set(value) = definedExternally
    var track: MediaStreamTrack
    var transceiver: RTCRtpTransceiver
}

external interface ReadableStreamReadDoneResult<T> {
    var done: Boolean
    var value: T?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ReadableStreamReadValueResult<T> {
    var done: Boolean
    var value: T
}

external interface RegistrationOptions {
    var scope: String?
        get() = definedExternally
        set(value) = definedExternally
    var type: String? /* "classic" | "module" */
        get() = definedExternally
        set(value) = definedExternally
    var updateViaCache: String? /* "all" | "imports" | "none" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RequestInit {
    var body: dynamic /* Blob? | ArrayBufferView? | ArrayBuffer? | FormData? | URLSearchParams? | ReadableStream<Uint8Array>? | String? */
        get() = definedExternally
        set(value) = definedExternally
    var cache: String? /* "default" | "force-cache" | "no-cache" | "no-store" | "only-if-cached" | "reload" */
        get() = definedExternally
        set(value) = definedExternally
    var credentials: String? /* "include" | "omit" | "same-origin" */
        get() = definedExternally
        set(value) = definedExternally
    var headers: dynamic /* Headers? | Array<Array<String>>? | Record<String, String>? */
        get() = definedExternally
        set(value) = definedExternally
    var integrity: String?
        get() = definedExternally
        set(value) = definedExternally
    var keepalive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var method: String?
        get() = definedExternally
        set(value) = definedExternally
    var mode: String? /* "cors" | "navigate" | "no-cors" | "same-origin" */
        get() = definedExternally
        set(value) = definedExternally
    var redirect: String? /* "error" | "follow" | "manual" */
        get() = definedExternally
        set(value) = definedExternally
    var referrer: String?
        get() = definedExternally
        set(value) = definedExternally
    var referrerPolicy: String? /* "" | "no-referrer" | "no-referrer-when-downgrade" | "origin" | "origin-when-cross-origin" | "same-origin" | "strict-origin" | "strict-origin-when-cross-origin" | "unsafe-url" */
        get() = definedExternally
        set(value) = definedExternally
    var signal: AbortSignal?
        get() = definedExternally
        set(value) = definedExternally
    var window: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ResponseInit {
    var headers: dynamic /* Headers? | Array<Array<String>>? | Record<String, String>? */
        get() = definedExternally
        set(value) = definedExternally
    var status: Number?
        get() = definedExternally
        set(value) = definedExternally
    var statusText: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaHashedImportParams : Algorithm {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaHashedKeyGenParams : RsaKeyGenParams {
    var hash: dynamic /* typealias HashAlgorithmIdentifier = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaKeyGenParams : Algorithm {
    var modulusLength: Number
    var publicExponent: BigInteger
}

external interface RsaOaepParams : Algorithm {
    var label: dynamic /* Int8Array? | Int16Array? | Int32Array? | Uint8Array? | Uint16Array? | Uint32Array? | Uint8ClampedArray? | Float32Array? | Float64Array? | DataView? | ArrayBuffer? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaOtherPrimesInfo {
    var d: String?
        get() = definedExternally
        set(value) = definedExternally
    var r: String?
        get() = definedExternally
        set(value) = definedExternally
    var t: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RsaPssParams : Algorithm {
    var saltLength: Number
}

external interface SVGBoundingBoxOptions {
    var clipped: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fill: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var markers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stroke: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScrollIntoViewOptions : ScrollOptions {
    var block: String? /* "center" | "end" | "nearest" | "start" */
        get() = definedExternally
        set(value) = definedExternally
    var inline: String? /* "center" | "end" | "nearest" | "start" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScrollOptions {
    var behavior: String? /* "auto" | "smooth" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScrollToOptions : ScrollOptions {
    var left: Number?
        get() = definedExternally
        set(value) = definedExternally
    var top: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SecurityPolicyViolationEventInit : EventInit {
    var blockedURI: String?
        get() = definedExternally
        set(value) = definedExternally
    var columnNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
    var documentURI: String?
        get() = definedExternally
        set(value) = definedExternally
    var effectiveDirective: String?
        get() = definedExternally
        set(value) = definedExternally
    var lineNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
    var originalPolicy: String?
        get() = definedExternally
        set(value) = definedExternally
    var referrer: String?
        get() = definedExternally
        set(value) = definedExternally
    var sourceFile: String?
        get() = definedExternally
        set(value) = definedExternally
    var statusCode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var violatedDirective: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ServiceWorkerMessageEventInit : EventInit {
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var lastEventId: String?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String?
        get() = definedExternally
        set(value) = definedExternally
    var ports: Array<MessagePort>?
        get() = definedExternally
        set(value) = definedExternally
    var source: dynamic /* ServiceWorker? | MessagePort? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ShadowRootInit {
    var delegatesFocus: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var mode: String /* "closed" | "open" */
}

external interface ShareData {
    var text: String?
        get() = definedExternally
        set(value) = definedExternally
    var title: String?
        get() = definedExternally
        set(value) = definedExternally
    var url: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SpeechSynthesisErrorEventInit : SpeechSynthesisEventInit {
    var error: String /* "audio-busy" | "audio-hardware" | "canceled" | "interrupted" | "invalid-argument" | "language-unavailable" | "network" | "not-allowed" | "synthesis-failed" | "synthesis-unavailable" | "text-too-long" | "voice-unavailable" */
}

external interface SpeechSynthesisEventInit : EventInit {
    var charIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
    var charLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var elapsedTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var utterance: SpeechSynthesisUtterance
}

external interface StereoPannerOptions : AudioNodeOptions {
    var pan: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StorageEstimate {
    var quota: Number?
        get() = definedExternally
        set(value) = definedExternally
    var usage: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StorageEventInit : EventInit {
    var key: String?
        get() = definedExternally
        set(value) = definedExternally
    var newValue: String?
        get() = definedExternally
        set(value) = definedExternally
    var oldValue: String?
        get() = definedExternally
        set(value) = definedExternally
    var storageArea: Storage?
        get() = definedExternally
        set(value) = definedExternally
    var url: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StoreExceptionsInformation : ExceptionInformation {
    var detailURI: String?
        get() = definedExternally
        set(value) = definedExternally
    var explanationString: String?
        get() = definedExternally
        set(value) = definedExternally
    var siteName: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StoreSiteSpecificExceptionsInformation : StoreExceptionsInformation {
    var arrayOfDomainStrings: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TouchEventInit : EventModifierInit {
    var changedTouches: Array<Touch>?
        get() = definedExternally
        set(value) = definedExternally
    var targetTouches: Array<Touch>?
        get() = definedExternally
        set(value) = definedExternally
    var touches: Array<Touch>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TouchInit {
    var altitudeAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var azimuthAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clientX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clientY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var force: Number?
        get() = definedExternally
        set(value) = definedExternally
    var identifier: Number
    var pageX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var pageY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radiusX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radiusY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotationAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var screenX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var screenY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var target: EventTarget
    var touchType: String? /* "direct" | "stylus" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface TrackEventInit : EventInit {
    var track: TextTrack?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TransitionEventInit : EventInit {
    var elapsedTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var propertyName: String?
        get() = definedExternally
        set(value) = definedExternally
    var pseudoElement: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UIEventInit : EventInit {
    var detail: Number?
        get() = definedExternally
        set(value) = definedExternally
    var view: Window?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ULongRange {
    var max: Number?
        get() = definedExternally
        set(value) = definedExternally
    var min: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UnderlyingByteSource {
    var autoAllocateChunkSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var cancel: ReadableStreamErrorCallback?
        get() = definedExternally
        set(value) = definedExternally
    var pull: ReadableByteStreamControllerCallback?
        get() = definedExternally
        set(value) = definedExternally
    var start: ReadableByteStreamControllerCallback?
        get() = definedExternally
        set(value) = definedExternally
    var type: String /* "bytes" */
}

external interface UnderlyingSink<W> {
    var abort: WritableStreamErrorCallback?
        get() = definedExternally
        set(value) = definedExternally
    var close: WritableStreamDefaultControllerCloseCallback?
        get() = definedExternally
        set(value) = definedExternally
    var start: WritableStreamDefaultControllerStartCallback?
        get() = definedExternally
        set(value) = definedExternally
    var type: Any?
        get() = definedExternally
        set(value) = definedExternally
    var write: WritableStreamDefaultControllerWriteCallback<W>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UnderlyingSource<R> {
    var cancel: ReadableStreamErrorCallback?
        get() = definedExternally
        set(value) = definedExternally
    var pull: ReadableStreamDefaultControllerCallback<R>?
        get() = definedExternally
        set(value) = definedExternally
    var start: ReadableStreamDefaultControllerCallback<R>?
        get() = definedExternally
        set(value) = definedExternally
    var type: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface VRDisplayEventInit : EventInit {
    var display: VRDisplay
    var reason: String? /* "mounted" | "navigation" | "requested" | "unmounted" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface VRLayer {
    var leftBounds: dynamic /* Array<Number>? | Float32Array? */
        get() = definedExternally
        set(value) = definedExternally
    var rightBounds: dynamic /* Array<Number>? | Float32Array? */
        get() = definedExternally
        set(value) = definedExternally
    var source: HTMLCanvasElement?
        get() = definedExternally
        set(value) = definedExternally
}

external interface VRStageParameters {
    var sittingToStandingTransform: Float32Array?
        get() = definedExternally
        set(value) = definedExternally
    var sizeX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sizeY: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WaveShaperOptions : AudioNodeOptions {
    var curve: dynamic /* Array<Number>? | Float32Array? */
        get() = definedExternally
        set(value) = definedExternally
    var oversample: String? /* "2x" | "4x" | "none" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface WebGLContextAttributes {
    var xrCompatible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var antialias: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var desynchronized: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var failIfMajorPerformanceCaveat: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var powerPreference: String? /* "default" | "high-performance" | "low-power" */
        get() = definedExternally
        set(value) = definedExternally
    var premultipliedAlpha: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preserveDrawingBuffer: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stencil: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WebGLContextEventInit : EventInit {
    var statusMessage: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WheelEventInit : MouseEventInit {
    var deltaMode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var deltaX: Number?
        get() = definedExternally
        set(value) = definedExternally
    var deltaY: Number?
        get() = definedExternally
        set(value) = definedExternally
    var deltaZ: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WorkerOptions {
    var credentials: String? /* "include" | "omit" | "same-origin" */
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var type: String? /* "classic" | "module" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface WorkletOptions {
    var credentials: String? /* "include" | "omit" | "same-origin" */
        get() = definedExternally
        set(value) = definedExternally
}

external interface txAuthGenericArg {
    var content: ArrayBuffer
    var contentType: String
}

external interface EventListener {
    @nativeInvoke
    operator fun invoke(evt: Event)
}

external interface `T$15` {
    fun lookupNamespaceURI(prefix: String?): String?
}

external interface ANGLE_instanced_arrays {
    fun drawArraysInstancedANGLE(mode: GLenum, first: GLint, count: GLsizei, primcount: GLsizei)
    fun drawElementsInstancedANGLE(mode: GLenum, count: GLsizei, type: GLenum, offset: GLintptr, primcount: GLsizei)
    fun vertexAttribDivisorANGLE(index: GLuint, divisor: GLuint)
    val VERTEX_ATTRIB_ARRAY_DIVISOR_ANGLE: GLenum
}

external interface AbortSignalEventMap {
    var abort: Event
}

external interface AbortSignal : EventTarget {
    val aborted: Boolean
    var onabort: ((self: AbortSignal, ev: Event) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: AbortSignal, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AbortSignal, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: AbortSignal, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: AbortSignal, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: AbortSignal, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: AbortSignal, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface AbstractRange {
    val collapsed: Boolean
    val endContainer: Node
    val endOffset: Number
    val startContainer: Node
    val startOffset: Number
}

external interface AbstractWorkerEventMap {
    var error: ErrorEvent
}

external interface AbstractWorker {
    var onerror: ((self: AbstractWorker, ev: ErrorEvent) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: AbstractWorker, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AbstractWorker, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: AbstractWorker, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListener)
    fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: AbstractWorker, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: AbstractWorker, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: AbstractWorker, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListener)
    fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
}

external interface AesCfbParams : Algorithm {
    var iv: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AesCmacParams : Algorithm {
    var length: Number
}

external interface AnalyserNode : AudioNode {
    var fftSize: Number
    val frequencyBinCount: Number
    var maxDecibels: Number
    var minDecibels: Number
    var smoothingTimeConstant: Number
    fun getByteFrequencyData(array: Uint8Array)
    fun getByteTimeDomainData(array: Uint8Array)
    fun getFloatFrequencyData(array: Float32Array)
    fun getFloatTimeDomainData(array: Float32Array)
}

external interface Animatable {
    fun animate(keyframes: Array<Keyframe>?, options: Number = definedExternally): Animation
    fun animate(keyframes: Array<Keyframe>?): Animation
    fun animate(keyframes: Array<Keyframe>?, options: KeyframeAnimationOptions = definedExternally): Animation
    fun animate(keyframes: PropertyIndexedKeyframes?, options: Number = definedExternally): Animation
    fun animate(keyframes: PropertyIndexedKeyframes?): Animation
    fun animate(keyframes: PropertyIndexedKeyframes?, options: KeyframeAnimationOptions = definedExternally): Animation
    fun getAnimations(): Array<Animation>
}

external interface AnimationEventMap {
    var cancel: AnimationPlaybackEvent
    var finish: AnimationPlaybackEvent
}

external interface Animation : EventTarget {
    var currentTime: Number?
    var effect: AnimationEffect?
    val finished: Promise<Animation>
    var id: String
    var oncancel: ((self: Animation, ev: AnimationPlaybackEvent) -> Any)?
    var onfinish: ((self: Animation, ev: AnimationPlaybackEvent) -> Any)?
    val pending: Boolean
    val playState: String /* "finished" | "idle" | "paused" | "running" */
    var playbackRate: Number
    val ready: Promise<Animation>
    var startTime: Number?
    var timeline: AnimationTimeline?
    fun cancel()
    fun finish()
    fun pause()
    fun play()
    fun reverse()
    fun updatePlaybackRate(playbackRate: Number)
    fun <K : String> addEventListener(type: K, listener: (self: Animation, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Animation, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Animation, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Animation, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Animation, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Animation, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface AnimationEffect {
    fun getComputedTiming(): ComputedEffectTiming
    fun getTiming(): EffectTiming
    fun updateTiming(timing: OptionalEffectTiming = definedExternally)
}

external interface AnimationEvent : Event {
    val animationName: String
    val elapsedTime: Number
    val pseudoElement: String
}

external interface AnimationFrameProvider {
    fun cancelAnimationFrame(handle: Number)
    fun requestAnimationFrame(callback: FrameRequestCallback): Number
}

external interface AnimationPlaybackEvent : Event {
    val currentTime: Number?
    val timelineTime: Number?
}

external interface AnimationTimeline {
    val currentTime: Number?
}

external interface ApplicationCacheEventMap {
    var cached: Event
    var checking: Event
    var downloading: Event
    var error: Event
    var noupdate: Event
    var obsolete: Event
    var progress: ProgressEvent<ApplicationCache>
    var updateready: Event
}

external interface ApplicationCache : EventTarget {
    var oncached: ((self: ApplicationCache, ev: Event) -> Any)?
    var onchecking: ((self: ApplicationCache, ev: Event) -> Any)?
    var ondownloading: ((self: ApplicationCache, ev: Event) -> Any)?
    var onerror: ((self: ApplicationCache, ev: Event) -> Any)?
    var onnoupdate: ((self: ApplicationCache, ev: Event) -> Any)?
    var onobsolete: ((self: ApplicationCache, ev: Event) -> Any)?
    var onprogress: ((self: ApplicationCache, ev: ProgressEvent<ApplicationCache>) -> Any)?
    var onupdateready: ((self: ApplicationCache, ev: Event) -> Any)?
    val status: Number
    fun abort()
    fun swapCache()
    fun update()
    val CHECKING: Number
    val DOWNLOADING: Number
    val IDLE: Number
    val OBSOLETE: Number
    val UNCACHED: Number
    val UPDATEREADY: Number
    fun <K : String> addEventListener(type: K, listener: (self: ApplicationCache, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ApplicationCache, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ApplicationCache, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ApplicationCache, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ApplicationCache, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ApplicationCache, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface Attr : Node {
    val localName: String
    val name: String
    override val namespaceURI: String?
    val ownerElement: Element?
    val prefix: String?
    val specified: Boolean
    var value: String
}

external interface AudioBuffer {
    val duration: Number
    val length: Number
    val numberOfChannels: Number
    val sampleRate: Number
    fun copyFromChannel(destination: Float32Array, channelNumber: Number, bufferOffset: Number = definedExternally)
    fun copyToChannel(source: Float32Array, channelNumber: Number, bufferOffset: Number = definedExternally)
    fun getChannelData(channel: Number): Float32Array
}

external interface AudioBufferSourceNode : AudioScheduledSourceNode {
    var buffer: AudioBuffer?
    val detune: AudioParam
    var loop: Boolean
    var loopEnd: Number
    var loopStart: Number
    val playbackRate: AudioParam
    fun start(param_when: Number = definedExternally, offset: Number = definedExternally, duration: Number = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AudioBufferSourceNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AudioBufferSourceNode, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: AudioBufferSourceNode, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioBufferSourceNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioBufferSourceNode, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioBufferSourceNode, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface AudioContext : BaseAudioContext {
    val baseLatency: Number
    val outputLatency: Number
    fun close(): Promise<Unit>
    fun createMediaElementSource(mediaElement: HTMLMediaElement): MediaElementAudioSourceNode
    fun createMediaStreamDestination(): MediaStreamAudioDestinationNode
    fun createMediaStreamSource(mediaStream: MediaStream): MediaStreamAudioSourceNode
    fun createMediaStreamTrackSource(mediaStreamTrack: MediaStreamTrack): MediaStreamTrackAudioSourceNode
    fun getOutputTimestamp(): AudioTimestamp
    fun resume(): Promise<Unit>
    fun suspend(): Promise<Unit>
    fun <K : String> addEventListener(type: K, listener: (self: AudioContext, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AudioContext, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: AudioContext, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioContext, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioContext, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioContext, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)

    companion object {
        fun getContext(): AudioContext
        fun setContext(context: AudioContext)
    }
}

external interface AudioDestinationNode : AudioNode {
    val maxChannelCount: Number
}

external interface AudioListener {
    val forwardX: AudioParam
    val forwardY: AudioParam
    val forwardZ: AudioParam
    val positionX: AudioParam
    val positionY: AudioParam
    val positionZ: AudioParam
    val upX: AudioParam
    val upY: AudioParam
    val upZ: AudioParam
    fun setOrientation(x: Number, y: Number, z: Number, xUp: Number, yUp: Number, zUp: Number)
    fun setPosition(x: Number, y: Number, z: Number)
}

external interface AudioNode : EventTarget {
    var channelCount: Number
    var channelCountMode: String /* "clamped-max" | "explicit" | "max" */
    var channelInterpretation: String /* "discrete" | "speakers" */
    val context: BaseAudioContext
    val numberOfInputs: Number
    val numberOfOutputs: Number
    fun connect(destinationNode: AudioNode, output: Number = definedExternally, input: Number = definedExternally): AudioNode
    fun connect(destinationNode: AudioNode): AudioNode
    fun connect(destinationNode: AudioNode, output: Number = definedExternally): AudioNode
    fun connect(destinationParam: AudioParam, output: Number = definedExternally)
    fun connect(destinationParam: AudioParam)
    fun disconnect()
    fun disconnect(output: Number)
    fun disconnect(destinationNode: AudioNode)
    fun disconnect(destinationNode: AudioNode, output: Number)
    fun disconnect(destinationNode: AudioNode, output: Number, input: Number)
    fun disconnect(destinationParam: AudioParam)
    fun disconnect(destinationParam: AudioParam, output: Number)
}

external interface AudioParam {
    fun setValueCurveAtTime(values: Iterable<Number>, startTime: Number, duration: Number): AudioParam
    var automationRate: String /* "a-rate" | "k-rate" */
    val defaultValue: Number
    val maxValue: Number
    val minValue: Number
    var value: Number
    fun cancelAndHoldAtTime(cancelTime: Number): AudioParam
    fun cancelScheduledValues(cancelTime: Number): AudioParam
    fun exponentialRampToValueAtTime(value: Number, endTime: Number): AudioParam
    fun linearRampToValueAtTime(value: Number, endTime: Number): AudioParam
    fun setTargetAtTime(target: Number, startTime: Number, timeConstant: Number): AudioParam
    fun setValueAtTime(value: Number, startTime: Number): AudioParam
    fun setValueCurveAtTime(values: Array<Number>, startTime: Number, duration: Number): AudioParam
    fun setValueCurveAtTime(values: Float32Array, startTime: Number, duration: Number): AudioParam
}

external interface AudioProcessingEvent : Event {
    val inputBuffer: AudioBuffer
    val outputBuffer: AudioBuffer
    val playbackTime: Number
}

external interface AudioScheduledSourceNodeEventMap {
    var ended: Event
}

external interface AudioScheduledSourceNode : AudioNode {
    var onended: ((self: AudioScheduledSourceNode, ev: Event) -> Any)?
    fun start(param_when: Number = definedExternally)
    fun stop(param_when: Number = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AudioScheduledSourceNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: AudioScheduledSourceNode, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: AudioScheduledSourceNode, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioScheduledSourceNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioScheduledSourceNode, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: AudioScheduledSourceNode, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface AudioWorklet : Worklet

external interface BarProp {
    val visible: Boolean
}

external interface BaseAudioContextEventMap {
    var statechange: Event
}

external interface BaseAudioContext : EventTarget {
    fun createIIRFilter(feedforward: Iterable<Number>, feedback: Iterable<Number>): IIRFilterNode
    fun createPeriodicWave(real: Iterable<Number>, imag: Iterable<Number>, constraints: PeriodicWaveConstraints = definedExternally): PeriodicWave
    fun createPeriodicWave(real: Iterable<Number>, imag: Iterable<Number>): PeriodicWave
    val audioWorklet: AudioWorklet
    val currentTime: Number
    val destination: AudioDestinationNode
    val listener: AudioListener
    var onstatechange: ((self: BaseAudioContext, ev: Event) -> Any)?
    val sampleRate: Number
    val state: String /* "closed" | "running" | "suspended" */
    fun createAnalyser(): AnalyserNode
    fun createBiquadFilter(): BiquadFilterNode
    fun createBuffer(numberOfChannels: Number, length: Number, sampleRate: Number): AudioBuffer
    fun createBufferSource(): AudioBufferSourceNode
    fun createChannelMerger(numberOfInputs: Number = definedExternally): ChannelMergerNode
    fun createChannelSplitter(numberOfOutputs: Number = definedExternally): ChannelSplitterNode
    fun createConstantSource(): ConstantSourceNode
    fun createConvolver(): ConvolverNode
    fun createDelay(maxDelayTime: Number = definedExternally): DelayNode
    fun createDynamicsCompressor(): DynamicsCompressorNode
    fun createGain(): GainNode
    fun createIIRFilter(feedforward: Array<Number>, feedback: Array<Number>): IIRFilterNode
    fun createOscillator(): OscillatorNode
    fun createPanner(): PannerNode
    fun createPeriodicWave(real: Array<Number>, imag: Array<Number>, constraints: PeriodicWaveConstraints = definedExternally): PeriodicWave
    fun createPeriodicWave(real: Array<Number>, imag: Array<Number>): PeriodicWave
    fun createPeriodicWave(real: Array<Number>, imag: Float32Array, constraints: PeriodicWaveConstraints = definedExternally): PeriodicWave
    fun createPeriodicWave(real: Array<Number>, imag: Float32Array): PeriodicWave
    fun createPeriodicWave(real: Float32Array, imag: Array<Number>, constraints: PeriodicWaveConstraints = definedExternally): PeriodicWave
    fun createPeriodicWave(real: Float32Array, imag: Array<Number>): PeriodicWave
    fun createPeriodicWave(real: Float32Array, imag: Float32Array, constraints: PeriodicWaveConstraints = definedExternally): PeriodicWave
    fun createPeriodicWave(real: Float32Array, imag: Float32Array): PeriodicWave
    fun createScriptProcessor(bufferSize: Number = definedExternally, numberOfInputChannels: Number = definedExternally, numberOfOutputChannels: Number = definedExternally): ScriptProcessorNode
    fun createStereoPanner(): StereoPannerNode
    fun createWaveShaper(): WaveShaperNode
    fun decodeAudioData(audioData: ArrayBuffer, successCallback: DecodeSuccessCallback? = definedExternally, errorCallback: DecodeErrorCallback? = definedExternally): Promise<AudioBuffer>
    fun <K : String> addEventListener(type: K, listener: (self: BaseAudioContext, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: BaseAudioContext, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: BaseAudioContext, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: BaseAudioContext, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: BaseAudioContext, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: BaseAudioContext, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface BeforeUnloadEvent : Event

external interface BiquadFilterNode : AudioNode {
    val Q: AudioParam
    val detune: AudioParam
    val frequency: AudioParam
    val gain: AudioParam
    var type: String /* "allpass" | "bandpass" | "highpass" | "highshelf" | "lowpass" | "lowshelf" | "notch" | "peaking" */
    fun getFrequencyResponse(frequencyHz: Float32Array, magResponse: Float32Array, phaseResponse: Float32Array)
}

external interface Blob {
    val size: Number
    val type: String
    fun arrayBuffer(): Promise<ArrayBuffer>
    fun slice(start: Number = definedExternally, end: Number = definedExternally, contentType: String = definedExternally): Blob
    fun stream(): ReadableStream__0
    fun text(): Promise<String>
}

external interface Body {
    val body: ReadableStream<Uint8Array>?
    val bodyUsed: Boolean
    fun arrayBuffer(): Promise<ArrayBuffer>
    fun blob(): Promise<Blob>
    fun formData(): Promise<FormData>
    fun json(): Promise<Any>
    fun text(): Promise<String>
}

external interface CDATASection : Text

external interface CSSRule {
    var cssText: String
    val parentRule: CSSRule?
    val parentStyleSheet: CSSStyleSheet?
    val type: Number
    val CHARSET_RULE: Number
    val FONT_FACE_RULE: Number
    val IMPORT_RULE: Number
    val KEYFRAMES_RULE: Number
    val KEYFRAME_RULE: Number
    val MEDIA_RULE: Number
    val NAMESPACE_RULE: Number
    val PAGE_RULE: Number
    val STYLE_RULE: Number
    val SUPPORTS_RULE: Number
}

external interface CSSRuleList {
    val length: Number
    fun item(index: Number): CSSRule?
    @nativeGetter
    operator fun get(index: Number): CSSRule?
    @nativeSetter
    operator fun set(index: Number, value: CSSRule)
}

external interface CSSStyleDeclaration {
    var alignContent: String
    var alignItems: String
    var alignSelf: String
    var alignmentBaseline: String
    var all: String
    var animation: String
    var animationDelay: String
    var animationDirection: String
    var animationDuration: String
    var animationFillMode: String
    var animationIterationCount: String
    var animationName: String
    var animationPlayState: String
    var animationTimingFunction: String
    var backfaceVisibility: String
    var background: String
    var backgroundAttachment: String
    var backgroundClip: String
    var backgroundColor: String
    var backgroundImage: String
    var backgroundOrigin: String
    var backgroundPosition: String
    var backgroundPositionX: String
    var backgroundPositionY: String
    var backgroundRepeat: String
    var backgroundSize: String
    var baselineShift: String
    var blockSize: String
    var border: String
    var borderBlockEnd: String
    var borderBlockEndColor: String
    var borderBlockEndStyle: String
    var borderBlockEndWidth: String
    var borderBlockStart: String
    var borderBlockStartColor: String
    var borderBlockStartStyle: String
    var borderBlockStartWidth: String
    var borderBottom: String
    var borderBottomColor: String
    var borderBottomLeftRadius: String
    var borderBottomRightRadius: String
    var borderBottomStyle: String
    var borderBottomWidth: String
    var borderCollapse: String
    var borderColor: String
    var borderImage: String
    var borderImageOutset: String
    var borderImageRepeat: String
    var borderImageSlice: String
    var borderImageSource: String
    var borderImageWidth: String
    var borderInlineEnd: String
    var borderInlineEndColor: String
    var borderInlineEndStyle: String
    var borderInlineEndWidth: String
    var borderInlineStart: String
    var borderInlineStartColor: String
    var borderInlineStartStyle: String
    var borderInlineStartWidth: String
    var borderLeft: String
    var borderLeftColor: String
    var borderLeftStyle: String
    var borderLeftWidth: String
    var borderRadius: String
    var borderRight: String
    var borderRightColor: String
    var borderRightStyle: String
    var borderRightWidth: String
    var borderSpacing: String
    var borderStyle: String
    var borderTop: String
    var borderTopColor: String
    var borderTopLeftRadius: String
    var borderTopRightRadius: String
    var borderTopStyle: String
    var borderTopWidth: String
    var borderWidth: String
    var bottom: String
    var boxShadow: String
    var boxSizing: String
    var breakAfter: String
    var breakBefore: String
    var breakInside: String
    var captionSide: String
    var caretColor: String
    var clear: String
    var clip: String
    var clipPath: String
    var clipRule: String
    var color: String
    var colorInterpolation: String
    var colorInterpolationFilters: String
    var columnCount: String
    var columnFill: String
    var columnGap: String
    var columnRule: String
    var columnRuleColor: String
    var columnRuleStyle: String
    var columnRuleWidth: String
    var columnSpan: String
    var columnWidth: String
    var columns: String
    var content: String
    var counterIncrement: String
    var counterReset: String
    var cssFloat: String
    var cssText: String
    var cursor: String
    var direction: String
    var display: String
    var dominantBaseline: String
    var emptyCells: String
    var fill: String
    var fillOpacity: String
    var fillRule: String
    var filter: String
    var flex: String
    var flexBasis: String
    var flexDirection: String
    var flexFlow: String
    var flexGrow: String
    var flexShrink: String
    var flexWrap: String
    var float: String
    var floodColor: String
    var floodOpacity: String
    var font: String
    var fontFamily: String
    var fontFeatureSettings: String
    var fontKerning: String
    var fontSize: String
    var fontSizeAdjust: String
    var fontStretch: String
    var fontStyle: String
    var fontSynthesis: String
    var fontVariant: String
    var fontVariantCaps: String
    var fontVariantEastAsian: String
    var fontVariantLigatures: String
    var fontVariantNumeric: String
    var fontVariantPosition: String
    var fontWeight: String
    var gap: String
    var glyphOrientationVertical: String
    var grid: String
    var gridArea: String
    var gridAutoColumns: String
    var gridAutoFlow: String
    var gridAutoRows: String
    var gridColumn: String
    var gridColumnEnd: String
    var gridColumnGap: String
    var gridColumnStart: String
    var gridGap: String
    var gridRow: String
    var gridRowEnd: String
    var gridRowGap: String
    var gridRowStart: String
    var gridTemplate: String
    var gridTemplateAreas: String
    var gridTemplateColumns: String
    var gridTemplateRows: String
    var height: String
    var hyphens: String
    var imageOrientation: String
    var imageRendering: String
    var inlineSize: String
    var justifyContent: String
    var justifyItems: String
    var justifySelf: String
    var left: String
    val length: Number
    var letterSpacing: String
    var lightingColor: String
    var lineBreak: String
    var lineHeight: String
    var listStyle: String
    var listStyleImage: String
    var listStylePosition: String
    var listStyleType: String
    var margin: String
    var marginBlockEnd: String
    var marginBlockStart: String
    var marginBottom: String
    var marginInlineEnd: String
    var marginInlineStart: String
    var marginLeft: String
    var marginRight: String
    var marginTop: String
    var marker: String
    var markerEnd: String
    var markerMid: String
    var markerStart: String
    var mask: String
    var maskComposite: String
    var maskImage: String
    var maskPosition: String
    var maskRepeat: String
    var maskSize: String
    var maskType: String
    var maxBlockSize: String
    var maxHeight: String
    var maxInlineSize: String
    var maxWidth: String
    var minBlockSize: String
    var minHeight: String
    var minInlineSize: String
    var minWidth: String
    var objectFit: String
    var objectPosition: String
    var opacity: String
    var order: String
    var orphans: String
    var outline: String
    var outlineColor: String
    var outlineOffset: String
    var outlineStyle: String
    var outlineWidth: String
    var overflow: String
    var overflowAnchor: String
    var overflowWrap: String
    var overflowX: String
    var overflowY: String
    var padding: String
    var paddingBlockEnd: String
    var paddingBlockStart: String
    var paddingBottom: String
    var paddingInlineEnd: String
    var paddingInlineStart: String
    var paddingLeft: String
    var paddingRight: String
    var paddingTop: String
    var pageBreakAfter: String
    var pageBreakBefore: String
    var pageBreakInside: String
    var paintOrder: String
    val parentRule: CSSRule?
    var perspective: String
    var perspectiveOrigin: String
    var placeContent: String
    var placeItems: String
    var placeSelf: String
    var pointerEvents: String
    var position: String
    var quotes: String
    var resize: String
    var right: String
    var rotate: String
    var rowGap: String
    var rubyAlign: String
    var rubyPosition: String
    var scale: String
    var scrollBehavior: String
    var shapeRendering: String
    var stopColor: String
    var stopOpacity: String
    var stroke: String
    var strokeDasharray: String
    var strokeDashoffset: String
    var strokeLinecap: String
    var strokeLinejoin: String
    var strokeMiterlimit: String
    var strokeOpacity: String
    var strokeWidth: String
    var tabSize: String
    var tableLayout: String
    var textAlign: String
    var textAlignLast: String
    var textAnchor: String
    var textCombineUpright: String
    var textDecoration: String
    var textDecorationColor: String
    var textDecorationLine: String
    var textDecorationStyle: String
    var textEmphasis: String
    var textEmphasisColor: String
    var textEmphasisPosition: String
    var textEmphasisStyle: String
    var textIndent: String
    var textJustify: String
    var textOrientation: String
    var textOverflow: String
    var textRendering: String
    var textShadow: String
    var textTransform: String
    var textUnderlinePosition: String
    var top: String
    var touchAction: String
    var transform: String
    var transformBox: String
    var transformOrigin: String
    var transformStyle: String
    var transition: String
    var transitionDelay: String
    var transitionDuration: String
    var transitionProperty: String
    var transitionTimingFunction: String
    var translate: String
    var unicodeBidi: String
    var userSelect: String
    var verticalAlign: String
    var visibility: String
    var webkitAlignContent: String
    var webkitAlignItems: String
    var webkitAlignSelf: String
    var webkitAnimation: String
    var webkitAnimationDelay: String
    var webkitAnimationDirection: String
    var webkitAnimationDuration: String
    var webkitAnimationFillMode: String
    var webkitAnimationIterationCount: String
    var webkitAnimationName: String
    var webkitAnimationPlayState: String
    var webkitAnimationTimingFunction: String
    var webkitAppearance: String
    var webkitBackfaceVisibility: String
    var webkitBackgroundClip: String
    var webkitBackgroundOrigin: String
    var webkitBackgroundSize: String
    var webkitBorderBottomLeftRadius: String
    var webkitBorderBottomRightRadius: String
    var webkitBorderRadius: String
    var webkitBorderTopLeftRadius: String
    var webkitBorderTopRightRadius: String
    var webkitBoxAlign: String
    var webkitBoxFlex: String
    var webkitBoxOrdinalGroup: String
    var webkitBoxOrient: String
    var webkitBoxPack: String
    var webkitBoxShadow: String
    var webkitBoxSizing: String
    var webkitFilter: String
    var webkitFlex: String
    var webkitFlexBasis: String
    var webkitFlexDirection: String
    var webkitFlexFlow: String
    var webkitFlexGrow: String
    var webkitFlexShrink: String
    var webkitFlexWrap: String
    var webkitJustifyContent: String
    var webkitLineClamp: String
    var webkitMask: String
    var webkitMaskBoxImage: String
    var webkitMaskBoxImageOutset: String
    var webkitMaskBoxImageRepeat: String
    var webkitMaskBoxImageSlice: String
    var webkitMaskBoxImageSource: String
    var webkitMaskBoxImageWidth: String
    var webkitMaskClip: String
    var webkitMaskComposite: String
    var webkitMaskImage: String
    var webkitMaskOrigin: String
    var webkitMaskPosition: String
    var webkitMaskRepeat: String
    var webkitMaskSize: String
    var webkitOrder: String
    var webkitPerspective: String
    var webkitPerspectiveOrigin: String
    var webkitTapHighlightColor: String
    var webkitTextFillColor: String
    var webkitTextSizeAdjust: String
    var webkitTextStroke: String
    var webkitTextStrokeColor: String
    var webkitTextStrokeWidth: String
    var webkitTransform: String
    var webkitTransformOrigin: String
    var webkitTransformStyle: String
    var webkitTransition: String
    var webkitTransitionDelay: String
    var webkitTransitionDuration: String
    var webkitTransitionProperty: String
    var webkitTransitionTimingFunction: String
    var webkitUserSelect: String
    var whiteSpace: String
    var widows: String
    var width: String
    var willChange: String
    var wordBreak: String
    var wordSpacing: String
    var wordWrap: String
    var writingMode: String
    var zIndex: String
    var zoom: String
    fun getPropertyPriority(property: String): String
    fun getPropertyValue(property: String): String
    fun item(index: Number): String
    fun removeProperty(property: String): String
    fun setProperty(property: String, value: String?, priority: String = definedExternally)
    @nativeGetter
    operator fun get(index: Number): String?
    @nativeSetter
    operator fun set(index: Number, value: String)
}

external interface CSSStyleSheet : StyleSheet {
    val cssRules: CSSRuleList
    val ownerRule: CSSRule?
    val rules: CSSRuleList
    fun addRule(selector: String = definedExternally, style: String = definedExternally, index: Number = definedExternally): Number
    fun deleteRule(index: Number)
    fun insertRule(rule: String, index: Number = definedExternally): Number
    fun removeRule(index: Number = definedExternally)
}

external interface Cache {
    fun addAll(requests: Iterable<Any /* Request | String */>): Promise<Unit>
    fun add(request: Request): Promise<Unit>
    fun add(request: String): Promise<Unit>
    fun addAll(requests: Array<Any /* Request | String */>): Promise<Unit>
    fun delete(request: Request, options: CacheQueryOptions = definedExternally): Promise<Boolean>
    fun delete(request: Request): Promise<Boolean>
    fun delete(request: String, options: CacheQueryOptions = definedExternally): Promise<Boolean>
    fun delete(request: String): Promise<Boolean>
    fun keys(request: Request = definedExternally, options: CacheQueryOptions = definedExternally): Promise<ReadonlyArray<Request>>
    fun keys(): Promise<ReadonlyArray<Request>>
    fun keys(request: Request = definedExternally): Promise<ReadonlyArray<Request>>
    fun keys(request: String = definedExternally, options: CacheQueryOptions = definedExternally): Promise<ReadonlyArray<Request>>
    fun keys(request: String = definedExternally): Promise<ReadonlyArray<Request>>
    fun match(request: Request, options: CacheQueryOptions = definedExternally): Promise<Response?>
    fun match(request: Request): Promise<Response?>
    fun match(request: String, options: CacheQueryOptions = definedExternally): Promise<Response?>
    fun match(request: String): Promise<Response?>
    fun matchAll(request: Request = definedExternally, options: CacheQueryOptions = definedExternally): Promise<ReadonlyArray<Response>>
    fun matchAll(): Promise<ReadonlyArray<Response>>
    fun matchAll(request: Request = definedExternally): Promise<ReadonlyArray<Response>>
    fun matchAll(request: String = definedExternally, options: CacheQueryOptions = definedExternally): Promise<ReadonlyArray<Response>>
    fun matchAll(request: String = definedExternally): Promise<ReadonlyArray<Response>>
    fun put(request: Request, response: Response): Promise<Unit>
    fun put(request: String, response: Response): Promise<Unit>
}

external interface CacheStorage {
    fun delete(cacheName: String): Promise<Boolean>
    fun has(cacheName: String): Promise<Boolean>
    fun keys(): Promise<Array<String>>
    fun match(request: Request, options: MultiCacheQueryOptions = definedExternally): Promise<Response?>
    fun match(request: Request): Promise<Response?>
    fun match(request: String, options: MultiCacheQueryOptions = definedExternally): Promise<Response?>
    fun match(request: String): Promise<Response?>
    fun open(cacheName: String): Promise<Cache>
}

external interface CanvasCompositing {
    var globalAlpha: Number
    var globalCompositeOperation: String
}

external interface CanvasDrawImage {
    fun drawImage(image: HTMLImageElement, dx: Number, dy: Number)
    fun drawImage(image: SVGImageElement, dx: Number, dy: Number)
    fun drawImage(image: HTMLVideoElement, dx: Number, dy: Number)
    fun drawImage(image: HTMLCanvasElement, dx: Number, dy: Number)
    fun drawImage(image: ImageBitmap, dx: Number, dy: Number)
    fun drawImage(image: OffscreenCanvas, dx: Number, dy: Number)
    fun drawImage(image: HTMLImageElement, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: SVGImageElement, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: HTMLVideoElement, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: HTMLCanvasElement, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: ImageBitmap, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: OffscreenCanvas, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: HTMLImageElement, sx: Number, sy: Number, sw: Number, sh: Number, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: SVGImageElement, sx: Number, sy: Number, sw: Number, sh: Number, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: HTMLVideoElement, sx: Number, sy: Number, sw: Number, sh: Number, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: HTMLCanvasElement, sx: Number, sy: Number, sw: Number, sh: Number, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: ImageBitmap, sx: Number, sy: Number, sw: Number, sh: Number, dx: Number, dy: Number, dw: Number, dh: Number)
    fun drawImage(image: OffscreenCanvas, sx: Number, sy: Number, sw: Number, sh: Number, dx: Number, dy: Number, dw: Number, dh: Number)
}

external interface CanvasDrawPath {
    fun beginPath()
    fun clip(fillRule: String /* "evenodd" | "nonzero" */ = definedExternally)
    fun clip()
    fun clip(path: Path2D, fillRule: String /* "evenodd" | "nonzero" */ = definedExternally)
    fun clip(path: Path2D)
    fun fill(fillRule: String /* "evenodd" | "nonzero" */ = definedExternally)
    fun fill()
    fun fill(path: Path2D, fillRule: String /* "evenodd" | "nonzero" */ = definedExternally)
    fun fill(path: Path2D)
    fun isPointInPath(x: Number, y: Number, fillRule: String /* "evenodd" | "nonzero" */ = definedExternally): Boolean
    fun isPointInPath(x: Number, y: Number): Boolean
    fun isPointInPath(path: Path2D, x: Number, y: Number, fillRule: String /* "evenodd" | "nonzero" */ = definedExternally): Boolean
    fun isPointInPath(path: Path2D, x: Number, y: Number): Boolean
    fun isPointInStroke(x: Number, y: Number): Boolean
    fun isPointInStroke(path: Path2D, x: Number, y: Number): Boolean
    fun stroke()
    fun stroke(path: Path2D)
}

external interface CanvasFillStrokeStyles {
    var fillStyle: dynamic /* String | CanvasGradient | CanvasPattern */
        get() = definedExternally
        set(value) = definedExternally
    var strokeStyle: dynamic /* String | CanvasGradient | CanvasPattern */
        get() = definedExternally
        set(value) = definedExternally
    fun createLinearGradient(x0: Number, y0: Number, x1: Number, y1: Number): CanvasGradient
    fun createPattern(image: HTMLImageElement, repetition: String?): CanvasPattern?
    fun createPattern(image: SVGImageElement, repetition: String?): CanvasPattern?
    fun createPattern(image: HTMLVideoElement, repetition: String?): CanvasPattern?
    fun createPattern(image: HTMLCanvasElement, repetition: String?): CanvasPattern?
    fun createPattern(image: ImageBitmap, repetition: String?): CanvasPattern?
    fun createPattern(image: OffscreenCanvas, repetition: String?): CanvasPattern?
    fun createRadialGradient(x0: Number, y0: Number, r0: Number, x1: Number, y1: Number, r1: Number): CanvasGradient
}

external interface CanvasFilters {
    var filter: String
}

external interface CanvasGradient {
    fun addColorStop(offset: Number, color: String)
}

external interface CanvasImageData {
    fun createImageData(sw: Number, sh: Number): ImageData
    fun createImageData(imagedata: ImageData): ImageData
    fun getImageData(sx: Number, sy: Number, sw: Number, sh: Number): ImageData
    fun putImageData(imagedata: ImageData, dx: Number, dy: Number)
    fun putImageData(imagedata: ImageData, dx: Number, dy: Number, dirtyX: Number, dirtyY: Number, dirtyWidth: Number, dirtyHeight: Number)
}

external interface CanvasImageSmoothing {
    var imageSmoothingEnabled: Boolean
    var imageSmoothingQuality: String /* "high" | "low" | "medium" */
}

external interface CanvasPath {
    fun arc(x: Number, y: Number, radius: Number, startAngle: Number, endAngle: Number, anticlockwise: Boolean = definedExternally)
    fun arcTo(x1: Number, y1: Number, x2: Number, y2: Number, radius: Number)
    fun bezierCurveTo(cp1x: Number, cp1y: Number, cp2x: Number, cp2y: Number, x: Number, y: Number)
    fun closePath()
    fun ellipse(x: Number, y: Number, radiusX: Number, radiusY: Number, rotation: Number, startAngle: Number, endAngle: Number, anticlockwise: Boolean = definedExternally)
    fun lineTo(x: Number, y: Number)
    fun moveTo(x: Number, y: Number)
    fun quadraticCurveTo(cpx: Number, cpy: Number, x: Number, y: Number)
    fun rect(x: Number, y: Number, w: Number, h: Number)
}

external interface CanvasPathDrawingStyles {
    fun setLineDash(segments: Iterable<Number>)
    var lineCap: String /* "butt" | "round" | "square" */
    var lineDashOffset: Number
    var lineJoin: String /* "bevel" | "miter" | "round" */
    var lineWidth: Number
    var miterLimit: Number
    fun getLineDash(): Array<Number>
    fun setLineDash(segments: Array<Number>)
}

external interface CanvasPattern {
    fun setTransform(transform: DOMMatrix2DInit = definedExternally)
}

external interface CanvasRect {
    fun clearRect(x: Number, y: Number, w: Number, h: Number)
    fun fillRect(x: Number, y: Number, w: Number, h: Number)
    fun strokeRect(x: Number, y: Number, w: Number, h: Number)
}

external interface CanvasRenderingContext2D : CanvasCompositing, CanvasDrawImage, CanvasDrawPath, CanvasFillStrokeStyles, CanvasFilters, CanvasImageData, CanvasImageSmoothing, CanvasPath, CanvasPathDrawingStyles, CanvasRect, CanvasShadowStyles, CanvasState, CanvasText, CanvasTextDrawingStyles, CanvasTransform, CanvasUserInterface {
    val canvas: HTMLCanvasElement
}

external interface CanvasShadowStyles {
    var shadowBlur: Number
    var shadowColor: String
    var shadowOffsetX: Number
    var shadowOffsetY: Number
}

external interface CanvasState {
    fun restore()
    fun save()
}

external interface CanvasText {
    fun fillText(text: String, x: Number, y: Number, maxWidth: Number = definedExternally)
    fun measureText(text: String): TextMetrics
    fun strokeText(text: String, x: Number, y: Number, maxWidth: Number = definedExternally)
}

external interface CanvasTextDrawingStyles {
    var direction: String /* "inherit" | "ltr" | "rtl" */
    var font: String
    var textAlign: String /* "center" | "end" | "left" | "right" | "start" */
    var textBaseline: String /* "alphabetic" | "bottom" | "hanging" | "ideographic" | "middle" | "top" */
}

external interface CanvasTransform {
    fun getTransform(): DOMMatrix
    fun resetTransform()
    fun rotate(angle: Number)
    fun scale(x: Number, y: Number)
    fun setTransform(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number)
    fun setTransform(transform: DOMMatrix2DInit = definedExternally)
    fun setTransform()
    fun transform(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number)
    fun translate(x: Number, y: Number)
}

external interface CanvasUserInterface {
    fun drawFocusIfNeeded(element: Element)
    fun drawFocusIfNeeded(path: Path2D, element: Element)
    fun scrollPathIntoView()
    fun scrollPathIntoView(path: Path2D)
}

external interface CaretPosition {
    val offset: Number
    val offsetNode: Node
    fun getClientRect(): DOMRect?
}

external interface ChannelMergerNode : AudioNode

external interface ChannelSplitterNode : AudioNode

external interface CharacterData : Node, ChildNode, NonDocumentTypeChildNode {
    var data: String
    val length: Number
    fun appendData(data: String)
    fun deleteData(offset: Number, count: Number)
    fun insertData(offset: Number, data: String)
    fun replaceData(offset: Number, count: Number, data: String)
    fun substringData(offset: Number, count: Number): String
}

external interface ChildNode : Node {
    fun after(vararg nodes: Any /* Node | String */)
    fun before(vararg nodes: Any /* Node | String */)
    fun remove()
    fun replaceWith(vararg nodes: Any /* Node | String */)
}

external interface Clipboard : EventTarget {
    fun readText(): Promise<String>
    fun writeText(data: String): Promise<Unit>
}

external interface ClipboardEvent : Event {
    val clipboardData: DataTransfer?
}

external interface CloseEvent : Event {
    val code: Number
    val reason: String
    val wasClean: Boolean
}

external interface Comment : CharacterData

external interface CompositionEvent : UIEvent {
    val data: String
}

external interface ConcatParams : Algorithm {
    var algorithmId: Uint8Array
    var hash: dynamic /* String? | Algorithm? */
        get() = definedExternally
        set(value) = definedExternally
    var partyUInfo: Uint8Array
    var partyVInfo: Uint8Array
    var privateInfo: Uint8Array?
        get() = definedExternally
        set(value) = definedExternally
    var publicInfo: Uint8Array?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConstantSourceNode : AudioScheduledSourceNode {
    val offset: AudioParam
    fun <K : String> addEventListener(type: K, listener: (self: ConstantSourceNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ConstantSourceNode, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ConstantSourceNode, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ConstantSourceNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ConstantSourceNode, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ConstantSourceNode, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface ConvolverNode : AudioNode {
    var buffer: AudioBuffer?
    var normalize: Boolean
}

external interface Coordinates {
    val accuracy: Number
    val altitude: Number?
    val altitudeAccuracy: Number?
    val heading: Number?
    val latitude: Number
    val longitude: Number
    val speed: Number?
}

external interface Credential {
    val id: String
    val type: String
}

external interface CredentialsContainer {
    fun create(options: CredentialCreationOptions = definedExternally): Promise<Credential?>
    fun get(options: CredentialRequestOptions = definedExternally): Promise<Credential?>
    fun preventSilentAccess(): Promise<Unit>
    fun store(credential: Credential): Promise<Credential>
}

external interface Crypto {
    val subtle: SubtleCrypto
    fun <T> getRandomValues(array: T): T
}

external interface CryptoKey {
    val algorithm: KeyAlgorithm
    val extractable: Boolean
    val type: String /* "private" | "public" | "secret" */
    val usages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>
}

external interface CryptoKeyPair {
    var privateKey: CryptoKey
    var publicKey: CryptoKey
}

external interface CustomElementRegistry {
    fun define(name: String, constructor: CustomElementConstructor, options: ElementDefinitionOptions = definedExternally)
    fun get(name: String): Any
    fun upgrade(root: Node)
    fun whenDefined(name: String): Promise<Unit>
}

external interface CustomEvent<T> : Event {
    val detail: T
    fun initCustomEvent(typeArg: String, canBubbleArg: Boolean, cancelableArg: Boolean, detailArg: T)
}

external interface CustomEvent__0 : CustomEvent<Any>

external interface DOMException {
    val code: Number
    val message: String
    val name: String
    val ABORT_ERR: Number
    val DATA_CLONE_ERR: Number
    val DOMSTRING_SIZE_ERR: Number
    val HIERARCHY_REQUEST_ERR: Number
    val INDEX_SIZE_ERR: Number
    val INUSE_ATTRIBUTE_ERR: Number
    val INVALID_ACCESS_ERR: Number
    val INVALID_CHARACTER_ERR: Number
    val INVALID_MODIFICATION_ERR: Number
    val INVALID_NODE_TYPE_ERR: Number
    val INVALID_STATE_ERR: Number
    val NAMESPACE_ERR: Number
    val NETWORK_ERR: Number
    val NOT_FOUND_ERR: Number
    val NOT_SUPPORTED_ERR: Number
    val NO_DATA_ALLOWED_ERR: Number
    val NO_MODIFICATION_ALLOWED_ERR: Number
    val QUOTA_EXCEEDED_ERR: Number
    val SECURITY_ERR: Number
    val SYNTAX_ERR: Number
    val TIMEOUT_ERR: Number
    val TYPE_MISMATCH_ERR: Number
    val URL_MISMATCH_ERR: Number
    val VALIDATION_ERR: Number
    val WRONG_DOCUMENT_ERR: Number
}

external interface DOMImplementation {
    fun createDocument(namespaceURI: String?, qualifiedName: String?, doctype: DocumentType?): Document
    fun createDocumentType(qualifiedName: String, publicId: String, systemId: String): DocumentType
    fun createHTMLDocument(title: String = definedExternally): Document
    fun hasFeature(vararg args: Any): Boolean
}

external interface DOML2DeprecatedColorProperty {
    var color: String
}

external interface DOMMatrix : DOMMatrixReadOnly {
    override var a: Number
    override var b: Number
    override var c: Number
    override var d: Number
    override var e: Number
    override var f: Number
    override var m11: Number
    override var m12: Number
    override var m13: Number
    override var m14: Number
    override var m21: Number
    override var m22: Number
    override var m23: Number
    override var m24: Number
    override var m31: Number
    override var m32: Number
    override var m33: Number
    override var m34: Number
    override var m41: Number
    override var m42: Number
    override var m43: Number
    override var m44: Number
    fun invertSelf(): DOMMatrix
    fun multiplySelf(other: DOMMatrixInit = definedExternally): DOMMatrix
    fun preMultiplySelf(other: DOMMatrixInit = definedExternally): DOMMatrix
    fun rotateAxisAngleSelf(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, angle: Number = definedExternally): DOMMatrix
    fun rotateFromVectorSelf(x: Number = definedExternally, y: Number = definedExternally): DOMMatrix
    fun rotateSelf(rotX: Number = definedExternally, rotY: Number = definedExternally, rotZ: Number = definedExternally): DOMMatrix
    fun scale3dSelf(scale: Number = definedExternally, originX: Number = definedExternally, originY: Number = definedExternally, originZ: Number = definedExternally): DOMMatrix
    fun scaleSelf(scaleX: Number = definedExternally, scaleY: Number = definedExternally, scaleZ: Number = definedExternally, originX: Number = definedExternally, originY: Number = definedExternally, originZ: Number = definedExternally): DOMMatrix
    fun setMatrixValue(transformList: String): DOMMatrix
    fun skewXSelf(sx: Number = definedExternally): DOMMatrix
    fun skewYSelf(sy: Number = definedExternally): DOMMatrix
    fun translateSelf(tx: Number = definedExternally, ty: Number = definedExternally, tz: Number = definedExternally): DOMMatrix
}

typealias SVGMatrix = DOMMatrix

external interface DOMMatrixReadOnly {
    val a: Number
    val b: Number
    val c: Number
    val d: Number
    val e: Number
    val f: Number
    val is2D: Boolean
    val isIdentity: Boolean
    val m11: Number
    val m12: Number
    val m13: Number
    val m14: Number
    val m21: Number
    val m22: Number
    val m23: Number
    val m24: Number
    val m31: Number
    val m32: Number
    val m33: Number
    val m34: Number
    val m41: Number
    val m42: Number
    val m43: Number
    val m44: Number
    fun flipX(): DOMMatrix
    fun flipY(): DOMMatrix
    fun inverse(): DOMMatrix
    fun multiply(other: DOMMatrixInit = definedExternally): DOMMatrix
    fun rotate(rotX: Number = definedExternally, rotY: Number = definedExternally, rotZ: Number = definedExternally): DOMMatrix
    fun rotateAxisAngle(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, angle: Number = definedExternally): DOMMatrix
    fun rotateFromVector(x: Number = definedExternally, y: Number = definedExternally): DOMMatrix
    fun scale(scaleX: Number = definedExternally, scaleY: Number = definedExternally, scaleZ: Number = definedExternally, originX: Number = definedExternally, originY: Number = definedExternally, originZ: Number = definedExternally): DOMMatrix
    fun scale3d(scale: Number = definedExternally, originX: Number = definedExternally, originY: Number = definedExternally, originZ: Number = definedExternally): DOMMatrix
    fun scaleNonUniform(scaleX: Number = definedExternally, scaleY: Number = definedExternally): DOMMatrix
    fun skewX(sx: Number = definedExternally): DOMMatrix
    fun skewY(sy: Number = definedExternally): DOMMatrix
    fun toFloat32Array(): Float32Array
    fun toFloat64Array(): Float64Array
    fun toJSON(): Any
    fun transformPoint(point: DOMPointInit = definedExternally): DOMPoint
    fun translate(tx: Number = definedExternally, ty: Number = definedExternally, tz: Number = definedExternally): DOMMatrix
    override fun toString(): String
}

external interface DOMPoint : DOMPointReadOnly {
    override var w: Number
    override var x: Number
    override var y: Number
    override var z: Number
}

typealias SVGPoint = DOMPoint

external interface DOMPointReadOnly {
    val w: Number
    val x: Number
    val y: Number
    val z: Number
    fun matrixTransform(matrix: DOMMatrixInit = definedExternally): DOMPoint
    fun toJSON(): Any
}

external interface DOMRect : DOMRectReadOnly {
    override var height: Number
    override var width: Number
    override var x: Number
    override var y: Number
}

typealias SVGRect = DOMRect

external interface DOMRectList {
    val length: Number
    fun item(index: Number): DOMRect?
    @nativeGetter
    operator fun get(index: Number): DOMRect?
    @nativeSetter
    operator fun set(index: Number, value: DOMRect)
}

external interface DOMRectReadOnly {
    val bottom: Number
    val height: Number
    val left: Number
    val right: Number
    val top: Number
    val width: Number
    val x: Number
    val y: Number
    fun toJSON(): Any
}

external interface DOMStringList {
    val length: Number
    fun contains(string: String): Boolean
    fun item(index: Number): String?
    @nativeGetter
    operator fun get(index: Number): String?
    @nativeSetter
    operator fun set(index: Number, value: String)
}

external interface DOMStringMap {
    @nativeGetter
    operator fun get(name: String): String?
    @nativeSetter
    operator fun set(name: String, value: String?)
}

external interface DOMTokenList {
    fun entries(): IterableIterator<dynamic /* JsTuple<Number, String> */>
    fun keys(): IterableIterator<Number>
    fun values(): IterableIterator<String>
    val length: Number
    var value: String
    override fun toString(): String
    fun add(vararg tokens: String)
    fun contains(token: String): Boolean
    fun item(index: Number): String?
    fun remove(vararg tokens: String)
    fun replace(oldToken: String, newToken: String)
    fun supports(token: String): Boolean
    fun toggle(token: String, force: Boolean = definedExternally): Boolean
    fun forEach(callbackfn: (value: String, key: Number, parent: DOMTokenList) -> Unit, thisArg: Any = definedExternally)
    @nativeGetter
    operator fun get(index: Number): String?
    @nativeSetter
    operator fun set(index: Number, value: String)
}

external interface DataTransfer {
    var dropEffect: String
    var effectAllowed: String
    val files: FileList
    val items: DataTransferItemList
    val types: ReadonlyArray<String>
    fun clearData(format: String = definedExternally)
    fun getData(format: String): String
    fun setData(format: String, data: String)
    fun setDragImage(image: Element, x: Number, y: Number)
}

external interface DataTransferItem {
    val kind: String
    val type: String
    fun getAsFile(): File?
    fun getAsString(callback: FunctionStringCallback?)
    fun webkitGetAsEntry(): Any
}

external interface DataTransferItemList {
    val length: Number
    fun add(data: String, type: String): DataTransferItem?
    fun add(data: File): DataTransferItem?
    fun clear()
    fun item(index: Number): DataTransferItem
    fun remove(index: Number)
    @nativeGetter
    operator fun get(name: Number): DataTransferItem?
    @nativeSetter
    operator fun set(name: Number, value: DataTransferItem)
}

external interface DeferredPermissionRequest {
    val id: Number
    val type: String /* "geolocation" | "media" | "pointerlock" | "unlimitedIndexedDBQuota" | "webnotifications" */
    val uri: String
    fun allow()
    fun deny()
}

external interface DelayNode : AudioNode {
    val delayTime: AudioParam
}

external interface DeviceLightEvent : Event {
    val value: Number
}

external interface DeviceMotionEvent : Event {
    val acceleration: DeviceMotionEventAcceleration?
    val accelerationIncludingGravity: DeviceMotionEventAcceleration?
    val interval: Number
    val rotationRate: DeviceMotionEventRotationRate?
}

external interface DeviceMotionEventAcceleration {
    val x: Number?
    val y: Number?
    val z: Number?
}

external interface DeviceMotionEventRotationRate {
    val alpha: Number?
    val beta: Number?
    val gamma: Number?
}

external interface DeviceOrientationEvent : Event {
    val absolute: Boolean
    val alpha: Number?
    val beta: Number?
    val gamma: Number?
}

external interface DhImportKeyParams : Algorithm {
    var generator: Uint8Array
    var prime: Uint8Array
}

external interface DhKeyDeriveParams : Algorithm {
    var public: CryptoKey
}

external interface DhKeyGenParams : Algorithm {
    var generator: Uint8Array
    var prime: Uint8Array
}

external interface DocumentEventMap : GlobalEventHandlersEventMap, DocumentAndElementEventHandlersEventMap {
    var fullscreenchange: Event
    var fullscreenerror: Event
    var pointerlockchange: Event
    var pointerlockerror: Event
    var readystatechange: Event
    var visibilitychange: Event
}

external interface Document : Node, DocumentAndElementEventHandlers, DocumentOrShadowRoot, GlobalEventHandlers, NonElementParentNode, ParentNode, XPathEvaluatorBase {
    val URL: String
    var alinkColor: String
    val all: HTMLAllCollection
    val anchors: HTMLCollectionOf<HTMLAnchorElement>
    val applets: HTMLCollectionOf<HTMLAppletElement>
    var bgColor: String
    var body: HTMLElement
    val characterSet: String
    val charset: String
    val compatMode: String
    val contentType: String
    var cookie: String
    val currentScript: dynamic /* HTMLScriptElement? | SVGScriptElement? */
        get() = definedExternally
    val defaultView: dynamic /* typealias WindowProxy = dynamic */
        get() = definedExternally
    var designMode: String
    var dir: String
    val doctype: DocumentType?
    val documentElement: HTMLElement
    val documentURI: String
    var domain: String
    val embeds: HTMLCollectionOf<HTMLEmbedElement>
    var fgColor: String
    val forms: HTMLCollectionOf<HTMLFormElement>
    val fullscreen: Boolean
    val fullscreenEnabled: Boolean
    val head: HTMLHeadElement
    val hidden: Boolean
    val images: HTMLCollectionOf<HTMLImageElement>
    val implementation: DOMImplementation
    val inputEncoding: String
    val lastModified: String
    var linkColor: String
    val links: HTMLCollectionOf<dynamic /* HTMLAnchorElement | HTMLAreaElement */>
    var location: Location
    var onfullscreenchange: ((self: Document, ev: Event) -> Any)?
    var onfullscreenerror: ((self: Document, ev: Event) -> Any)?
    var onpointerlockchange: ((self: Document, ev: Event) -> Any)?
    var onpointerlockerror: ((self: Document, ev: Event) -> Any)?
    var onreadystatechange: ((self: Document, ev: Event) -> Any)?
    var onvisibilitychange: ((self: Document, ev: Event) -> Any)?
    val origin: String
    val plugins: HTMLCollectionOf<HTMLEmbedElement>
    val readyState: String /* "complete" | "interactive" | "loading" */
    val referrer: String
    val scripts: HTMLCollectionOf<HTMLScriptElement>
    val scrollingElement: Element?
    val timeline: DocumentTimeline
    var title: String
    val visibilityState: String /* "hidden" | "visible" */
    var vlinkColor: String
    fun <T : Node> adoptNode(source: T): T
    fun captureEvents()
    override fun caretPositionFromPoint(x: Number, y: Number): CaretPosition?
    override fun caretRangeFromPoint(x: Number, y: Number): Range
    fun clear()
    fun close()
    fun createAttribute(localName: String): Attr
    fun createAttributeNS(namespace: String?, qualifiedName: String): Attr
    fun createCDATASection(data: String): CDATASection
    fun createComment(data: String): Comment
    fun createDocumentFragment(): DocumentFragment
    fun <K : String> createElement(tagName: K, options: ElementCreationOptions = definedExternally): Any
    fun <K : String> createElement(tagName: K): Any
    fun createElement(tagName: String, options: ElementCreationOptions = definedExternally): HTMLElement
    fun createElement(tagName: String): HTMLElement
    fun createElementNS(namespaceURI: String /* "http://www.w3.org/1999/xhtml" | "http://www.w3.org/2000/svg" */, qualifiedName: String): dynamic /* HTMLElement | SVGElement */
    fun <K : String> createElementNS(namespaceURI: String /* "http://www.w3.org/2000/svg" */, qualifiedName: K): Any
    fun createElementNS(namespaceURI: String?, qualifiedName: String, options: ElementCreationOptions = definedExternally): Element
    fun createElementNS(namespaceURI: String?, qualifiedName: String): Element
    fun createElementNS(namespace: String?, qualifiedName: String, options: String = definedExternally): Element
    fun createEvent(eventInterface: String /* "AnimationEvent" | "AnimationPlaybackEvent" | "AudioProcessingEvent" | "BeforeUnloadEvent" | "ClipboardEvent" | "CloseEvent" | "CompositionEvent" | "CustomEvent" | "DeviceLightEvent" | "DeviceMotionEvent" | "DeviceOrientationEvent" | "DragEvent" | "ErrorEvent" | "Event" | "Events" | "FocusEvent" | "FocusNavigationEvent" | "GamepadEvent" | "HashChangeEvent" | "IDBVersionChangeEvent" | "InputEvent" | "KeyboardEvent" | "ListeningStateChangedEvent" | "MSGestureEvent" | "MSMediaKeyMessageEvent" | "MSMediaKeyNeededEvent" | "MSPointerEvent" | "MediaEncryptedEvent" | "MediaKeyMessageEvent" | "MediaQueryListEvent" | "MediaStreamErrorEvent" | "MediaStreamEvent" | "MediaStreamTrackEvent" | "MessageEvent" | "MouseEvent" | "MouseEvents" | "MutationEvent" | "MutationEvents" | "OfflineAudioCompletionEvent" | "OverflowEvent" | "PageTransitionEvent" | "PaymentRequestUpdateEvent" | "PermissionRequestedEvent" | "PointerEvent" | "PopStateEvent" | "ProgressEvent" | "PromiseRejectionEvent" | "RTCDTMFToneChangeEvent" | "RTCDataChannelEvent" | "RTCDtlsTransportStateChangedEvent" | "RTCErrorEvent" | "RTCIceCandidatePairChangedEvent" | "RTCIceGathererEvent" | "RTCIceTransportStateChangedEvent" | "RTCPeerConnectionIceErrorEvent" | "RTCPeerConnectionIceEvent" | "RTCSsrcConflictEvent" | "RTCStatsEvent" | "RTCTrackEvent" | "SVGZoomEvent" | "SVGZoomEvents" | "SecurityPolicyViolationEvent" | "ServiceWorkerMessageEvent" | "SpeechRecognitionEvent" | "SpeechSynthesisErrorEvent" | "SpeechSynthesisEvent" | "StorageEvent" | "TextEvent" | "TouchEvent" | "TrackEvent" | "TransitionEvent" | "UIEvent" | "UIEvents" | "VRDisplayEvent" | "VRDisplayEvent " | "WebGLContextEvent" | "WheelEvent" */): dynamic /* Event */
    fun createNodeIterator(root: Node, whatToShow: Number = definedExternally, filter: NodeFilter? = definedExternally): NodeIterator
    fun createProcessingInstruction(target: String, data: String): ProcessingInstruction
    fun createRange(): Range
    fun createTextNode(data: String): Text
    fun createTreeWalker(root: Node, whatToShow: Number = definedExternally, filter: NodeFilter? = definedExternally): TreeWalker
    fun createTreeWalker(root: Node): TreeWalker
    fun createTreeWalker(root: Node, whatToShow: Number = definedExternally): TreeWalker
    fun createTreeWalker(root: Node, whatToShow: Number, filter: NodeFilter?, entityReferenceExpansion: Boolean = definedExternally): TreeWalker
    override fun elementFromPoint(x: Number, y: Number): Element?
    override fun elementsFromPoint(x: Number, y: Number): Array<Element>
    fun execCommand(commandId: String, showUI: Boolean = definedExternally, value: String = definedExternally): Boolean
    fun exitFullscreen(): Promise<Unit>
    fun exitPointerLock()
    fun getAnimations(): Array<Animation>
    override fun getElementById(elementId: String): HTMLElement?
    fun getElementsByClassName(classNames: String): HTMLCollectionOf<Element>
    fun getElementsByName(elementName: String): NodeListOf<HTMLElement>
    fun <K : String> getElementsByTagName(qualifiedName: K): HTMLCollectionOf<Any>
    fun getElementsByTagName(qualifiedName: String): HTMLCollectionOf<Element>
    fun getElementsByTagNameNS(namespaceURI: String /* "http://www.w3.org/1999/xhtml" | "http://www.w3.org/2000/svg" */, localName: String): dynamic /* HTMLCollectionOf */
    override fun getSelection(): Selection?
    fun hasFocus(): Boolean
    fun <T : Node> importNode(importedNode: T, deep: Boolean): T
    fun open(url: String = definedExternally, name: String = definedExternally, features: String = definedExternally, replace: Boolean = definedExternally): Document
    fun queryCommandEnabled(commandId: String): Boolean
    fun queryCommandIndeterm(commandId: String): Boolean
    fun queryCommandState(commandId: String): Boolean
    fun queryCommandSupported(commandId: String): Boolean
    fun queryCommandValue(commandId: String): String
    fun releaseEvents()
    fun write(vararg text: String)
    fun writeln(vararg text: String)
    fun <K : String> addEventListener(type: K, listener: (self: Document, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Document, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Document, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Document, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Document, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Document, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface DocumentAndElementEventHandlersEventMap {
    var copy: ClipboardEvent
    var cut: ClipboardEvent
    var paste: ClipboardEvent
}

external interface DocumentAndElementEventHandlers {
    var oncopy: ((self: DocumentAndElementEventHandlers, ev: ClipboardEvent) -> Any)?
    var oncut: ((self: DocumentAndElementEventHandlers, ev: ClipboardEvent) -> Any)?
    var onpaste: ((self: DocumentAndElementEventHandlers, ev: ClipboardEvent) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: DocumentAndElementEventHandlers, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: DocumentAndElementEventHandlers, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: DocumentAndElementEventHandlers, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListener)
    fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: DocumentAndElementEventHandlers, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: DocumentAndElementEventHandlers, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: DocumentAndElementEventHandlers, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListener)
    fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
}

external interface DocumentEvent {
    fun createEvent(eventInterface: String /* "AnimationEvent" | "AnimationPlaybackEvent" | "AudioProcessingEvent" | "BeforeUnloadEvent" | "ClipboardEvent" | "CloseEvent" | "CompositionEvent" | "CustomEvent" | "DeviceLightEvent" | "DeviceMotionEvent" | "DeviceOrientationEvent" | "DragEvent" | "ErrorEvent" | "Event" | "Events" | "FocusEvent" | "FocusNavigationEvent" | "GamepadEvent" | "HashChangeEvent" | "IDBVersionChangeEvent" | "InputEvent" | "KeyboardEvent" | "ListeningStateChangedEvent" | "MSGestureEvent" | "MSMediaKeyMessageEvent" | "MSMediaKeyNeededEvent" | "MSPointerEvent" | "MediaEncryptedEvent" | "MediaKeyMessageEvent" | "MediaQueryListEvent" | "MediaStreamErrorEvent" | "MediaStreamEvent" | "MediaStreamTrackEvent" | "MessageEvent" | "MouseEvent" | "MouseEvents" | "MutationEvent" | "MutationEvents" | "OfflineAudioCompletionEvent" | "OverflowEvent" | "PageTransitionEvent" | "PaymentRequestUpdateEvent" | "PermissionRequestedEvent" | "PointerEvent" | "PopStateEvent" | "ProgressEvent" | "PromiseRejectionEvent" | "RTCDTMFToneChangeEvent" | "RTCDataChannelEvent" | "RTCDtlsTransportStateChangedEvent" | "RTCErrorEvent" | "RTCIceCandidatePairChangedEvent" | "RTCIceGathererEvent" | "RTCIceTransportStateChangedEvent" | "RTCPeerConnectionIceErrorEvent" | "RTCPeerConnectionIceEvent" | "RTCSsrcConflictEvent" | "RTCStatsEvent" | "RTCTrackEvent" | "SVGZoomEvent" | "SVGZoomEvents" | "SecurityPolicyViolationEvent" | "ServiceWorkerMessageEvent" | "SpeechRecognitionEvent" | "SpeechSynthesisErrorEvent" | "SpeechSynthesisEvent" | "StorageEvent" | "TextEvent" | "TouchEvent" | "TrackEvent" | "TransitionEvent" | "UIEvent" | "UIEvents" | "VRDisplayEvent" | "VRDisplayEvent " | "WebGLContextEvent" | "WheelEvent" */): dynamic /* Event */
}

external interface DocumentFragment : Node, NonElementParentNode, ParentNode {
    override fun getElementById(elementId: String): HTMLElement?
}

external interface DocumentOrShadowRoot {
    val activeElement: Element?
    val fullscreenElement: Element?
    val pointerLockElement: Element?
    val styleSheets: StyleSheetList
    fun caretPositionFromPoint(x: Number, y: Number): CaretPosition?
    fun caretRangeFromPoint(x: Number, y: Number): Range
    fun elementFromPoint(x: Number, y: Number): Element?
    fun elementsFromPoint(x: Number, y: Number): Array<Element>
    fun getSelection(): Selection?
}

external interface DocumentTimeline : AnimationTimeline

external interface DocumentType : Node, ChildNode {
    val name: String
    val publicId: String
    val systemId: String
}

external interface DragEvent : MouseEvent {
    val dataTransfer: DataTransfer?
}

external interface DynamicsCompressorNode : AudioNode {
    val attack: AudioParam
    val knee: AudioParam
    val ratio: AudioParam
    val reduction: Number
    val release: AudioParam
    val threshold: AudioParam
}

external interface EXT_blend_minmax {
    val MAX_EXT: GLenum
    val MIN_EXT: GLenum
}

external interface EXT_frag_depth

external interface EXT_sRGB {
    val FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING_EXT: GLenum
    val SRGB8_ALPHA8_EXT: GLenum
    val SRGB_ALPHA_EXT: GLenum
    val SRGB_EXT: GLenum
}

external interface EXT_shader_texture_lod

external interface EXT_texture_filter_anisotropic {
    val MAX_TEXTURE_MAX_ANISOTROPY_EXT: GLenum
    val TEXTURE_MAX_ANISOTROPY_EXT: GLenum
}

external interface ElementEventMap {
    var fullscreenchange: Event
    var fullscreenerror: Event
}

external interface Element : Node, Animatable, ChildNode, InnerHTML, NonDocumentTypeChildNode, ParentNode, Slotable {
    override val assignedSlot: HTMLSlotElement?
    val attributes: NamedNodeMap
    val classList: DOMTokenList
    var className: String
    val clientHeight: Number
    val clientLeft: Number
    val clientTop: Number
    val clientWidth: Number
    var id: String
    val localName: String
    override val namespaceURI: String?
    var onfullscreenchange: ((self: Element, ev: Event) -> Any)?
    var onfullscreenerror: ((self: Element, ev: Event) -> Any)?
    var outerHTML: String
    val prefix: String?
    val scrollHeight: Number
    var scrollLeft: Number
    var scrollTop: Number
    val scrollWidth: Number
    val shadowRoot: ShadowRoot?
    var slot: String
    val tagName: String
    fun attachShadow(init: ShadowRootInit): ShadowRoot
    fun <K : String> closest(selector: K): Any
    fun <E : Element> closest(selector: String): E?
    fun getAttribute(qualifiedName: String): String?
    fun getAttributeNS(namespace: String?, localName: String): String?
    fun getAttributeNames(): Array<String>
    fun getAttributeNode(name: String): Attr?
    fun getAttributeNodeNS(namespaceURI: String, localName: String): Attr?
    fun getBoundingClientRect(): DOMRect
    fun getClientRects(): DOMRectList
    fun getElementsByClassName(classNames: String): HTMLCollectionOf<Element>
    fun <K : String> getElementsByTagName(qualifiedName: K): HTMLCollectionOf<Any>
    fun getElementsByTagName(qualifiedName: String): HTMLCollectionOf<Element>
    fun getElementsByTagNameNS(namespaceURI: String /* "http://www.w3.org/1999/xhtml" | "http://www.w3.org/2000/svg" */, localName: String): dynamic /* HTMLCollectionOf */
    fun hasAttribute(qualifiedName: String): Boolean
    fun hasAttributeNS(namespace: String?, localName: String): Boolean
    fun hasAttributes(): Boolean
    fun hasPointerCapture(pointerId: Number): Boolean
    fun insertAdjacentElement(position: String /* "beforebegin" | "afterbegin" | "beforeend" | "afterend" */, insertedElement: Element): Element?
    fun insertAdjacentHTML(where: String /* "beforebegin" | "afterbegin" | "beforeend" | "afterend" */, html: String)
    fun insertAdjacentText(where: String /* "beforebegin" | "afterbegin" | "beforeend" | "afterend" */, text: String)
    fun matches(selectors: String): Boolean
    fun msGetRegionContent(): Any
    fun releasePointerCapture(pointerId: Number)
    fun removeAttribute(qualifiedName: String)
    fun removeAttributeNS(namespace: String?, localName: String)
    fun removeAttributeNode(attr: Attr): Attr
    fun requestFullscreen(options: FullscreenOptions = definedExternally): Promise<Unit>
    fun requestPointerLock()
    fun scroll(options: ScrollToOptions = definedExternally)
    fun scroll()
    fun scroll(x: Number, y: Number)
    fun scrollBy(options: ScrollToOptions = definedExternally)
    fun scrollBy()
    fun scrollBy(x: Number, y: Number)
    fun scrollIntoView(arg: Boolean = definedExternally)
    fun scrollIntoView()
    fun scrollIntoView(arg: ScrollIntoViewOptions = definedExternally)
    fun scrollTo(options: ScrollToOptions = definedExternally)
    fun scrollTo()
    fun scrollTo(x: Number, y: Number)
    fun setAttribute(qualifiedName: String, value: String)
    fun setAttributeNS(namespace: String?, qualifiedName: String, value: String)
    fun setAttributeNode(attr: Attr): Attr?
    fun setAttributeNodeNS(attr: Attr): Attr?
    fun setPointerCapture(pointerId: Number)
    fun toggleAttribute(qualifiedName: String, force: Boolean = definedExternally): Boolean
    fun webkitMatchesSelector(selectors: String): Boolean
    fun <K : String> addEventListener(type: K, listener: (self: Element, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Element, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Element, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Element, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Element, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Element, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface ElementCSSInlineStyle {
    val style: CSSStyleDeclaration
}

external interface ElementContentEditable {
    var contentEditable: String
    var inputMode: String
    val isContentEditable: Boolean
}

external interface ErrorEvent : Event {
    val colno: Number
    val error: Any
    val filename: String
    val lineno: Number
    val message: String
}

external interface Event {
    val bubbles: Boolean
    var cancelBubble: Boolean
    val cancelable: Boolean
    val composed: Boolean
    val currentTarget: EventTarget?
    val defaultPrevented: Boolean
    val eventPhase: Number
    val isTrusted: Boolean
    var returnValue: Boolean
    val srcElement: EventTarget?
    val target: EventTarget?
    val timeStamp: Number
    val type: String
    fun composedPath(): Array<EventTarget>
    fun initEvent(type: String, bubbles: Boolean = definedExternally, cancelable: Boolean = definedExternally)
    fun preventDefault()
    fun stopImmediatePropagation()
    fun stopPropagation()
    val AT_TARGET: Number
    val BUBBLING_PHASE: Number
    val CAPTURING_PHASE: Number
    val NONE: Number
}

external interface EventListenerObject {
    fun handleEvent(evt: Event)
}

external interface EventTarget {
    fun addEventListener(type: String, listener: EventListener?, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListener?)
    fun addEventListener(type: String, listener: EventListener?, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions = definedExternally)
    fun dispatchEvent(event: Event): Boolean
    fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, callback: EventListener?)
    fun removeEventListener(type: String, callback: EventListener?, options: Boolean = definedExternally)
    fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean = definedExternally)
}

external interface ExtensionScriptApis {
    fun extensionIdToShortId(extensionId: String): Number
    fun fireExtensionApiTelemetry(functionName: String, isSucceeded: Boolean, isSupported: Boolean, errorString: String)
    fun genericFunction(routerAddress: Any, parameters: String = definedExternally, callbackId: Number = definedExternally)
    fun genericSynchronousFunction(functionId: Number, parameters: String = definedExternally): String
    fun genericWebRuntimeCallout(to: Any, from: Any, payload: String)
    fun getExtensionId(): String
    fun registerGenericFunctionCallbackHandler(callbackHandler: Function<*>)
    fun registerGenericPersistentCallbackHandler(callbackHandler: Function<*>)
    fun registerWebRuntimeCallbackHandler(handler: Function<*>): Any
}

external interface External {
    fun AddSearchProvider()
    fun IsSearchProviderInstalled()
}

external interface File : Blob {
    val lastModified: Number
    val name: String
}

external interface FileList {
    val length: Number
    fun item(index: Number): File?
    @nativeGetter
    operator fun get(index: Number): File?
    @nativeSetter
    operator fun set(index: Number, value: File)
}

external interface FocusEvent : UIEvent {
    val relatedTarget: EventTarget?
}

external interface FocusNavigationEvent : Event {
    val navigationReason: String /* "down" | "left" | "right" | "up" */
    val originHeight: Number
    val originLeft: Number
    val originTop: Number
    val originWidth: Number
    fun requestFocus()
}

external interface FormData {
    fun entries(): IterableIterator<dynamic /* JsTuple<String, dynamic> */>
    fun keys(): IterableIterator<String>
    fun values(): IterableIterator<dynamic /* File | String */>
    fun append(name: String, value: String, fileName: String = definedExternally)
    fun append(name: String, value: String)
    fun append(name: String, value: Blob, fileName: String = definedExternally)
    fun append(name: String, value: Blob)
    fun delete(name: String)
    fun get(name: String): dynamic /* File? | String? */
    fun getAll(name: String): Array<dynamic /* File | String */>
    fun has(name: String): Boolean
    fun set(name: String, value: String, fileName: String = definedExternally)
    fun set(name: String, value: String)
    fun set(name: String, value: Blob, fileName: String = definedExternally)
    fun set(name: String, value: Blob)
    fun forEach(callbackfn: (value: Any /* File | String */, key: String, parent: FormData) -> Unit, thisArg: Any = definedExternally)
}

external interface GainNode : AudioNode {
    val gain: AudioParam
}

external interface Gamepad {
    val hapticActuators: Array<GamepadHapticActuator>
    val axes: ReadonlyArray<Number>
    val buttons: ReadonlyArray<GamepadButton>
    val connected: Boolean
    val hand: String /* "" | "left" | "right" */
    val hapticActuators: ReadonlyArray<GamepadHapticActuator>
    val id: String
    val index: Number
    val mapping: String /* "" | "standard" */
    val pose: GamepadPose?
    val timestamp: Number
}

external interface GamepadButton {
    val pressed: Boolean
    val touched: Boolean
    val value: Number
}

external interface GamepadEvent : Event {
    val gamepad: Gamepad
}

external interface GamepadHapticActuator {
    fun pulse(value: Number, duration: Number): Promise<Boolean>
    val type: String /* "vibration" */
}

external interface GamepadPose {
    val angularAcceleration: Float32Array?
    val angularVelocity: Float32Array?
    val hasOrientation: Boolean
    val hasPosition: Boolean
    val linearAcceleration: Float32Array?
    val linearVelocity: Float32Array?
    val orientation: Float32Array?
    val position: Float32Array?
}

external interface Geolocation {
    fun clearWatch(watchId: Number)
    fun getCurrentPosition(successCallback: PositionCallback, errorCallback: PositionErrorCallback = definedExternally, options: PositionOptions = definedExternally)
    fun watchPosition(successCallback: PositionCallback, errorCallback: PositionErrorCallback = definedExternally, options: PositionOptions = definedExternally): Number
}

external interface GlobalEventHandlersEventMap {
    var beforexrselect: XRSessionEvent
    var abort: UIEvent
    var animationcancel: AnimationEvent
    var animationend: AnimationEvent
    var animationiteration: AnimationEvent
    var animationstart: AnimationEvent
    var auxclick: MouseEvent
    var blur: FocusEvent
    var cancel: Event
    var canplay: Event
    var canplaythrough: Event
    var change: Event
    var click: MouseEvent
    var close: Event
    var contextmenu: MouseEvent
    var cuechange: Event
    var dblclick: MouseEvent
    var drag: DragEvent
    var dragend: DragEvent
    var dragenter: DragEvent
    var dragexit: Event
    var dragleave: DragEvent
    var dragover: DragEvent
    var dragstart: DragEvent
    var drop: DragEvent
    var durationchange: Event
    var emptied: Event
    var ended: Event
    var error: ErrorEvent
    var focus: FocusEvent
    var focusin: FocusEvent
    var focusout: FocusEvent
    var gotpointercapture: PointerEvent
    var input: Event
    var invalid: Event
    var keydown: KeyboardEvent
    var keypress: KeyboardEvent
    var keyup: KeyboardEvent
    var load: Event
    var loadeddata: Event
    var loadedmetadata: Event
    var loadstart: Event
    var lostpointercapture: PointerEvent
    var mousedown: MouseEvent
    var mouseenter: MouseEvent
    var mouseleave: MouseEvent
    var mousemove: MouseEvent
    var mouseout: MouseEvent
    var mouseover: MouseEvent
    var mouseup: MouseEvent
    var pause: Event
    var play: Event
    var playing: Event
    var pointercancel: PointerEvent
    var pointerdown: PointerEvent
    var pointerenter: PointerEvent
    var pointerleave: PointerEvent
    var pointermove: PointerEvent
    var pointerout: PointerEvent
    var pointerover: PointerEvent
    var pointerup: PointerEvent
    var progress: ProgressEvent__0
    var ratechange: Event
    var reset: Event
    var resize: UIEvent
    var scroll: Event
    var securitypolicyviolation: SecurityPolicyViolationEvent
    var seeked: Event
    var seeking: Event
    var select: Event
    var selectionchange: Event
    var selectstart: Event
    var stalled: Event
    var submit: Event
    var suspend: Event
    var timeupdate: Event
    var toggle: Event
    var touchcancel: TouchEvent
    var touchend: TouchEvent
    var touchmove: TouchEvent
    var touchstart: TouchEvent
    var transitioncancel: TransitionEvent
    var transitionend: TransitionEvent
    var transitionrun: TransitionEvent
    var transitionstart: TransitionEvent
    var volumechange: Event
    var waiting: Event
    var wheel: WheelEvent
}

external interface GlobalEventHandlers {
    var onbeforexrselect: ((self: GlobalEventHandlers, ev: XRSessionEvent) -> Any)?
    var onabort: ((self: GlobalEventHandlers, ev: UIEvent) -> Any)?
    var onanimationcancel: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    var onanimationend: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    var onanimationiteration: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    var onanimationstart: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    var onauxclick: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onblur: ((self: GlobalEventHandlers, ev: FocusEvent) -> Any)?
    var oncancel: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var oncanplay: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var oncanplaythrough: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onchange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onclick: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onclose: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var oncontextmenu: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var oncuechange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var ondblclick: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var ondrag: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondragend: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondragenter: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondragexit: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var ondragleave: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondragover: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondragstart: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondrop: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    var ondurationchange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onemptied: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onended: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onerror: OnErrorEventHandlerNonNull?
    var onfocus: ((self: GlobalEventHandlers, ev: FocusEvent) -> Any)?
    var ongotpointercapture: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var oninput: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var oninvalid: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onkeydown: ((self: GlobalEventHandlers, ev: KeyboardEvent) -> Any)?
    var onkeypress: ((self: GlobalEventHandlers, ev: KeyboardEvent) -> Any)?
    var onkeyup: ((self: GlobalEventHandlers, ev: KeyboardEvent) -> Any)?
    var onload: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onloadeddata: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onloadedmetadata: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onloadstart: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onlostpointercapture: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onmousedown: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onmouseenter: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onmouseleave: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onmousemove: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onmouseout: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onmouseover: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onmouseup: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    var onpause: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onplay: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onplaying: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onpointercancel: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointerdown: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointerenter: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointerleave: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointermove: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointerout: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointerover: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onpointerup: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    var onprogress: ((self: GlobalEventHandlers, ev: ProgressEvent__0) -> Any)?
    var onratechange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onreset: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onresize: ((self: GlobalEventHandlers, ev: UIEvent) -> Any)?
    var onscroll: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onsecuritypolicyviolation: ((self: GlobalEventHandlers, ev: SecurityPolicyViolationEvent) -> Any)?
    var onseeked: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onseeking: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onselect: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onselectionchange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onselectstart: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onstalled: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onsubmit: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onsuspend: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var ontimeupdate: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var ontoggle: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var ontouchcancel: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var ontouchend: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var ontouchmove: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var ontouchstart: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var ontransitioncancel: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    var ontransitionend: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    var ontransitionrun: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    var ontransitionstart: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    var onvolumechange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onwaiting: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    var onwheel: ((self: GlobalEventHandlers, ev: WheelEvent) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: GlobalEventHandlers, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: GlobalEventHandlers, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: GlobalEventHandlers, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListener)
    fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: GlobalEventHandlers, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: GlobalEventHandlers, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: GlobalEventHandlers, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListener)
    fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
}

external interface HTMLAllCollection {
    val length: Number
    fun item(nameOrIndex: String = definedExternally): dynamic /* HTMLCollection? | Element? */
    fun namedItem(name: String): dynamic /* HTMLCollection? | Element? */
    @nativeGetter
    operator fun get(index: Number): Element?
    @nativeSetter
    operator fun set(index: Number, value: Element)
}

external interface HTMLAnchorElement : HTMLElement, HTMLHyperlinkElementUtils {
    var charset: String
    var coords: String
    var download: String
    var hreflang: String
    var name: String
    var ping: String
    var referrerPolicy: String
    var rel: String
    val relList: DOMTokenList
    var rev: String
    var shape: String
    var target: String
    var text: String
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAnchorElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAnchorElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAnchorElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAnchorElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAnchorElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAnchorElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLAppletElement : HTMLElement {
    var align: String
    var alt: String
    var archive: String
    var code: String
    var codeBase: String
    val form: HTMLFormElement?
    var height: String
    var hspace: Number
    var name: String
    var `object`: String
    var vspace: Number
    var width: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAppletElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAppletElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAppletElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAppletElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAppletElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAppletElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLAreaElement : HTMLElement, HTMLHyperlinkElementUtils {
    var alt: String
    var coords: String
    var download: String
    var noHref: Boolean
    var ping: String
    var referrerPolicy: String
    var rel: String
    val relList: DOMTokenList
    var shape: String
    var target: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAreaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAreaElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLAreaElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAreaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAreaElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLAreaElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLAudioElement : HTMLMediaElement {
    fun <K : String> addEventListener(type: K, listener: (self: HTMLAudioElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLAudioElement, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLAudioElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLAudioElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLAudioElement, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLAudioElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLBRElement : HTMLElement {
    var clear: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBRElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBRElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBRElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBRElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBRElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBRElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLBaseElement : HTMLElement {
    var href: String
    var target: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBaseElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBaseElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBaseElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBaseElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBaseElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBaseElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLBaseFontElement : HTMLElement, DOML2DeprecatedColorProperty {
    var face: String
    var size: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBaseFontElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBaseFontElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLBaseFontElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBaseFontElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBaseFontElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLBaseFontElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLBodyElementEventMap : HTMLElementEventMap, WindowEventHandlersEventMap {
    var orientationchange: Event
}

external interface HTMLBodyElement : HTMLElement, WindowEventHandlers {
    var aLink: String
    var background: String
    var bgColor: String
    var link: String
    var onorientationchange: ((self: HTMLBodyElement, ev: Event) -> Any)?
    var text: String
    var vLink: String
    fun <K : String> addEventListener(type: K, listener: (self: HTMLBodyElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLBodyElement, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLBodyElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLBodyElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLBodyElement, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLBodyElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLButtonElement : HTMLElement {
    var disabled: Boolean
    val form: HTMLFormElement?
    var formAction: String
    var formEnctype: String
    var formMethod: String
    var formNoValidate: Boolean
    var formTarget: String
    val labels: NodeListOf<HTMLLabelElement>
    var name: String
    var type: String
    val validationMessage: String
    val validity: ValidityState
    var value: String
    val willValidate: Boolean
    fun checkValidity(): Boolean
    fun reportValidity(): Boolean
    fun setCustomValidity(error: String)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLButtonElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLButtonElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLButtonElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLButtonElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLButtonElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLButtonElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLCanvasElement : HTMLElement {
    var height: Number
    var width: Number
    fun getContext(contextId: String /* "2d" */, options: CanvasRenderingContext2DSettings = definedExternally): CanvasRenderingContext2D?
    fun getContext(contextId: String /* "2d" | "bitmaprenderer" | "webgl" | "webgl2" */): dynamic /* WebGL2RenderingContext | CanvasRenderingContext2D? | ImageBitmapRenderingContext? | WebGLRenderingContext? | WebGL2RenderingContext? */
    fun getContext(contextId: String /* "bitmaprenderer" */, options: ImageBitmapRenderingContextSettings = definedExternally): ImageBitmapRenderingContext?
    fun getContext(contextId: String /* "webgl" | "webgl2" */, options: WebGLContextAttributes = definedExternally): dynamic /* WebGLRenderingContext | WebGL2RenderingContext */
    fun getContext(contextId: String, options: Any = definedExternally): dynamic /* CanvasRenderingContext2D? | ImageBitmapRenderingContext? | WebGLRenderingContext? | WebGL2RenderingContext? */
    fun toBlob(callback: BlobCallback, type: String = definedExternally, quality: Any = definedExternally)
    fun toDataURL(type: String = definedExternally, quality: Any = definedExternally): String
    fun transferControlToOffscreen(): OffscreenCanvas
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLCollectionBase {
    val length: Number
    fun item(index: Number): Element?
    @nativeGetter
    operator fun get(index: Number): Element?
    @nativeSetter
    operator fun set(index: Number, value: Element)
}

external interface HTMLCollection : HTMLCollectionBase {
    fun namedItem(name: String): Element?
}

external interface HTMLCollectionOf<T : Element> : HTMLCollectionBase {
    fun item(index: Number): T?
    fun namedItem(name: String): T?
    @nativeGetter
    operator fun get(index: Number): T?
    @nativeSetter
    override operator fun set(index: Number, value: T)
}

external interface HTMLDListElement : HTMLElement {
    var compact: Boolean
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDListElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDListElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDListElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDListElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLDataElement : HTMLElement {
    var value: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDataElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDataElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDataElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDataElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDataElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDataElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLDataListElement : HTMLElement {
    val options: HTMLCollectionOf<HTMLOptionElement>
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDataListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDataListElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDataListElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDataListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDataListElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDataListElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLDetailsElement : HTMLElement {
    var open: Boolean
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDetailsElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDetailsElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDetailsElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDetailsElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDetailsElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDetailsElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLDialogElement : HTMLElement {
    var open: Boolean
    var returnValue: String
    fun close(returnValue: String = definedExternally)
    fun show()
    fun showModal()
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDialogElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDialogElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDialogElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDialogElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDialogElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDialogElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLDirectoryElement : HTMLElement {
    var compact: Boolean
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDirectoryElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDirectoryElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDirectoryElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDirectoryElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDirectoryElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDirectoryElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLDivElement : HTMLElement {
    var align: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDivElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDivElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLDivElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDivElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDivElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLDivElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLElementEventMap : ElementEventMap, GlobalEventHandlersEventMap, DocumentAndElementEventHandlersEventMap

external interface HTMLElement : Element, DocumentAndElementEventHandlers, ElementCSSInlineStyle, ElementCSSInlineStyle, ElementContentEditable, GlobalEventHandlers, HTMLOrSVGElement {
    var accessKey: String
    val accessKeyLabel: String
    var autocapitalize: String
    var dir: String
    var draggable: Boolean
    var hidden: Boolean
    var innerText: String
    var lang: String
    val offsetHeight: Number
    val offsetLeft: Number
    val offsetParent: Element?
    val offsetTop: Number
    val offsetWidth: Number
    var spellcheck: Boolean
    var title: String
    var translate: Boolean
    fun click()
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
    override val style: CSSStyleDeclaration
}

external interface HTMLEmbedElement : HTMLElement {
    var align: String
    var height: String
    var name: String
    var src: String
    var type: String
    var width: String
    fun getSVGDocument(): Document?
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLEmbedElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLEmbedElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLEmbedElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLEmbedElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLEmbedElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLEmbedElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLFieldSetElement : HTMLElement {
    var disabled: Boolean
    val elements: HTMLCollection
    val form: HTMLFormElement?
    var name: String
    val type: String
    val validationMessage: String
    val validity: ValidityState
    val willValidate: Boolean
    fun checkValidity(): Boolean
    fun reportValidity(): Boolean
    fun setCustomValidity(error: String)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFieldSetElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFieldSetElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFieldSetElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFieldSetElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFieldSetElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFieldSetElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLFontElement : HTMLElement {
    var color: String
    var face: String
    var size: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFontElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFontElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFontElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFontElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFontElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFontElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLFormControlsCollection : HTMLCollectionBase {
    fun namedItem(name: String): dynamic /* RadioNodeList? | Element? */
}

external interface HTMLFormElement : HTMLElement {
    var acceptCharset: String
    var action: String
    var autocomplete: String
    val elements: HTMLFormControlsCollection
    var encoding: String
    var enctype: String
    val length: Number
    var method: String
    var name: String
    var noValidate: Boolean
    var target: String
    fun checkValidity(): Boolean
    fun reportValidity(): Boolean
    fun reset()
    fun submit()
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFormElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFormElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFormElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFormElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFormElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFormElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
    @nativeGetter
    operator fun get(index: Number): Element?
    @nativeSetter
    operator fun set(index: Number, value: Element)
    @nativeGetter
    operator fun get(name: String): Any?
    @nativeSetter
    operator fun set(name: String, value: Any)
}

external interface HTMLFrameElement : HTMLElement {
    val contentDocument: Document?
    val contentWindow: dynamic /* typealias WindowProxy = dynamic */
        get() = definedExternally
    var frameBorder: String
    var longDesc: String
    var marginHeight: String
    var marginWidth: String
    var name: String
    var noResize: Boolean
    var scrolling: String
    var src: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFrameElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFrameElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFrameElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFrameElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFrameElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFrameElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLFrameSetElementEventMap : HTMLElementEventMap, WindowEventHandlersEventMap

external interface HTMLFrameSetElement : HTMLElement, WindowEventHandlers {
    var cols: String
    var rows: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFrameSetElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFrameSetElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLFrameSetElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFrameSetElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFrameSetElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLFrameSetElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLHRElement : HTMLElement {
    var align: String
    var color: String
    var noShade: Boolean
    var size: String
    var width: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHRElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHRElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHRElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHRElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHRElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHRElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLHeadElement : HTMLElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHeadElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHeadElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHeadElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHeadElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHeadElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHeadElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLHeadingElement : HTMLElement {
    var align: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHeadingElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHeadingElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHeadingElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHeadingElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHeadingElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHeadingElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLHtmlElement : HTMLElement {
    var version: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHtmlElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHtmlElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLHtmlElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHtmlElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHtmlElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLHtmlElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLHyperlinkElementUtils {
    var hash: String
    var host: String
    var hostname: String
    var href: String
    override fun toString(): String
    val origin: String
    var password: String
    var pathname: String
    var port: String
    var protocol: String
    var search: String
    var username: String
}

external interface HTMLIFrameElement : HTMLElement {
    var align: String
    var allow: String
    var allowFullscreen: Boolean
    var allowPaymentRequest: Boolean
    val contentDocument: Document?
    val contentWindow: dynamic /* typealias WindowProxy = dynamic */
        get() = definedExternally
    var frameBorder: String
    var height: String
    var longDesc: String
    var marginHeight: String
    var marginWidth: String
    var name: String
    var referrerPolicy: String /* "" | "no-referrer" | "no-referrer-when-downgrade" | "origin" | "origin-when-cross-origin" | "same-origin" | "strict-origin" | "strict-origin-when-cross-origin" | "unsafe-url" */
    val sandbox: DOMTokenList
    var scrolling: String
    var src: String
    var srcdoc: String
    var width: String
    fun getSVGDocument(): Document?
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLIFrameElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLIFrameElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLIFrameElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLIFrameElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLIFrameElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLIFrameElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLImageElement : HTMLElement {
    var align: String
    var alt: String
    var border: String
    val complete: Boolean
    var crossOrigin: String?
    val currentSrc: String
    var decoding: String /* "async" | "sync" | "auto" */
    var height: Number
    var hspace: Number
    var isMap: Boolean
    var longDesc: String
    var lowsrc: String
    var name: String
    val naturalHeight: Number
    val naturalWidth: Number
    var referrerPolicy: String
    var sizes: String
    var src: String
    var srcset: String
    var useMap: String
    var vspace: Number
    var width: Number
    val x: Number
    val y: Number
    fun decode(): Promise<Unit>
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLImageElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLImageElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLImageElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLImageElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLImageElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLImageElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLInputElement : HTMLElement {
    var accept: String
    var align: String
    var alt: String
    var autocomplete: String
    var checked: Boolean
    var defaultChecked: Boolean
    var defaultValue: String
    var dirName: String
    var disabled: Boolean
    var files: FileList?
    val form: HTMLFormElement?
    var formAction: String
    var formEnctype: String
    var formMethod: String
    var formNoValidate: Boolean
    var formTarget: String
    var height: Number
    var indeterminate: Boolean
    val labels: NodeListOf<HTMLLabelElement>?
    val list: HTMLElement?
    var max: String
    var maxLength: Number
    var min: String
    var minLength: Number
    var multiple: Boolean
    var name: String
    var pattern: String
    var placeholder: String
    var readOnly: Boolean
    var required: Boolean
    var selectionDirection: String /* "forward" | "backward" | "none" */
    var selectionEnd: Number?
    var selectionStart: Number?
    var size: Number
    var src: String
    var step: String
    var type: String
    var useMap: String
    val validationMessage: String
    val validity: ValidityState
    var value: String
    var valueAsDate: Date?
    var valueAsNumber: Number
    var width: Number
    val willValidate: Boolean
    fun checkValidity(): Boolean
    fun reportValidity(): Boolean
    fun select()
    fun setCustomValidity(error: String)
    fun setRangeText(replacement: String)
    fun setRangeText(replacement: String, start: Number, end: Number, selectionMode: String /* "end" | "preserve" | "select" | "start" */ = definedExternally)
    fun setRangeText(replacement: String, start: Number, end: Number)
    fun setSelectionRange(start: Number, end: Number, direction: String /* "forward" | "backward" | "none" */ = definedExternally)
    fun stepDown(n: Number = definedExternally)
    fun stepUp(n: Number = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLInputElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLInputElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLInputElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLInputElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLInputElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLInputElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLLIElement : HTMLElement {
    var type: String
    var value: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLIElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLIElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLIElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLIElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLIElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLIElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLLabelElement : HTMLElement {
    val control: HTMLElement?
    val form: HTMLFormElement?
    var htmlFor: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLabelElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLabelElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLabelElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLabelElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLabelElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLabelElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLLegendElement : HTMLElement {
    var align: String
    val form: HTMLFormElement?
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLegendElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLegendElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLegendElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLegendElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLegendElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLegendElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLLinkElement : HTMLElement, LinkStyle {
    var `as`: String
    var charset: String
    var crossOrigin: String?
    var disabled: Boolean
    var href: String
    var hreflang: String
    var imageSizes: String
    var imageSrcset: String
    var integrity: String
    var media: String
    var referrerPolicy: String
    var rel: String
    val relList: DOMTokenList
    var rev: String
    val sizes: DOMTokenList
    var target: String
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLinkElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLinkElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLLinkElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLinkElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLinkElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLLinkElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLMapElement : HTMLElement {
    val areas: HTMLCollection
    var name: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMapElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMapElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMapElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMapElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMapElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMapElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLMarqueeElementEventMap : HTMLElementEventMap {
    var bounce: Event
    var finish: Event
    var start: Event
}

external interface HTMLMarqueeElement : HTMLElement {
    var behavior: String
    var bgColor: String
    var direction: String
    var height: String
    var hspace: Number
    var loop: Number
    var onbounce: ((self: HTMLMarqueeElement, ev: Event) -> Any)?
    var onfinish: ((self: HTMLMarqueeElement, ev: Event) -> Any)?
    var onstart: ((self: HTMLMarqueeElement, ev: Event) -> Any)?
    var scrollAmount: Number
    var scrollDelay: Number
    var trueSpeed: Boolean
    var vspace: Number
    var width: String
    fun start()
    fun stop()
    fun <K : String> addEventListener(type: K, listener: (self: HTMLMarqueeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLMarqueeElement, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLMarqueeElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLMarqueeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLMarqueeElement, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLMarqueeElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLMediaElementEventMap : HTMLElementEventMap {
    var encrypted: MediaEncryptedEvent
    var waitingforkey: Event
}

external interface HTMLMediaElement : HTMLElement {
    var autoplay: Boolean
    val buffered: TimeRanges
    var controls: Boolean
    var crossOrigin: String?
    val currentSrc: String
    var currentTime: Number
    var defaultMuted: Boolean
    var defaultPlaybackRate: Number
    val duration: Number
    val ended: Boolean
    val error: MediaError?
    var loop: Boolean
    val mediaKeys: MediaKeys?
    var muted: Boolean
    val networkState: Number
    var onencrypted: ((self: HTMLMediaElement, ev: MediaEncryptedEvent) -> Any)?
    var onwaitingforkey: ((self: HTMLMediaElement, ev: Event) -> Any)?
    val paused: Boolean
    var playbackRate: Number
    val played: TimeRanges
    var preload: String
    val readyState: Number
    val seekable: TimeRanges
    val seeking: Boolean
    var src: String
    var srcObject: dynamic /* MediaStream? | MediaSource? | Blob? */
        get() = definedExternally
        set(value) = definedExternally
    val textTracks: TextTrackList
    var volume: Number
    fun addTextTrack(kind: String /* "captions" | "chapters" | "descriptions" | "metadata" | "subtitles" */, label: String = definedExternally, language: String = definedExternally): TextTrack
    fun canPlayType(type: String): String /* "" | "maybe" | "probably" */
    fun fastSeek(time: Number)
    fun load()
    fun pause()
    fun play(): Promise<Unit>
    fun setMediaKeys(mediaKeys: MediaKeys?): Promise<Unit>
    val HAVE_CURRENT_DATA: Number
    val HAVE_ENOUGH_DATA: Number
    val HAVE_FUTURE_DATA: Number
    val HAVE_METADATA: Number
    val HAVE_NOTHING: Number
    val NETWORK_EMPTY: Number
    val NETWORK_IDLE: Number
    val NETWORK_LOADING: Number
    val NETWORK_NO_SOURCE: Number
    fun <K : String> addEventListener(type: K, listener: (self: HTMLMediaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLMediaElement, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLMediaElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLMediaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLMediaElement, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLMediaElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLMenuElement : HTMLElement {
    var compact: Boolean
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMenuElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMenuElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMenuElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMenuElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMenuElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMenuElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLMetaElement : HTMLElement {
    var content: String
    var httpEquiv: String
    var name: String
    var scheme: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMetaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMetaElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMetaElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMetaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMetaElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMetaElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLMeterElement : HTMLElement {
    var high: Number
    val labels: NodeListOf<HTMLLabelElement>
    var low: Number
    var max: Number
    var min: Number
    var optimum: Number
    var value: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMeterElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMeterElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLMeterElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMeterElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMeterElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLMeterElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLModElement : HTMLElement {
    var cite: String
    var dateTime: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLModElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLModElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLModElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLModElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLModElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLModElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLOListElement : HTMLElement {
    var compact: Boolean
    var reversed: Boolean
    var start: Number
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOListElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOListElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOListElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOListElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLObjectElement : HTMLElement {
    var align: String
    var archive: String
    var border: String
    var code: String
    var codeBase: String
    var codeType: String
    val contentDocument: Document?
    val contentWindow: dynamic /* typealias WindowProxy = dynamic */
        get() = definedExternally
    var data: String
    var declare: Boolean
    val form: HTMLFormElement?
    var height: String
    var hspace: Number
    var name: String
    var standby: String
    var type: String
    var useMap: String
    val validationMessage: String
    val validity: ValidityState
    var vspace: Number
    var width: String
    val willValidate: Boolean
    fun checkValidity(): Boolean
    fun getSVGDocument(): Document?
    fun reportValidity(): Boolean
    fun setCustomValidity(error: String)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLObjectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLObjectElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLObjectElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLObjectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLObjectElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLObjectElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLOptGroupElement : HTMLElement {
    var disabled: Boolean
    val form: HTMLFormElement?
    var label: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOptGroupElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOptGroupElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOptGroupElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOptGroupElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOptGroupElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOptGroupElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLOptionElement : HTMLElement {
    var defaultSelected: Boolean
    var disabled: Boolean
    val form: HTMLFormElement?
    val index: Number
    var label: String
    var selected: Boolean
    var text: String
    var value: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOptionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOptionElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOptionElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOptionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOptionElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOptionElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLOptionsCollection : HTMLCollectionOf<HTMLOptionElement> {
    override var length: Number
    var selectedIndex: Number
    fun add(element: HTMLOptionElement, before: HTMLElement? = definedExternally)
    fun add(element: HTMLOptionElement)
    fun add(element: HTMLOptionElement, before: Number? = definedExternally)
    fun add(element: HTMLOptGroupElement, before: HTMLElement? = definedExternally)
    fun add(element: HTMLOptGroupElement)
    fun add(element: HTMLOptGroupElement, before: Number? = definedExternally)
    fun remove(index: Number)
}

external interface HTMLOrSVGElement {
    var autofocus: Boolean
    val dataset: DOMStringMap
    var nonce: String?
        get() = definedExternally
        set(value) = definedExternally
    var tabIndex: Number
    fun blur()
    fun focus(options: FocusOptions = definedExternally)
}

external interface HTMLOutputElement : HTMLElement {
    var defaultValue: String
    val form: HTMLFormElement?
    val htmlFor: DOMTokenList
    val labels: NodeListOf<HTMLLabelElement>
    var name: String
    val type: String
    val validationMessage: String
    val validity: ValidityState
    var value: String
    val willValidate: Boolean
    fun checkValidity(): Boolean
    fun reportValidity(): Boolean
    fun setCustomValidity(error: String)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOutputElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOutputElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLOutputElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOutputElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOutputElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLOutputElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLParagraphElement : HTMLElement {
    var align: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLParagraphElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLParagraphElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLParagraphElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLParagraphElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLParagraphElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLParagraphElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLParamElement : HTMLElement {
    var name: String
    var type: String
    var value: String
    var valueType: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLParamElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLParamElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLParamElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLParamElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLParamElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLParamElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLPictureElement : HTMLElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLPictureElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLPictureElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLPictureElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLPictureElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLPictureElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLPictureElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLPreElement : HTMLElement {
    var width: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLPreElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLPreElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLPreElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLPreElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLPreElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLPreElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLProgressElement : HTMLElement {
    val labels: NodeListOf<HTMLLabelElement>
    var max: Number
    val position: Number
    var value: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLProgressElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLProgressElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLProgressElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLProgressElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLProgressElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLProgressElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLQuoteElement : HTMLElement {
    var cite: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLQuoteElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLQuoteElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLQuoteElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLQuoteElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLQuoteElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLQuoteElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLScriptElement : HTMLElement {
    var async: Boolean
    var charset: String
    var crossOrigin: String?
    var defer: Boolean
    var event: String
    var htmlFor: String
    var integrity: String
    var noModule: Boolean
    var referrerPolicy: String
    var src: String
    var text: String
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLScriptElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLScriptElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLScriptElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLScriptElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLScriptElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLScriptElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLSelectElement : HTMLElement {
    var autocomplete: String
    var disabled: Boolean
    val form: HTMLFormElement?
    val labels: NodeListOf<HTMLLabelElement>
    var length: Number
    var multiple: Boolean
    var name: String
    val options: HTMLOptionsCollection
    var required: Boolean
    var selectedIndex: Number
    val selectedOptions: HTMLCollectionOf<HTMLOptionElement>
    var size: Number
    val type: String
    val validationMessage: String
    val validity: ValidityState
    var value: String
    val willValidate: Boolean
    fun add(element: HTMLOptionElement, before: HTMLElement? = definedExternally)
    fun add(element: HTMLOptionElement)
    fun add(element: HTMLOptionElement, before: Number? = definedExternally)
    fun add(element: HTMLOptGroupElement, before: HTMLElement? = definedExternally)
    fun add(element: HTMLOptGroupElement)
    fun add(element: HTMLOptGroupElement, before: Number? = definedExternally)
    fun checkValidity(): Boolean
    fun item(index: Number): Element?
    fun namedItem(name: String): HTMLOptionElement?
    override fun remove()
    fun remove(index: Number)
    fun reportValidity(): Boolean
    fun setCustomValidity(error: String)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSelectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSelectElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSelectElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSelectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSelectElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSelectElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
    @nativeGetter
    operator fun get(name: Number): dynamic /* HTMLOptionElement? | HTMLOptGroupElement? */
    @nativeSetter
    operator fun set(name: Number, value: HTMLOptionElement)
    @nativeSetter
    operator fun set(name: Number, value: HTMLOptGroupElement)
}

external interface HTMLSlotElement : HTMLElement {
    var name: String
    fun assignedElements(options: AssignedNodesOptions = definedExternally): Array<Element>
    fun assignedNodes(options: AssignedNodesOptions = definedExternally): Array<Node>
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSlotElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSlotElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSlotElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSlotElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSlotElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSlotElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLSourceElement : HTMLElement {
    var media: String
    var sizes: String
    var src: String
    var srcset: String
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSourceElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSourceElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSourceElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSourceElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSourceElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSourceElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLSpanElement : HTMLElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSpanElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSpanElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLSpanElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSpanElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSpanElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLSpanElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLStyleElement : HTMLElement, LinkStyle {
    var media: String
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLStyleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLStyleElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLStyleElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLStyleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLStyleElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLStyleElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableCaptionElement : HTMLElement {
    var align: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableCaptionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableCaptionElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableCaptionElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableCaptionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableCaptionElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableCaptionElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableCellElement : HTMLElement {
    var abbr: String
    var align: String
    var axis: String
    var bgColor: String
    val cellIndex: Number
    var ch: String
    var chOff: String
    var colSpan: Number
    var headers: String
    var height: String
    var noWrap: Boolean
    var rowSpan: Number
    var scope: String
    var vAlign: String
    var width: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableCellElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableCellElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableCellElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableCellElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableCellElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableCellElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableColElement : HTMLElement {
    var align: String
    var ch: String
    var chOff: String
    var span: Number
    var vAlign: String
    var width: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableColElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableColElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableColElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableColElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableColElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableColElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableDataCellElement : HTMLTableCellElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableDataCellElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableDataCellElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableDataCellElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableDataCellElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableDataCellElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableDataCellElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableElement : HTMLElement {
    var align: String
    var bgColor: String
    var border: String
    var caption: HTMLTableCaptionElement?
    var cellPadding: String
    var cellSpacing: String
    var frame: String
    val rows: HTMLCollectionOf<HTMLTableRowElement>
    var rules: String
    var summary: String
    val tBodies: HTMLCollectionOf<HTMLTableSectionElement>
    var tFoot: HTMLTableSectionElement?
    var tHead: HTMLTableSectionElement?
    var width: String
    fun createCaption(): HTMLTableCaptionElement
    fun createTBody(): HTMLTableSectionElement
    fun createTFoot(): HTMLTableSectionElement
    fun createTHead(): HTMLTableSectionElement
    fun deleteCaption()
    fun deleteRow(index: Number)
    fun deleteTFoot()
    fun deleteTHead()
    fun insertRow(index: Number = definedExternally): HTMLTableRowElement
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableHeaderCellElement : HTMLTableCellElement {
    override var scope: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableHeaderCellElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableHeaderCellElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableHeaderCellElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableHeaderCellElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableHeaderCellElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableHeaderCellElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableRowElement : HTMLElement {
    var align: String
    var bgColor: String
    val cells: HTMLCollectionOf<dynamic /* HTMLTableDataCellElement | HTMLTableHeaderCellElement */>
    var ch: String
    var chOff: String
    val rowIndex: Number
    val sectionRowIndex: Number
    var vAlign: String
    fun deleteCell(index: Number)
    fun insertCell(index: Number = definedExternally): HTMLTableDataCellElement
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableRowElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableRowElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableRowElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableRowElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableRowElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableRowElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTableSectionElement : HTMLElement {
    var align: String
    var ch: String
    var chOff: String
    val rows: HTMLCollectionOf<HTMLTableRowElement>
    var vAlign: String
    fun deleteRow(index: Number)
    fun insertRow(index: Number = definedExternally): HTMLTableRowElement
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableSectionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableSectionElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTableSectionElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableSectionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableSectionElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTableSectionElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTemplateElement : HTMLElement {
    val content: DocumentFragment
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTemplateElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTemplateElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTemplateElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTemplateElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTemplateElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTemplateElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTextAreaElement : HTMLElement {
    var autocomplete: String
    var cols: Number
    var defaultValue: String
    var dirName: String
    var disabled: Boolean
    val form: HTMLFormElement?
    val labels: NodeListOf<HTMLLabelElement>
    var maxLength: Number
    var minLength: Number
    var name: String
    var placeholder: String
    var readOnly: Boolean
    var required: Boolean
    var rows: Number
    var selectionDirection: String /* "forward" | "backward" | "none" */
    var selectionEnd: Number
    var selectionStart: Number
    val textLength: Number
    val type: String
    val validationMessage: String
    val validity: ValidityState
    var value: String
    val willValidate: Boolean
    var wrap: String
    fun checkValidity(): Boolean
    fun reportValidity(): Boolean
    fun select()
    fun setCustomValidity(error: String)
    fun setRangeText(replacement: String)
    fun setRangeText(replacement: String, start: Number, end: Number, selectionMode: String /* "end" | "preserve" | "select" | "start" */ = definedExternally)
    fun setRangeText(replacement: String, start: Number, end: Number)
    fun setSelectionRange(start: Number, end: Number, direction: String /* "forward" | "backward" | "none" */ = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTextAreaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTextAreaElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTextAreaElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTextAreaElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTextAreaElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTextAreaElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTimeElement : HTMLElement {
    var dateTime: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTimeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTimeElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTimeElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTimeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTimeElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTimeElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTitleElement : HTMLElement {
    var text: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTitleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTitleElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTitleElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTitleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTitleElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTitleElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLTrackElement : HTMLElement {
    var default: Boolean
    var kind: String
    var label: String
    val readyState: Number
    var src: String
    var srclang: String
    val track: TextTrack
    val ERROR: Number
    val LOADED: Number
    val LOADING: Number
    val NONE: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTrackElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTrackElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLTrackElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTrackElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTrackElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLTrackElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLUListElement : HTMLElement {
    var compact: Boolean
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLUListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLUListElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLUListElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLUListElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLUListElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLUListElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HTMLVideoElement : HTMLMediaElement {
    var height: Number
    var poster: String
    val videoHeight: Number
    val videoWidth: Number
    var width: Number
    fun getVideoPlaybackQuality(): VideoPlaybackQuality
    fun <K : String> addEventListener(type: K, listener: (self: HTMLVideoElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLVideoElement, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: HTMLVideoElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLVideoElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLVideoElement, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: HTMLVideoElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface HashChangeEvent : Event {
    val newURL: String
    val oldURL: String
}

external interface Headers {
    fun entries(): IterableIterator<dynamic /* JsTuple<String, String> */>
    fun keys(): IterableIterator<String>
    fun values(): IterableIterator<String>
    fun append(name: String, value: String)
    fun delete(name: String)
    fun get(name: String): String?
    fun has(name: String): Boolean
    fun set(name: String, value: String)
    fun forEach(callbackfn: (value: String, key: String, parent: Headers) -> Unit, thisArg: Any = definedExternally)
}

external interface History {
    val length: Number
    var scrollRestoration: String /* "auto" | "manual" */
    val state: Any
    fun back()
    fun forward()
    fun go(delta: Number = definedExternally)
    fun pushState(data: Any, title: String, url: String? = definedExternally)
    fun replaceState(data: Any, title: String, url: String? = definedExternally)
}

external interface HkdfCtrParams : Algorithm {
    var context: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
    var hash: dynamic /* String | Algorithm */
        get() = definedExternally
        set(value) = definedExternally
    var label: dynamic /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */
        get() = definedExternally
        set(value) = definedExternally
}

external interface IDBArrayKey : Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>

external interface IDBCursor {
    val direction: String /* "next" | "nextunique" | "prev" | "prevunique" */
    val key: dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */
        get() = definedExternally
    val primaryKey: dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */
        get() = definedExternally
    val source: dynamic /* IDBObjectStore | IDBIndex */
        get() = definedExternally
    fun advance(count: Number)
    fun `continue`(key: Number = definedExternally)
    fun `continue`()
    fun `continue`(key: String = definedExternally)
    fun `continue`(key: Date = definedExternally)
    fun `continue`(key: ArrayBufferView = definedExternally)
    fun `continue`(key: ArrayBuffer = definedExternally)
    fun `continue`(key: IDBArrayKey = definedExternally)
    fun continuePrimaryKey(key: Number, primaryKey: Any /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */)
    fun continuePrimaryKey(key: String, primaryKey: Any /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */)
    fun continuePrimaryKey(key: Date, primaryKey: Any /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */)
    fun continuePrimaryKey(key: ArrayBufferView, primaryKey: Any /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */)
    fun continuePrimaryKey(key: ArrayBuffer, primaryKey: Any /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */)
    fun continuePrimaryKey(key: IDBArrayKey, primaryKey: Any /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */)
    fun delete(): IDBRequest<Nothing?>
    fun update(value: Any): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
}

external interface IDBCursorWithValue : IDBCursor {
    val value: Any
}

external interface IDBDatabaseEventMap {
    var abort: Event
    var close: Event
    var error: Event
    var versionchange: IDBVersionChangeEvent
}

external interface IDBDatabase : EventTarget {
    val name: String
    val objectStoreNames: DOMStringList
    var onabort: ((self: IDBDatabase, ev: Event) -> Any)?
    var onclose: ((self: IDBDatabase, ev: Event) -> Any)?
    var onerror: ((self: IDBDatabase, ev: Event) -> Any)?
    var onversionchange: ((self: IDBDatabase, ev: IDBVersionChangeEvent) -> Any)?
    val version: Number
    fun close()
    fun createObjectStore(name: String, optionalParameters: IDBObjectStoreParameters = definedExternally): IDBObjectStore
    fun deleteObjectStore(name: String)
    fun transaction(storeNames: String, mode: String /* "readonly" | "readwrite" | "versionchange" */ = definedExternally): IDBTransaction
    fun transaction(storeNames: String): IDBTransaction
    fun transaction(storeNames: Array<String>, mode: String /* "readonly" | "readwrite" | "versionchange" */ = definedExternally): IDBTransaction
    fun transaction(storeNames: Array<String>): IDBTransaction
    fun <K : String> addEventListener(type: K, listener: (self: IDBDatabase, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: IDBDatabase, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: IDBDatabase, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBDatabase, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBDatabase, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBDatabase, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface IDBFactory {
    fun cmp(first: Any, second: Any): Number
    fun deleteDatabase(name: String): IDBOpenDBRequest
    fun open(name: String, version: Number = definedExternally): IDBOpenDBRequest
}

external interface IDBIndex {
    val keyPath: dynamic /* String | Array<String> */
        get() = definedExternally
    val multiEntry: Boolean
    var name: String
    val objectStore: IDBObjectStore
    val unique: Boolean
    fun count(key: Number = definedExternally): IDBRequest<Number>
    fun count(): IDBRequest<Number>
    fun count(key: String = definedExternally): IDBRequest<Number>
    fun count(key: Date = definedExternally): IDBRequest<Number>
    fun count(key: ArrayBufferView = definedExternally): IDBRequest<Number>
    fun count(key: ArrayBuffer = definedExternally): IDBRequest<Number>
    fun count(key: IDBArrayKey = definedExternally): IDBRequest<Number>
    fun count(key: IDBKeyRange = definedExternally): IDBRequest<Number>
    fun get(key: Number): IDBRequest<Any?>
    fun get(key: String): IDBRequest<Any?>
    fun get(key: Date): IDBRequest<Any?>
    fun get(key: ArrayBufferView): IDBRequest<Any?>
    fun get(key: ArrayBuffer): IDBRequest<Any?>
    fun get(key: IDBArrayKey): IDBRequest<Any?>
    fun get(key: IDBKeyRange): IDBRequest<Any?>
    fun getAll(query: Number? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(): IDBRequest<Array<Any>>
    fun getAll(query: Number? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: String? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: String? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: Date? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: Date? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBufferView? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBufferView? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBuffer? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBuffer? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBArrayKey? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBArrayKey? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBKeyRange? = definedExternally): IDBRequest<Array<Any>>
    fun getAllKeys(query: Number? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: Number? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: String? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: String? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: Date? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: Date? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBufferView? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBufferView? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBuffer? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBuffer? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBArrayKey? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBArrayKey? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBKeyRange? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getKey(key: Number): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(key: String): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(key: Date): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(key: ArrayBufferView): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(key: ArrayBuffer): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(key: IDBArrayKey): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(key: IDBKeyRange): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun openCursor(query: Number? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: Number? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: String? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: String? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: Date? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: Date? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBufferView? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBufferView? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBuffer? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBuffer? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBArrayKey? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBArrayKey? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKeyRange? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKeyRange? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openKeyCursor(query: Number? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: Number? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: String? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: String? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: Date? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: Date? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBufferView? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBufferView? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBuffer? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBuffer? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBArrayKey? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBArrayKey? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKeyRange? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKeyRange? = definedExternally): IDBRequest<IDBCursor?>
}

external interface IDBKeyRange {
    val lower: Any
    val lowerOpen: Boolean
    val upper: Any
    val upperOpen: Boolean
    fun includes(key: Any): Boolean
}

external interface IDBObjectStore {
    fun createIndex(name: String, keyPath: String, options: IDBIndexParameters = definedExternally): IDBIndex
    fun createIndex(name: String, keyPath: String): IDBIndex
    fun createIndex(name: String, keyPath: Iterable<String>, options: IDBIndexParameters = definedExternally): IDBIndex
    fun createIndex(name: String, keyPath: Iterable<String>): IDBIndex
    val autoIncrement: Boolean
    val indexNames: DOMStringList
    val keyPath: dynamic /* String | Array<String> */
        get() = definedExternally
    var name: String
    val transaction: IDBTransaction
    fun add(value: Any, key: Number = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun add(value: Any): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun add(value: Any, key: String = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun add(value: Any, key: Date = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun add(value: Any, key: ArrayBufferView = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun add(value: Any, key: ArrayBuffer = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun add(value: Any, key: IDBArrayKey = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun clear(): IDBRequest<Nothing?>
    fun count(key: Number = definedExternally): IDBRequest<Number>
    fun count(): IDBRequest<Number>
    fun count(key: String = definedExternally): IDBRequest<Number>
    fun count(key: Date = definedExternally): IDBRequest<Number>
    fun count(key: ArrayBufferView = definedExternally): IDBRequest<Number>
    fun count(key: ArrayBuffer = definedExternally): IDBRequest<Number>
    fun count(key: IDBArrayKey = definedExternally): IDBRequest<Number>
    fun count(key: IDBKeyRange = definedExternally): IDBRequest<Number>
    fun createIndex(name: String, keyPath: Array<String>, options: IDBIndexParameters = definedExternally): IDBIndex
    fun createIndex(name: String, keyPath: Array<String>): IDBIndex
    fun delete(key: Number): IDBRequest<Nothing?>
    fun delete(key: String): IDBRequest<Nothing?>
    fun delete(key: Date): IDBRequest<Nothing?>
    fun delete(key: ArrayBufferView): IDBRequest<Nothing?>
    fun delete(key: ArrayBuffer): IDBRequest<Nothing?>
    fun delete(key: IDBArrayKey): IDBRequest<Nothing?>
    fun delete(key: IDBKeyRange): IDBRequest<Nothing?>
    fun deleteIndex(name: String)
    fun get(query: Number): IDBRequest<Any?>
    fun get(query: String): IDBRequest<Any?>
    fun get(query: Date): IDBRequest<Any?>
    fun get(query: ArrayBufferView): IDBRequest<Any?>
    fun get(query: ArrayBuffer): IDBRequest<Any?>
    fun get(query: IDBArrayKey): IDBRequest<Any?>
    fun get(query: IDBKeyRange): IDBRequest<Any?>
    fun getAll(query: Number? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(): IDBRequest<Array<Any>>
    fun getAll(query: Number? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: String? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: String? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: Date? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: Date? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBufferView? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBufferView? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBuffer? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: ArrayBuffer? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBArrayKey? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBArrayKey? = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): IDBRequest<Array<Any>>
    fun getAll(query: IDBKeyRange? = definedExternally): IDBRequest<Array<Any>>
    fun getAllKeys(query: Number? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: Number? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: String? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: String? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: Date? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: Date? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBufferView? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBufferView? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBuffer? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: ArrayBuffer? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBArrayKey? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBArrayKey? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getAllKeys(query: IDBKeyRange? = definedExternally): IDBRequest<Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>>
    fun getKey(query: Number): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(query: String): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(query: Date): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(query: ArrayBufferView): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(query: ArrayBuffer): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(query: IDBArrayKey): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun getKey(query: IDBKeyRange): IDBRequest<dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */>
    fun index(name: String): IDBIndex
    fun openCursor(query: Number? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: Number? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: String? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: String? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: Date? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: Date? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBufferView? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBufferView? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBuffer? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: ArrayBuffer? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBArrayKey? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBArrayKey? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKeyRange? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKeyRange? = definedExternally): IDBRequest<IDBCursorWithValue?>
    fun openKeyCursor(query: Number? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: Number? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: String? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: String? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: Date? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: Date? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBufferView? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBufferView? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBuffer? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: ArrayBuffer? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBArrayKey? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBArrayKey? = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKeyRange? = definedExternally, direction: String /* "next" | "nextunique" | "prev" | "prevunique" */ = definedExternally): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKeyRange? = definedExternally): IDBRequest<IDBCursor?>
    fun put(value: Any, key: Number = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun put(value: Any): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun put(value: Any, key: String = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun put(value: Any, key: Date = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun put(value: Any, key: ArrayBufferView = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun put(value: Any, key: ArrayBuffer = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
    fun put(value: Any, key: IDBArrayKey = definedExternally): IDBRequest<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>
}

external interface IDBOpenDBRequestEventMap : IDBRequestEventMap {
    var blocked: Event
    var upgradeneeded: IDBVersionChangeEvent
}

external interface IDBOpenDBRequest : IDBRequest<IDBDatabase> {
    var onblocked: ((self: IDBOpenDBRequest, ev: Event) -> Any)?
    var onupgradeneeded: ((self: IDBOpenDBRequest, ev: IDBVersionChangeEvent) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: IDBOpenDBRequest, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: IDBOpenDBRequest, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: IDBOpenDBRequest, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBOpenDBRequest, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBOpenDBRequest, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBOpenDBRequest, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface IDBRequestEventMap {
    var error: Event
    var success: Event
}

external interface IDBRequest<T> : EventTarget {
    val error: DOMException?
    var onerror: ((self: IDBRequest<T>, ev: Event) -> Any)?
    var onsuccess: ((self: IDBRequest<T>, ev: Event) -> Any)?
    val readyState: String /* "done" | "pending" */
    val result: T
    val source: dynamic /* IDBObjectStore | IDBIndex | IDBCursor */
        get() = definedExternally
    val transaction: IDBTransaction?
    fun <K : String> addEventListener(type: K, listener: (self: IDBRequest<T>, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: IDBRequest<T>, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: IDBRequest<T>, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBRequest<T>, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBRequest<T>, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBRequest<T>, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface IDBTransactionEventMap {
    var abort: Event
    var complete: Event
    var error: Event
}

external interface IDBTransaction : EventTarget {
    val db: IDBDatabase
    val error: DOMException
    val mode: String /* "readonly" | "readwrite" | "versionchange" */
    val objectStoreNames: DOMStringList
    var onabort: ((self: IDBTransaction, ev: Event) -> Any)?
    var oncomplete: ((self: IDBTransaction, ev: Event) -> Any)?
    var onerror: ((self: IDBTransaction, ev: Event) -> Any)?
    fun abort()
    fun objectStore(name: String): IDBObjectStore
    fun <K : String> addEventListener(type: K, listener: (self: IDBTransaction, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: IDBTransaction, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: IDBTransaction, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBTransaction, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBTransaction, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: IDBTransaction, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface IDBVersionChangeEvent : Event {
    val newVersion: Number?
    val oldVersion: Number
}

external interface IIRFilterNode : AudioNode {
    fun getFrequencyResponse(frequencyHz: Float32Array, magResponse: Float32Array, phaseResponse: Float32Array)
}

external interface ImageBitmap {
    val height: Number
    val width: Number
    fun close()
}

external interface ImageBitmapRenderingContext {
    val canvas: dynamic /* HTMLCanvasElement | OffscreenCanvas */
        get() = definedExternally
    fun transferFromImageBitmap(bitmap: ImageBitmap?)
}

external interface ImageData {
    val data: Uint8ClampedArray
    val height: Number
    val width: Number
}

external interface InnerHTML {
    var innerHTML: String
}

external interface InputEvent : UIEvent {
    val data: String?
    val inputType: String
    val isComposing: Boolean
}

external interface KeyboardEvent : UIEvent {
    val altKey: Boolean
    var char: String
    val charCode: Number
    val code: String
    val ctrlKey: Boolean
    val isComposing: Boolean
    val key: String
    val keyCode: Number
    val location: Number
    val metaKey: Boolean
    val repeat: Boolean
    val shiftKey: Boolean
    fun getModifierState(keyArg: String): Boolean
    val DOM_KEY_LOCATION_LEFT: Number
    val DOM_KEY_LOCATION_NUMPAD: Number
    val DOM_KEY_LOCATION_RIGHT: Number
    val DOM_KEY_LOCATION_STANDARD: Number
}

external interface LinkStyle {
    val sheet: CSSStyleSheet?
}

external interface ListeningStateChangedEvent : Event {
    val label: String
    val state: String /* "active" | "disambiguation" | "inactive" */
}

external interface Location {
    val ancestorOrigins: DOMStringList
    var hash: String
    var host: String
    var hostname: String
    var href: String
    override fun toString(): String
    val origin: String
    var pathname: String
    var port: String
    var protocol: String
    var search: String
    fun assign(url: String)
    fun reload()
    fun reload(forcedReload: Boolean)
    fun replace(url: String)
}

external interface MSFileSaver {
    fun msSaveBlob(blob: Any, defaultName: String = definedExternally): Boolean
    fun msSaveOrOpenBlob(blob: Any, defaultName: String = definedExternally): Boolean
}

external interface MSGestureEvent : UIEvent {
    val clientX: Number
    val clientY: Number
    val expansion: Number
    val gestureObject: Any
    val hwTimestamp: Number
    val offsetX: Number
    val offsetY: Number
    val rotation: Number
    val scale: Number
    val screenX: Number
    val screenY: Number
    val translationX: Number
    val translationY: Number
    val velocityAngular: Number
    val velocityExpansion: Number
    val velocityX: Number
    val velocityY: Number
    fun initGestureEvent(typeArg: String, canBubbleArg: Boolean, cancelableArg: Boolean, viewArg: Window, detailArg: Number, screenXArg: Number, screenYArg: Number, clientXArg: Number, clientYArg: Number, offsetXArg: Number, offsetYArg: Number, translationXArg: Number, translationYArg: Number, scaleArg: Number, expansionArg: Number, rotationArg: Number, velocityXArg: Number, velocityYArg: Number, velocityExpansionArg: Number, velocityAngularArg: Number, hwTimestampArg: Number)
    val MSGESTURE_FLAG_BEGIN: Number
    val MSGESTURE_FLAG_CANCEL: Number
    val MSGESTURE_FLAG_END: Number
    val MSGESTURE_FLAG_INERTIA: Number
    val MSGESTURE_FLAG_NONE: Number
}

external interface MSMediaKeyMessageEvent : Event {
    val destinationURL: String?
    val message: Uint8Array
}

external interface MSMediaKeyNeededEvent : Event {
    val initData: Uint8Array?
}

external interface MSNavigatorDoNotTrack {
    fun confirmSiteSpecificTrackingException(args: ConfirmSiteSpecificExceptionsInformation): Boolean
    fun confirmWebWideTrackingException(args: ExceptionInformation): Boolean
    fun removeSiteSpecificTrackingException(args: ExceptionInformation)
    fun removeWebWideTrackingException(args: ExceptionInformation)
    fun storeSiteSpecificTrackingException(args: StoreSiteSpecificExceptionsInformation)
    fun storeWebWideTrackingException(args: StoreExceptionsInformation)
}

external interface MSPointerEvent : MouseEvent {
    val currentPoint: Any
    val height: Number
    val hwTimestamp: Number
    val intermediatePoints: Any
    val isPrimary: Boolean
    val pointerId: Number
    val pointerType: Any
    val pressure: Number
    val rotation: Number
    val tiltX: Number
    val tiltY: Number
    val width: Number
    fun getCurrentPoint(element: Element)
    fun getIntermediatePoints(element: Element)
    fun initPointerEvent(typeArg: String, canBubbleArg: Boolean, cancelableArg: Boolean, viewArg: Window, detailArg: Number, screenXArg: Number, screenYArg: Number, clientXArg: Number, clientYArg: Number, ctrlKeyArg: Boolean, altKeyArg: Boolean, shiftKeyArg: Boolean, metaKeyArg: Boolean, buttonArg: Number, relatedTargetArg: EventTarget, offsetXArg: Number, offsetYArg: Number, widthArg: Number, heightArg: Number, pressure: Number, rotation: Number, tiltX: Number, tiltY: Number, pointerIdArg: Number, pointerType: Any, hwTimestampArg: Number, isPrimary: Boolean)
}

external interface MediaDeviceInfo {
    val deviceId: String
    val groupId: String
    val kind: String /* "audioinput" | "audiooutput" | "videoinput" */
    val label: String
    fun toJSON(): Any
}

external interface MediaDevicesEventMap {
    var devicechange: Event
}

external interface MediaDevices : EventTarget {
    var ondevicechange: ((self: MediaDevices, ev: Event) -> Any)?
    fun enumerateDevices(): Promise<Array<MediaDeviceInfo>>
    fun getSupportedConstraints(): MediaTrackSupportedConstraints
    fun getUserMedia(constraints: MediaStreamConstraints = definedExternally): Promise<MediaStream>
    fun <K : String> addEventListener(type: K, listener: (self: MediaDevices, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MediaDevices, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MediaDevices, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaDevices, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaDevices, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaDevices, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MediaElementAudioSourceNode : AudioNode {
    val mediaElement: HTMLMediaElement
}

external interface MediaEncryptedEvent : Event {
    val initData: ArrayBuffer?
    val initDataType: String
}

external interface MediaError {
    val code: Number
    val message: String
    val MEDIA_ERR_ABORTED: Number
    val MEDIA_ERR_DECODE: Number
    val MEDIA_ERR_NETWORK: Number
    val MEDIA_ERR_SRC_NOT_SUPPORTED: Number
}

external interface MediaKeyMessageEvent : Event {
    val message: ArrayBuffer
    val messageType: String /* "individualization-request" | "license-release" | "license-renewal" | "license-request" */
}

external interface MediaKeySessionEventMap {
    var keystatuseschange: Event
    var message: MediaKeyMessageEvent
}

external interface MediaKeySession : EventTarget {
    val closed: Promise<Unit>
    val expiration: Number
    val keyStatuses: MediaKeyStatusMap
    var onkeystatuseschange: ((self: MediaKeySession, ev: Event) -> Any)?
    var onmessage: ((self: MediaKeySession, ev: MediaKeyMessageEvent) -> Any)?
    val sessionId: String
    fun close(): Promise<Unit>
    fun generateRequest(initDataType: String, initData: ArrayBufferView): Promise<Unit>
    fun generateRequest(initDataType: String, initData: ArrayBuffer): Promise<Unit>
    fun load(sessionId: String): Promise<Boolean>
    fun remove(): Promise<Unit>
    fun update(response: ArrayBufferView): Promise<Unit>
    fun update(response: ArrayBuffer): Promise<Unit>
    fun <K : String> addEventListener(type: K, listener: (self: MediaKeySession, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MediaKeySession, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MediaKeySession, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaKeySession, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaKeySession, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaKeySession, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MediaKeyStatusMap {
    fun entries(): IterableIterator<dynamic /* JsTuple<dynamic, String> */>
    fun keys(): IterableIterator<dynamic /* ArrayBufferView | ArrayBuffer */>
    fun values(): IterableIterator<String /* "expired" | "internal-error" | "output-downscaled" | "output-restricted" | "released" | "status-pending" | "usable" */>
    val size: Number
    fun get(keyId: ArrayBufferView): String /* "expired" | "internal-error" | "output-downscaled" | "output-restricted" | "released" | "status-pending" | "usable" */
    fun get(keyId: ArrayBuffer): String /* "expired" | "internal-error" | "output-downscaled" | "output-restricted" | "released" | "status-pending" | "usable" */
    fun has(keyId: ArrayBufferView): Boolean
    fun has(keyId: ArrayBuffer): Boolean
    fun forEach(callbackfn: (value: String /* "expired" | "internal-error" | "output-downscaled" | "output-restricted" | "released" | "status-pending" | "usable" */, key: Any /* ArrayBufferView | ArrayBuffer */, parent: MediaKeyStatusMap) -> Unit, thisArg: Any = definedExternally)
}

external interface MediaKeySystemAccess {
    val keySystem: String
    fun createMediaKeys(): Promise<MediaKeys>
    fun getConfiguration(): MediaKeySystemConfiguration
}

external interface MediaKeys {
    fun createSession(sessionType: String /* "persistent-license" | "temporary" */ = definedExternally): MediaKeySession
    fun setServerCertificate(serverCertificate: ArrayBufferView): Promise<Boolean>
    fun setServerCertificate(serverCertificate: ArrayBuffer): Promise<Boolean>
}

external interface MediaList {
    val length: Number
    var mediaText: String
    override fun toString(): String
    fun appendMedium(medium: String)
    fun deleteMedium(medium: String)
    fun item(index: Number): String?
    @nativeGetter
    operator fun get(index: Number): String?
    @nativeSetter
    operator fun set(index: Number, value: String)
}

external interface MediaQueryListEventMap {
    var change: MediaQueryListEvent
}

external interface MediaQueryList : EventTarget {
    val matches: Boolean
    val media: String
    var onchange: ((self: MediaQueryList, ev: MediaQueryListEvent) -> Any)?
    fun addListener(listener: ((self: MediaQueryList, ev: MediaQueryListEvent) -> Any)?)
    fun removeListener(listener: ((self: MediaQueryList, ev: MediaQueryListEvent) -> Any)?)
    fun <K : String> addEventListener(type: K, listener: (self: MediaQueryList, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MediaQueryList, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MediaQueryList, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaQueryList, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaQueryList, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaQueryList, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MediaQueryListEvent : Event {
    val matches: Boolean
    val media: String
}

external interface MediaSourceEventMap {
    var sourceclose: Event
    var sourceended: Event
    var sourceopen: Event
}

external interface MediaSource : EventTarget {
    val activeSourceBuffers: SourceBufferList
    var duration: Number
    var onsourceclose: ((self: MediaSource, ev: Event) -> Any)?
    var onsourceended: ((self: MediaSource, ev: Event) -> Any)?
    var onsourceopen: ((self: MediaSource, ev: Event) -> Any)?
    val readyState: String /* "closed" | "ended" | "open" */
    val sourceBuffers: SourceBufferList
    fun addSourceBuffer(type: String): SourceBuffer
    fun clearLiveSeekableRange()
    fun endOfStream(error: String /* "decode" | "network" */ = definedExternally)
    fun removeSourceBuffer(sourceBuffer: SourceBuffer)
    fun setLiveSeekableRange(start: Number, end: Number)
    fun <K : String> addEventListener(type: K, listener: (self: MediaSource, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MediaSource, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MediaSource, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaSource, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaSource, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaSource, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MediaStreamEventMap {
    var addtrack: MediaStreamTrackEvent
    var removetrack: MediaStreamTrackEvent
}

external interface MediaStream : EventTarget {
    val active: Boolean
    val id: String
    var onaddtrack: ((self: MediaStream, ev: MediaStreamTrackEvent) -> Any)?
    var onremovetrack: ((self: MediaStream, ev: MediaStreamTrackEvent) -> Any)?
    fun addTrack(track: MediaStreamTrack)
    fun clone(): MediaStream
    fun getAudioTracks(): Array<MediaStreamTrack>
    fun getTrackById(trackId: String): MediaStreamTrack?
    fun getTracks(): Array<MediaStreamTrack>
    fun getVideoTracks(): Array<MediaStreamTrack>
    fun removeTrack(track: MediaStreamTrack)
    fun <K : String> addEventListener(type: K, listener: (self: MediaStream, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MediaStream, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MediaStream, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaStream, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaStream, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaStream, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MediaStreamAudioDestinationNode : AudioNode {
    val stream: MediaStream
}

external interface MediaStreamAudioSourceNode : AudioNode {
    val mediaStream: MediaStream
}

external interface MediaStreamError {
    val constraintName: String?
    val message: String?
    val name: String
}

external interface MediaStreamErrorEvent : Event {
    val error: MediaStreamError?
}

external interface MediaStreamEvent : Event {
    val stream: MediaStream?
}

external interface MediaStreamTrackEventMap {
    var ended: Event
    var isolationchange: Event
    var mute: Event
    var unmute: Event
}

external interface MediaStreamTrack : EventTarget {
    var enabled: Boolean
    val id: String
    val isolated: Boolean
    val kind: String
    val label: String
    val muted: Boolean
    var onended: ((self: MediaStreamTrack, ev: Event) -> Any)?
    var onisolationchange: ((self: MediaStreamTrack, ev: Event) -> Any)?
    var onmute: ((self: MediaStreamTrack, ev: Event) -> Any)?
    var onunmute: ((self: MediaStreamTrack, ev: Event) -> Any)?
    val readyState: String /* "ended" | "live" */
    fun applyConstraints(constraints: MediaTrackConstraints = definedExternally): Promise<Unit>
    fun clone(): MediaStreamTrack
    fun getCapabilities(): MediaTrackCapabilities
    fun getConstraints(): MediaTrackConstraints
    fun getSettings(): MediaTrackSettings
    fun stop()
    fun <K : String> addEventListener(type: K, listener: (self: MediaStreamTrack, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MediaStreamTrack, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MediaStreamTrack, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaStreamTrack, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaStreamTrack, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MediaStreamTrack, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MediaStreamTrackAudioSourceNode : AudioNode

external interface MediaStreamTrackEvent : Event {
    val track: MediaStreamTrack
}

external interface MessageEvent : Event {
    val data: Any
    val lastEventId: String
    val origin: String
    val ports: ReadonlyArray<MessagePort>
    val source: dynamic /* typealias WindowProxy = dynamic | MessagePort? | ServiceWorker? */
        get() = definedExternally
}

external interface MessagePortEventMap {
    var message: MessageEvent
    var messageerror: MessageEvent
}

external interface MessagePort : EventTarget {
    var onmessage: ((self: MessagePort, ev: MessageEvent) -> Any)?
    var onmessageerror: ((self: MessagePort, ev: MessageEvent) -> Any)?
    fun close()
    fun postMessage(message: Any, transfer: Array<Any /* ArrayBuffer | MessagePort | ImageBitmap | OffscreenCanvas */>)
    fun postMessage(message: Any, options: PostMessageOptions = definedExternally)
    fun postMessage(message: Any)
    fun start()
    fun <K : String> addEventListener(type: K, listener: (self: MessagePort, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: MessagePort, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: MessagePort, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: MessagePort, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: MessagePort, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: MessagePort, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface MimeType {
    val description: String
    val enabledPlugin: Plugin
    val suffixes: String
    val type: String
}

external interface MimeTypeArray {
    val length: Number
    fun item(index: Number): MimeType?
    fun namedItem(name: String): MimeType?
    @nativeGetter
    operator fun get(index: Number): MimeType?
    @nativeSetter
    operator fun set(index: Number, value: MimeType)
}

external interface MouseEvent : UIEvent {
    val altKey: Boolean
    val button: Number
    val buttons: Number
    val clientX: Number
    val clientY: Number
    val ctrlKey: Boolean
    val metaKey: Boolean
    val movementX: Number
    val movementY: Number
    val offsetX: Number
    val offsetY: Number
    val pageX: Number
    val pageY: Number
    val relatedTarget: EventTarget?
    val screenX: Number
    val screenY: Number
    val shiftKey: Boolean
    val x: Number
    val y: Number
    fun getModifierState(keyArg: String): Boolean
    fun initMouseEvent(typeArg: String, canBubbleArg: Boolean, cancelableArg: Boolean, viewArg: Window, detailArg: Number, screenXArg: Number, screenYArg: Number, clientXArg: Number, clientYArg: Number, ctrlKeyArg: Boolean, altKeyArg: Boolean, shiftKeyArg: Boolean, metaKeyArg: Boolean, buttonArg: Number, relatedTargetArg: EventTarget?)
}

external interface MutationEvent : Event {
    val attrChange: Number
    val attrName: String
    val newValue: String
    val prevValue: String
    val relatedNode: Node
    fun initMutationEvent(typeArg: String, canBubbleArg: Boolean, cancelableArg: Boolean, relatedNodeArg: Node, prevValueArg: String, newValueArg: String, attrNameArg: String, attrChangeArg: Number)
    val ADDITION: Number
    val MODIFICATION: Number
    val REMOVAL: Number
}

external interface NamedNodeMap {
    val length: Number
    fun getNamedItem(qualifiedName: String): Attr?
    fun getNamedItemNS(namespace: String?, localName: String): Attr?
    fun item(index: Number): Attr?
    fun removeNamedItem(qualifiedName: String): Attr
    fun removeNamedItemNS(namespace: String?, localName: String): Attr
    fun setNamedItem(attr: Attr): Attr?
    fun setNamedItemNS(attr: Attr): Attr?
    @nativeGetter
    operator fun get(index: Number): Attr?
    @nativeSetter
    operator fun set(index: Number, value: Attr)
}

external interface NavigationPreloadManager {
    fun disable(): Promise<Unit>
    fun enable(): Promise<Unit>
    fun getState(): Promise<NavigationPreloadState>
    fun setHeaderValue(value: String): Promise<Unit>
}

external interface Navigator : MSFileSaver, MSNavigatorDoNotTrack, NavigatorAutomationInformation, NavigatorBeacon, NavigatorConcurrentHardware, NavigatorContentUtils, NavigatorCookies, NavigatorID, NavigatorLanguage, NavigatorOnLine, NavigatorPlugins, NavigatorStorage {
    var xr: XRSystem?
        get() = definedExternally
        set(value) = definedExternally
    fun requestMediaKeySystemAccess(keySystem: String, supportedConfigurations: Iterable<MediaKeySystemConfiguration>): Promise<MediaKeySystemAccess>
    val activeVRDisplays: ReadonlyArray<VRDisplay>
    val clipboard: Clipboard
    val credentials: CredentialsContainer
    val doNotTrack: String?
    val geolocation: Geolocation
    val maxTouchPoints: Number
    val mediaDevices: MediaDevices
    val msManipulationViewsEnabled: Boolean
    val msMaxTouchPoints: Number
    val msPointerEnabled: Boolean
    val permissions: Permissions
    val pointerEnabled: Boolean
    val serviceWorker: ServiceWorkerContainer
    fun getGamepads(): Array<Gamepad?>
    fun getUserMedia(constraints: MediaStreamConstraints, successCallback: NavigatorUserMediaSuccessCallback, errorCallback: NavigatorUserMediaErrorCallback)
    fun getVRDisplays(): Promise<Array<VRDisplay>>
    fun msLaunchUri(uri: String, successCallback: MSLaunchUriCallback = definedExternally, noHandlerCallback: MSLaunchUriCallback = definedExternally)
    fun requestMediaKeySystemAccess(keySystem: String, supportedConfigurations: Array<MediaKeySystemConfiguration>): Promise<MediaKeySystemAccess>
    override fun sendBeacon(url: String, data: Blob?): Boolean
    override fun sendBeacon(url: String): Boolean
    fun sendBeacon(url: String, data: ArrayBufferView? = definedExternally): Boolean
    override fun sendBeacon(url: String, data: ArrayBuffer?): Boolean
    override fun sendBeacon(url: String, data: FormData?): Boolean
    fun sendBeacon(url: String, data: URLSearchParams? = definedExternally): Boolean
    fun sendBeacon(url: String, data: ReadableStream<Uint8Array>? = definedExternally): Boolean
    override fun sendBeacon(url: String, data: String?): Boolean
    fun share(data: ShareData = definedExternally): Promise<Unit>
    fun vibrate(pattern: Number): Boolean
    fun vibrate(pattern: Array<Number>): Boolean
}

external interface NavigatorAutomationInformation {
    val webdriver: Boolean
}

external interface NavigatorBeacon {
    fun sendBeacon(url: String, data: Blob? = definedExternally): Boolean
    fun sendBeacon(url: String): Boolean
    fun sendBeacon(url: String, data: Int8Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Int16Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Int32Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Uint8Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Uint16Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Uint32Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Uint8ClampedArray? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Float32Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: Float64Array? = definedExternally): Boolean
    fun sendBeacon(url: String, data: DataView? = definedExternally): Boolean
    fun sendBeacon(url: String, data: ArrayBuffer? = definedExternally): Boolean
    fun sendBeacon(url: String, data: FormData? = definedExternally): Boolean
    fun sendBeacon(url: String, data: String? = definedExternally): Boolean
}

external interface NavigatorConcurrentHardware {
    val hardwareConcurrency: Number
}

external interface NavigatorContentUtils {
    fun registerProtocolHandler(scheme: String, url: String, title: String)
    fun unregisterProtocolHandler(scheme: String, url: String)
}

external interface NavigatorCookies {
    val cookieEnabled: Boolean
}

external interface NavigatorID {
    val appCodeName: String
    val appName: String
    val appVersion: String
    val platform: String
    val product: String
    val productSub: String
    val userAgent: String
    val vendor: String
    val vendorSub: String
}

external interface NavigatorLanguage {
    val language: String
    val languages: ReadonlyArray<String>
}

external interface NavigatorOnLine {
    val onLine: Boolean
}

external interface NavigatorPlugins {
    val mimeTypes: MimeTypeArray
    val plugins: PluginArray
    fun javaEnabled(): Boolean
}

external interface NavigatorStorage {
    val storage: StorageManager
}

external interface Node : EventTarget {
    val baseURI: String
    val childNodes: NodeListOf<ChildNode>
    val firstChild: ChildNode?
    val isConnected: Boolean
    val lastChild: ChildNode?
    val namespaceURI: String?
    val nextSibling: ChildNode?
    val nodeName: String
    val nodeType: Number
    var nodeValue: String?
    val ownerDocument: Document?
    val parentElement: HTMLElement?
    val parentNode: Node /* Node & ParentNode */
    val previousSibling: ChildNode?
    var textContent: String?
    fun <T : Node> appendChild(newChild: T): T
    fun cloneNode(deep: Boolean = definedExternally): Node
    fun compareDocumentPosition(other: Node): Number
    fun contains(other: Node?): Boolean
    fun getRootNode(options: GetRootNodeOptions = definedExternally): Node
    fun hasChildNodes(): Boolean
    fun <T : Node> insertBefore(newChild: T, refChild: Node?): T
    fun isDefaultNamespace(namespace: String?): Boolean
    fun isEqualNode(otherNode: Node?): Boolean
    fun isSameNode(otherNode: Node?): Boolean
    fun lookupNamespaceURI(prefix: String?): String?
    fun lookupPrefix(namespace: String?): String?
    fun normalize()
    fun <T : Node> removeChild(oldChild: T): T
    fun <T : Node> replaceChild(newChild: Node, oldChild: T): T
    val ATTRIBUTE_NODE: Number
    val CDATA_SECTION_NODE: Number
    val COMMENT_NODE: Number
    val DOCUMENT_FRAGMENT_NODE: Number
    val DOCUMENT_NODE: Number
    val DOCUMENT_POSITION_CONTAINED_BY: Number
    val DOCUMENT_POSITION_CONTAINS: Number
    val DOCUMENT_POSITION_DISCONNECTED: Number
    val DOCUMENT_POSITION_FOLLOWING: Number
    val DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC: Number
    val DOCUMENT_POSITION_PRECEDING: Number
    val DOCUMENT_TYPE_NODE: Number
    val ELEMENT_NODE: Number
    val ENTITY_NODE: Number
    val ENTITY_REFERENCE_NODE: Number
    val NOTATION_NODE: Number
    val PROCESSING_INSTRUCTION_NODE: Number
    val TEXT_NODE: Number
}

external interface NodeFilter {
    fun acceptNode(node: Node): Number
}

external interface NodeIterator {
    val filter: NodeFilter?
    val pointerBeforeReferenceNode: Boolean
    val referenceNode: Node
    val root: Node
    val whatToShow: Number
    fun detach()
    fun nextNode(): Node?
    fun previousNode(): Node?
}

external interface NodeList {
    fun entries(): IterableIterator<dynamic /* JsTuple<Number, Node> */>
    fun keys(): IterableIterator<Number>
    fun values(): IterableIterator<Node>
    val length: Number
    fun item(index: Number): Node?
    fun forEach(callbackfn: (value: Node, key: Number, parent: NodeList) -> Unit, thisArg: Any = definedExternally)
    @nativeGetter
    operator fun get(index: Number): Node?
    @nativeSetter
    operator fun set(index: Number, value: Node)
}

external interface NodeListOf<TNode : Node> : NodeList {
    override fun entries(): IterableIterator<dynamic /* JsTuple<Number, TNode> */>
    override fun keys(): IterableIterator<Number>
    override fun values(): IterableIterator<TNode>
    override var length: Number
    fun item(index: Number): TNode
    fun forEach(callbackfn: (value: TNode, key: Number, parent: NodeListOf<TNode>) -> Unit, thisArg: Any = definedExternally)
    @nativeGetter
    operator fun get(index: Number): TNode?
    @nativeSetter
    override operator fun set(index: Number, value: TNode)
}

external interface NonDocumentTypeChildNode {
    val nextElementSibling: Element?
    val previousElementSibling: Element?
}

external interface NonElementParentNode {
    fun getElementById(elementId: String): Element?
}

external interface NotificationEventMap {
    var click: Event
    var close: Event
    var error: Event
    var show: Event
}

external interface Notification : EventTarget {
    val actions: ReadonlyArray<NotificationAction>
    val badge: String
    val body: String
    val data: Any
    val dir: String /* "auto" | "ltr" | "rtl" */
    val icon: String
    val image: String
    val lang: String
    var onclick: ((self: Notification, ev: Event) -> Any)?
    var onclose: ((self: Notification, ev: Event) -> Any)?
    var onerror: ((self: Notification, ev: Event) -> Any)?
    var onshow: ((self: Notification, ev: Event) -> Any)?
    val renotify: Boolean
    val requireInteraction: Boolean
    val silent: Boolean
    val tag: String
    val timestamp: Number
    val title: String
    val vibrate: ReadonlyArray<Number>
    fun close()
    fun <K : String> addEventListener(type: K, listener: (self: Notification, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Notification, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Notification, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Notification, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Notification, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Notification, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface OES_element_index_uint

external interface OES_standard_derivatives {
    val FRAGMENT_SHADER_DERIVATIVE_HINT_OES: GLenum
}

external interface OES_texture_float

external interface OES_texture_float_linear

external interface OES_texture_half_float {
    val HALF_FLOAT_OES: GLenum
}

external interface OES_texture_half_float_linear

external interface OES_vertex_array_object {
    fun bindVertexArrayOES(arrayObject: WebGLVertexArrayObjectOES?)
    fun createVertexArrayOES(): WebGLVertexArrayObjectOES?
    fun deleteVertexArrayOES(arrayObject: WebGLVertexArrayObjectOES?)
    fun isVertexArrayOES(arrayObject: WebGLVertexArrayObjectOES?): GLboolean
    val VERTEX_ARRAY_BINDING_OES: GLenum
}

external interface OfflineAudioCompletionEvent : Event {
    val renderedBuffer: AudioBuffer
}

external interface OffscreenCanvas : EventTarget {
    var height: Number
    var width: Number
    fun convertToBlob(options: ImageEncodeOptions = definedExternally): Promise<Blob>
    fun getContext(contextId: String /* "2d" */, options: CanvasRenderingContext2DSettings = definedExternally): OffscreenCanvasRenderingContext2D?
    fun getContext(contextId: String /* "2d" | "bitmaprenderer" | "webgl" | "webgl2" */): dynamic /* WebGL2RenderingContext | OffscreenCanvasRenderingContext2D? | ImageBitmapRenderingContext? | WebGLRenderingContext? | WebGL2RenderingContext? */
    fun getContext(contextId: String /* "bitmaprenderer" */, options: ImageBitmapRenderingContextSettings = definedExternally): ImageBitmapRenderingContext?
    fun getContext(contextId: String /* "webgl" | "webgl2" */, options: WebGLContextAttributes = definedExternally): dynamic /* WebGLRenderingContext | WebGL2RenderingContext */
    fun getContext(contextId: String /* "2d" | "bitmaprenderer" | "webgl" | "webgl2" */, options: Any = definedExternally): dynamic /* OffscreenCanvasRenderingContext2D? | ImageBitmapRenderingContext? | WebGLRenderingContext? | WebGL2RenderingContext? */
    fun transferToImageBitmap(): ImageBitmap
}

external interface OffscreenCanvasRenderingContext2D : CanvasCompositing, CanvasDrawImage, CanvasDrawPath, CanvasFillStrokeStyles, CanvasFilters, CanvasImageData, CanvasImageSmoothing, CanvasPath, CanvasPathDrawingStyles, CanvasRect, CanvasShadowStyles, CanvasState, CanvasText, CanvasTextDrawingStyles, CanvasTransform {
    val canvas: OffscreenCanvas
    fun commit()
}

external interface OscillatorNode : AudioScheduledSourceNode {
    val detune: AudioParam
    val frequency: AudioParam
    var type: String /* "custom" | "sawtooth" | "sine" | "square" | "triangle" */
    fun setPeriodicWave(periodicWave: PeriodicWave)
    fun <K : String> addEventListener(type: K, listener: (self: OscillatorNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: OscillatorNode, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: OscillatorNode, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: OscillatorNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: OscillatorNode, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: OscillatorNode, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface OverflowEvent : UIEvent {
    val horizontalOverflow: Boolean
    val orient: Number
    val verticalOverflow: Boolean
    val BOTH: Number
    val HORIZONTAL: Number
    val VERTICAL: Number
}

external interface PageTransitionEvent : Event {
    val persisted: Boolean
}

external interface PannerNode : AudioNode {
    var coneInnerAngle: Number
    var coneOuterAngle: Number
    var coneOuterGain: Number
    var distanceModel: String /* "exponential" | "inverse" | "linear" */
    var maxDistance: Number
    val orientationX: AudioParam
    val orientationY: AudioParam
    val orientationZ: AudioParam
    var panningModel: String /* "HRTF" | "equalpower" */
    val positionX: AudioParam
    val positionY: AudioParam
    val positionZ: AudioParam
    var refDistance: Number
    var rolloffFactor: Number
    fun setOrientation(x: Number, y: Number, z: Number)
    fun setPosition(x: Number, y: Number, z: Number)
}

external interface ParentNode {
    val childElementCount: Number
    val children: HTMLCollection
    val firstElementChild: Element?
    val lastElementChild: Element?
    fun append(vararg nodes: Any /* Node | String */)
    fun prepend(vararg nodes: Any /* Node | String */)
    fun <K : String> querySelector(selectors: K): Any
    fun <E : Element> querySelector(selectors: String): E?
    fun <K : String> querySelectorAll(selectors: K): NodeListOf<Any>
    fun <E : Element> querySelectorAll(selectors: String): NodeListOf<E>
}

external interface Path2D : CanvasPath {
    fun addPath(path: Path2D, transform: DOMMatrix2DInit = definedExternally)
}

external interface PaymentRequestUpdateEvent : Event {
    fun updateWith(detailsPromise: PaymentDetailsUpdate)
    fun updateWith(detailsPromise: Promise<PaymentDetailsUpdate>)
}

external interface PerformanceEventMap {
    var resourcetimingbufferfull: Event
}

external interface Performance : EventTarget {
    val navigation: PerformanceNavigation
    var onresourcetimingbufferfull: ((self: Performance, ev: Event) -> Any)?
    val timeOrigin: Number
    val timing: PerformanceTiming
    fun clearMarks(markName: String = definedExternally)
    fun clearMeasures(measureName: String = definedExternally)
    fun clearResourceTimings()
    fun getEntries(): PerformanceEntryList
    fun getEntriesByName(name: String, type: String = definedExternally): PerformanceEntryList
    fun getEntriesByType(type: String): PerformanceEntryList
    fun mark(markName: String)
    fun measure(measureName: String, startMark: String = definedExternally, endMark: String = definedExternally)
    fun now(): Number
    fun setResourceTimingBufferSize(maxSize: Number)
    fun toJSON(): Any
    fun <K : String> addEventListener(type: K, listener: (self: Performance, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Performance, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Performance, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Performance, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Performance, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Performance, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface PerformanceEntry {
    val duration: Number
    val entryType: String
    val name: String
    val startTime: Number
    fun toJSON(): Any
}

external interface PerformanceNavigation {
    val redirectCount: Number
    val type: Number
    fun toJSON(): Any
    val TYPE_BACK_FORWARD: Number
    val TYPE_NAVIGATE: Number
    val TYPE_RELOAD: Number
    val TYPE_RESERVED: Number
}

external interface PerformanceTiming {
    val connectEnd: Number
    val connectStart: Number
    val domComplete: Number
    val domContentLoadedEventEnd: Number
    val domContentLoadedEventStart: Number
    val domInteractive: Number
    val domLoading: Number
    val domainLookupEnd: Number
    val domainLookupStart: Number
    val fetchStart: Number
    val loadEventEnd: Number
    val loadEventStart: Number
    val navigationStart: Number
    val redirectEnd: Number
    val redirectStart: Number
    val requestStart: Number
    val responseEnd: Number
    val responseStart: Number
    val secureConnectionStart: Number
    val unloadEventEnd: Number
    val unloadEventStart: Number
    fun toJSON(): Any
}

external interface PeriodicWave

external interface PermissionRequest : DeferredPermissionRequest {
    val state: String /* "allow" | "defer" | "deny" | "unknown" */
    fun defer()
}

external interface PermissionRequestedEvent : Event {
    val permissionRequest: PermissionRequest
}

external interface PermissionStatusEventMap {
    var change: Event
}

external interface PermissionStatus : EventTarget {
    var onchange: ((self: PermissionStatus, ev: Event) -> Any)?
    val state: String /* "denied" | "granted" | "prompt" */
    fun <K : String> addEventListener(type: K, listener: (self: PermissionStatus, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: PermissionStatus, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: PermissionStatus, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: PermissionStatus, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: PermissionStatus, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: PermissionStatus, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface Permissions {
    fun query(permissionDesc: PermissionDescriptor): Promise<PermissionStatus>
    fun query(permissionDesc: DevicePermissionDescriptor): Promise<PermissionStatus>
    fun query(permissionDesc: MidiPermissionDescriptor): Promise<PermissionStatus>
    fun query(permissionDesc: PushPermissionDescriptor): Promise<PermissionStatus>
}

external interface Plugin {
    val description: String
    val filename: String
    val length: Number
    val name: String
    fun item(index: Number): MimeType?
    fun namedItem(name: String): MimeType?
    @nativeGetter
    operator fun get(index: Number): MimeType?
    @nativeSetter
    operator fun set(index: Number, value: MimeType)
}

external interface PluginArray {
    val length: Number
    fun item(index: Number): Plugin?
    fun namedItem(name: String): Plugin?
    fun refresh(reload: Boolean = definedExternally)
    @nativeGetter
    operator fun get(index: Number): Plugin?
    @nativeSetter
    operator fun set(index: Number, value: Plugin)
}

external interface PointerEvent : MouseEvent {
    val height: Number
    val isPrimary: Boolean
    val pointerId: Number
    val pointerType: String
    val pressure: Number
    val tangentialPressure: Number
    val tiltX: Number
    val tiltY: Number
    val twist: Number
    val width: Number
}

external interface PopStateEvent : Event {
    val state: Any
}

external interface Position {
    val coords: Coordinates
    val timestamp: Number
}

external interface PositionError {
    val code: Number
    val message: String
    val PERMISSION_DENIED: Number
    val POSITION_UNAVAILABLE: Number
    val TIMEOUT: Number
}

external interface ProcessingInstruction : CharacterData, LinkStyle {
    override val ownerDocument: Document
    val target: String
}

external interface ProgressEvent<T : EventTarget> : Event {
    val lengthComputable: Boolean
    val loaded: Number
    val total: Number
}

external interface ProgressEvent__0 : ProgressEvent<EventTarget>

external interface PromiseRejectionEvent : Event {
    val promise: Promise<Any>
    val reason: Any
}

external interface PushManager {
    fun getSubscription(): Promise<PushSubscription?>
    fun permissionState(options: PushSubscriptionOptionsInit = definedExternally): Promise<String /* "denied" | "granted" | "prompt" */>
    fun subscribe(options: PushSubscriptionOptionsInit = definedExternally): Promise<PushSubscription>
}

external interface PushSubscription {
    val endpoint: String
    val expirationTime: Number?
    val options: PushSubscriptionOptions
    fun getKey(name: String /* "auth" | "p256dh" */): ArrayBuffer?
    fun toJSON(): PushSubscriptionJSON
    fun unsubscribe(): Promise<Boolean>
}

external interface PushSubscriptionOptions {
    val applicationServerKey: ArrayBuffer?
    val userVisibleOnly: Boolean
}

external interface RTCDTMFSenderEventMap {
    var tonechange: RTCDTMFToneChangeEvent
}

external interface RTCDTMFSender : EventTarget {
    val canInsertDTMF: Boolean
    var ontonechange: ((self: RTCDTMFSender, ev: RTCDTMFToneChangeEvent) -> Any)?
    val toneBuffer: String
    fun insertDTMF(tones: String, duration: Number = definedExternally, interToneGap: Number = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDTMFSender, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDTMFSender, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDTMFSender, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDTMFSender, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDTMFSender, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDTMFSender, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface RTCDTMFToneChangeEvent : Event {
    val tone: String
}

external interface RTCDataChannelEventMap {
    var bufferedamountlow: Event
    var close: Event
    var error: RTCErrorEvent
    var message: MessageEvent
    var open: Event
}

external interface RTCDataChannel : EventTarget {
    var binaryType: String
    val bufferedAmount: Number
    var bufferedAmountLowThreshold: Number
    val id: Number?
    val label: String
    val maxPacketLifeTime: Number?
    val maxRetransmits: Number?
    val negotiated: Boolean
    var onbufferedamountlow: ((self: RTCDataChannel, ev: Event) -> Any)?
    var onclose: ((self: RTCDataChannel, ev: Event) -> Any)?
    var onerror: ((self: RTCDataChannel, ev: RTCErrorEvent) -> Any)?
    var onmessage: ((self: RTCDataChannel, ev: MessageEvent) -> Any)?
    var onopen: ((self: RTCDataChannel, ev: Event) -> Any)?
    val ordered: Boolean
    val priority: String /* "high" | "low" | "medium" | "very-low" */
    val protocol: String
    val readyState: String /* "closed" | "closing" | "connecting" | "open" */
    fun close()
    fun send(data: String)
    fun send(data: Blob)
    fun send(data: ArrayBuffer)
    fun send(data: ArrayBufferView)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDataChannel, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDataChannel, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDataChannel, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDataChannel, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDataChannel, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDataChannel, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface RTCDataChannelEvent : Event {
    val channel: RTCDataChannel
}

external interface RTCDtlsTransportEventMap {
    var error: RTCErrorEvent
    var statechange: Event
}

external interface RTCDtlsTransport : EventTarget {
    val iceTransport: RTCIceTransport
    var onerror: ((self: RTCDtlsTransport, ev: RTCErrorEvent) -> Any)?
    var onstatechange: ((self: RTCDtlsTransport, ev: Event) -> Any)?
    val state: String /* "closed" | "connected" | "connecting" | "failed" | "new" */
    fun getRemoteCertificates(): Array<ArrayBuffer>
    fun <K : String> addEventListener(type: K, listener: (self: RTCDtlsTransport, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDtlsTransport, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: RTCDtlsTransport, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDtlsTransport, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDtlsTransport, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCDtlsTransport, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface RTCDtlsTransportStateChangedEvent : Event {
    val state: String /* "closed" | "connected" | "connecting" | "failed" | "new" */
}

external interface RTCError : DOMException {
    val errorDetail: String /* "data-channel-failure" | "dtls-failure" | "fingerprint-failure" | "hardware-encoder-error" | "hardware-encoder-not-available" | "idp-bad-script-failure" | "idp-execution-failure" | "idp-load-failure" | "idp-need-login" | "idp-timeout" | "idp-tls-failure" | "idp-token-expired" | "idp-token-invalid" | "sctp-failure" | "sdp-syntax-error" */
    val httpRequestStatusCode: Number?
    val receivedAlert: Number?
    val sctpCauseCode: Number?
    val sdpLineNumber: Number?
    val sentAlert: Number?
}

external interface RTCErrorEvent : Event {
    val error: RTCError
}

external interface RTCIceCandidate {
    val candidate: String
    val component: String /* "rtcp" | "rtp" */
    val foundation: String?
    val port: Number?
    val priority: Number?
    val protocol: String /* "tcp" | "udp" */
    val relatedAddress: String?
    val relatedPort: Number?
    val sdpMLineIndex: Number?
    val sdpMid: String?
    val tcpType: String /* "active" | "passive" | "so" */
    val type: String /* "host" | "prflx" | "relay" | "srflx" */
    val usernameFragment: String?
    fun toJSON(): RTCIceCandidateInit
}

external interface RTCIceCandidatePairChangedEvent : Event {
    val pair: RTCIceCandidatePair
}

external interface RTCIceGathererEvent : Event {
    val candidate: dynamic /* RTCIceCandidateDictionary | RTCIceCandidateComplete */
        get() = definedExternally
}

external interface RTCIceTransportEventMap {
    var gatheringstatechange: Event
    var selectedcandidatepairchange: Event
    var statechange: Event
}

external interface RTCIceTransport : EventTarget {
    val component: String /* "rtcp" | "rtp" */
    val gatheringState: String /* "complete" | "gathering" | "new" */
    var ongatheringstatechange: ((self: RTCIceTransport, ev: Event) -> Any)?
    var onselectedcandidatepairchange: ((self: RTCIceTransport, ev: Event) -> Any)?
    var onstatechange: ((self: RTCIceTransport, ev: Event) -> Any)?
    val role: String /* "controlled" | "controlling" | "unknown" */
    val state: String /* "checking" | "closed" | "completed" | "connected" | "disconnected" | "failed" | "new" */
    fun getLocalCandidates(): Array<RTCIceCandidate>
    fun getLocalParameters(): RTCIceParameters?
    fun getRemoteCandidates(): Array<RTCIceCandidate>
    fun getRemoteParameters(): RTCIceParameters?
    fun getSelectedCandidatePair(): RTCIceCandidatePair?
    fun <K : String> addEventListener(type: K, listener: (self: RTCIceTransport, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: RTCIceTransport, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: RTCIceTransport, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCIceTransport, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCIceTransport, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: RTCIceTransport, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface RTCIceTransportStateChangedEvent : Event {
    val state: String /* "checking" | "closed" | "completed" | "connected" | "disconnected" | "failed" | "new" */
}

external interface RTCPeerConnectionIceErrorEvent : Event {
    val errorCode: Number
    val errorText: String
    val hostCandidate: String
    val url: String
}

external interface RTCPeerConnectionIceEvent : Event {
    val candidate: RTCIceCandidate?
    val url: String?
}

external interface RTCRtpReceiver {
    val rtcpTransport: RTCDtlsTransport?
    val track: MediaStreamTrack
    val transport: RTCDtlsTransport?
    fun getContributingSources(): Array<RTCRtpContributingSource>
    fun getParameters(): RTCRtpReceiveParameters
    fun getStats(): Promise<RTCStatsReport>
    fun getSynchronizationSources(): Array<RTCRtpSynchronizationSource>
}

external interface RTCRtpSender {
    val dtmf: RTCDTMFSender?
    val rtcpTransport: RTCDtlsTransport?
    val track: MediaStreamTrack?
    val transport: RTCDtlsTransport?
    fun getParameters(): RTCRtpSendParameters
    fun getStats(): Promise<RTCStatsReport>
    fun replaceTrack(withTrack: MediaStreamTrack?): Promise<Unit>
    fun setParameters(parameters: RTCRtpSendParameters): Promise<Unit>
    fun setStreams(vararg streams: MediaStream)
}

external interface RTCRtpTransceiver {
    fun setCodecPreferences(codecs: Iterable<RTCRtpCodecCapability>)
    val currentDirection: String /* "inactive" | "recvonly" | "sendonly" | "sendrecv" | "stopped" */
    var direction: String /* "inactive" | "recvonly" | "sendonly" | "sendrecv" | "stopped" */
    val mid: String?
    val receiver: RTCRtpReceiver
    val sender: RTCRtpSender
    fun setCodecPreferences(codecs: Array<RTCRtpCodecCapability>)
    fun stop()
}

external interface RTCSsrcConflictEvent : Event {
    val ssrc: Number
}

external interface RTCStatsEvent : Event {
    val report: RTCStatsReport
}

external interface RTCTrackEvent : Event {
    val receiver: RTCRtpReceiver
    val streams: ReadonlyArray<MediaStream>
    val track: MediaStreamTrack
    val transceiver: RTCRtpTransceiver
}

external interface RadioNodeList : NodeList {
    var value: String
}

external interface Range : AbstractRange {
    val commonAncestorContainer: Node
    fun cloneContents(): DocumentFragment
    fun cloneRange(): Range
    fun collapse(toStart: Boolean = definedExternally)
    fun compareBoundaryPoints(how: Number, sourceRange: Range): Number
    fun comparePoint(node: Node, offset: Number): Number
    fun createContextualFragment(fragment: String): DocumentFragment
    fun deleteContents()
    fun detach()
    fun extractContents(): DocumentFragment
    fun getBoundingClientRect(): DOMRect
    fun getClientRects(): DOMRectList
    fun insertNode(node: Node)
    fun intersectsNode(node: Node): Boolean
    fun isPointInRange(node: Node, offset: Number): Boolean
    fun selectNode(node: Node)
    fun selectNodeContents(node: Node)
    fun setEnd(node: Node, offset: Number)
    fun setEndAfter(node: Node)
    fun setEndBefore(node: Node)
    fun setStart(node: Node, offset: Number)
    fun setStartAfter(node: Node)
    fun setStartBefore(node: Node)
    fun surroundContents(newParent: Node)
    override fun toString(): String
    val END_TO_END: Number
    val END_TO_START: Number
    val START_TO_END: Number
    val START_TO_START: Number
}

external interface ReadableByteStreamController {
    val byobRequest: ReadableStreamBYOBRequest?
    val desiredSize: Number?
    fun close()
    fun enqueue(chunk: ArrayBufferView)
    fun error(error: Any = definedExternally)
}

external interface `T$13` {
    var mode: String /* "byob" */
}

external interface `T$14`<R, T> {
    var writable: WritableStream<R>
    var readable: ReadableStream<T>
}

external interface ReadableStream<R> {
    val locked: Boolean
    fun cancel(reason: Any = definedExternally): Promise<Unit>
    fun getReader(options: `T$13`): ReadableStreamBYOBReader
    fun getReader(): ReadableStreamDefaultReader<R>
    fun <T> pipeThrough(__0: `T$14`<R, T>, options: PipeOptions = definedExternally): ReadableStream<T>
    fun pipeTo(dest: WritableStream<R>, options: PipeOptions = definedExternally): Promise<Unit>
    fun tee(): dynamic /* JsTuple<ReadableStream<R>, ReadableStream<R>> */
}

external interface ReadableStream__0 : ReadableStream<Any>

external interface ReadableStreamBYOBReader {
    val closed: Promise<Unit>
    fun cancel(reason: Any = definedExternally): Promise<Unit>
    fun <T : ArrayBufferView> read(view: T): Promise<dynamic /* ReadableStreamReadValueResult<T> | ReadableStreamReadDoneResult<T> */>
    fun releaseLock()
}

external interface ReadableStreamBYOBRequest {
    val view: ArrayBufferView
    fun respond(bytesWritten: Number)
    fun respondWithNewView(view: ArrayBufferView)
}

external interface ReadableStreamDefaultController<R> {
    val desiredSize: Number?
    fun close()
    fun enqueue(chunk: R)
    fun error(error: Any = definedExternally)
}

external interface ReadableStreamDefaultReader<R> {
    val closed: Promise<Unit>
    fun cancel(reason: Any = definedExternally): Promise<Unit>
    fun read(): Promise<dynamic /* ReadableStreamReadValueResult<R> | ReadableStreamReadDoneResult<R> */>
    fun releaseLock()
}

external interface Request : Body {
    val cache: String /* "default" | "force-cache" | "no-cache" | "no-store" | "only-if-cached" | "reload" */
    val credentials: String /* "include" | "omit" | "same-origin" */
    val destination: String /* "" | "audio" | "audioworklet" | "document" | "embed" | "font" | "image" | "manifest" | "object" | "paintworklet" | "report" | "script" | "sharedworker" | "style" | "track" | "video" | "worker" | "xslt" */
    val headers: Headers
    val integrity: String
    val isHistoryNavigation: Boolean
    val isReloadNavigation: Boolean
    val keepalive: Boolean
    val method: String
    val mode: String /* "cors" | "navigate" | "no-cors" | "same-origin" */
    val redirect: String /* "error" | "follow" | "manual" */
    val referrer: String
    val referrerPolicy: String /* "" | "no-referrer" | "no-referrer-when-downgrade" | "origin" | "origin-when-cross-origin" | "same-origin" | "strict-origin" | "strict-origin-when-cross-origin" | "unsafe-url" */
    val signal: AbortSignal
    val url: String
    fun clone(): Request
}

external interface Response : Body {
    val headers: Headers
    val ok: Boolean
    val redirected: Boolean
    val status: Number
    val statusText: String
    val trailer: Promise<Headers>
    val type: String /* "basic" | "cors" | "default" | "error" | "opaque" | "opaqueredirect" */
    val url: String
    fun clone(): Response
}

external interface SVGAElement : SVGGraphicsElement, SVGURIReference {
    val target: SVGAnimatedString
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGAElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGAElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGAElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGAElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGAElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGAElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGAngle {
    val unitType: Number
    var value: Number
    var valueAsString: String
    var valueInSpecifiedUnits: Number
    fun convertToSpecifiedUnits(unitType: Number)
    fun newValueSpecifiedUnits(unitType: Number, valueInSpecifiedUnits: Number)
    val SVG_ANGLETYPE_DEG: Number
    val SVG_ANGLETYPE_GRAD: Number
    val SVG_ANGLETYPE_RAD: Number
    val SVG_ANGLETYPE_UNKNOWN: Number
    val SVG_ANGLETYPE_UNSPECIFIED: Number
}

external interface SVGAnimatedAngle {
    val animVal: SVGAngle
    val baseVal: SVGAngle
}

external interface SVGAnimatedBoolean {
    val animVal: Boolean
    var baseVal: Boolean
}

external interface SVGAnimatedEnumeration {
    val animVal: Number
    var baseVal: Number
}

external interface SVGAnimatedInteger {
    val animVal: Number
    var baseVal: Number
}

external interface SVGAnimatedLength {
    val animVal: SVGLength
    val baseVal: SVGLength
}

external interface SVGAnimatedLengthList {
    val animVal: SVGLengthList
    val baseVal: SVGLengthList
}

external interface SVGAnimatedNumber {
    val animVal: Number
    var baseVal: Number
}

external interface SVGAnimatedNumberList {
    val animVal: SVGNumberList
    val baseVal: SVGNumberList
}

external interface SVGAnimatedPoints {
    val animatedPoints: SVGPointList
    val points: SVGPointList
}

external interface SVGAnimatedPreserveAspectRatio {
    val animVal: SVGPreserveAspectRatio
    val baseVal: SVGPreserveAspectRatio
}

external interface SVGAnimatedRect {
    val animVal: DOMRectReadOnly
    val baseVal: DOMRect
}

external interface SVGAnimatedString {
    val animVal: String
    var baseVal: String
}

external interface SVGAnimatedTransformList {
    val animVal: SVGTransformList
    val baseVal: SVGTransformList
}

external interface SVGCircleElement : SVGGeometryElement {
    val cx: SVGAnimatedLength
    val cy: SVGAnimatedLength
    val r: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGCircleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGCircleElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGCircleElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGCircleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGCircleElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGCircleElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGClipPathElement : SVGElement {
    val clipPathUnits: SVGAnimatedEnumeration
    val transform: SVGAnimatedTransformList
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGClipPathElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGClipPathElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGClipPathElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGClipPathElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGClipPathElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGClipPathElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGComponentTransferFunctionElement : SVGElement {
    val amplitude: SVGAnimatedNumber
    val exponent: SVGAnimatedNumber
    val intercept: SVGAnimatedNumber
    val offset: SVGAnimatedNumber
    val slope: SVGAnimatedNumber
    val tableValues: SVGAnimatedNumberList
    val type: SVGAnimatedEnumeration
    val SVG_FECOMPONENTTRANSFER_TYPE_DISCRETE: Number
    val SVG_FECOMPONENTTRANSFER_TYPE_GAMMA: Number
    val SVG_FECOMPONENTTRANSFER_TYPE_IDENTITY: Number
    val SVG_FECOMPONENTTRANSFER_TYPE_LINEAR: Number
    val SVG_FECOMPONENTTRANSFER_TYPE_TABLE: Number
    val SVG_FECOMPONENTTRANSFER_TYPE_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGComponentTransferFunctionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGComponentTransferFunctionElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGComponentTransferFunctionElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGComponentTransferFunctionElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGComponentTransferFunctionElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGComponentTransferFunctionElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGDefsElement : SVGGraphicsElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGDefsElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGDefsElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGDefsElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGDefsElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGDefsElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGDefsElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGDescElement : SVGElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGDescElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGDescElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGDescElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGDescElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGDescElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGDescElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGElementEventMap : ElementEventMap, GlobalEventHandlersEventMap, DocumentAndElementEventHandlersEventMap

external interface SVGElement : Element, DocumentAndElementEventHandlers, DocumentAndElementEventHandlers, ElementCSSInlineStyle, GlobalEventHandlers, GlobalEventHandlers, HTMLOrSVGElement, SVGElementInstance {
    val ownerSVGElement: SVGSVGElement?
    val viewportElement: SVGElement?
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
    override var oncopy: ((self: DocumentAndElementEventHandlers, ev: ClipboardEvent) -> Any)?
    override var oncut: ((self: DocumentAndElementEventHandlers, ev: ClipboardEvent) -> Any)?
    override var onpaste: ((self: DocumentAndElementEventHandlers, ev: ClipboardEvent) -> Any)?
    override var onbeforexrselect: ((self: GlobalEventHandlers, ev: XRSessionEvent) -> Any)?
    override var onabort: ((self: GlobalEventHandlers, ev: UIEvent) -> Any)?
    override var onanimationcancel: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    override var onanimationend: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    override var onanimationiteration: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    override var onanimationstart: ((self: GlobalEventHandlers, ev: AnimationEvent) -> Any)?
    override var onauxclick: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onblur: ((self: GlobalEventHandlers, ev: FocusEvent) -> Any)?
    override var oncancel: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var oncanplay: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var oncanplaythrough: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onchange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onclick: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onclose: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var oncontextmenu: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var oncuechange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var ondblclick: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var ondrag: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondragend: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondragenter: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondragexit: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var ondragleave: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondragover: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondragstart: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondrop: ((self: GlobalEventHandlers, ev: DragEvent) -> Any)?
    override var ondurationchange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onemptied: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onended: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onerror: OnErrorEventHandlerNonNull?
    override var onfocus: ((self: GlobalEventHandlers, ev: FocusEvent) -> Any)?
    override var ongotpointercapture: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var oninput: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var oninvalid: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onkeydown: ((self: GlobalEventHandlers, ev: KeyboardEvent) -> Any)?
    override var onkeypress: ((self: GlobalEventHandlers, ev: KeyboardEvent) -> Any)?
    override var onkeyup: ((self: GlobalEventHandlers, ev: KeyboardEvent) -> Any)?
    override var onload: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onloadeddata: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onloadedmetadata: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onloadstart: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onlostpointercapture: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onmousedown: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onmouseenter: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onmouseleave: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onmousemove: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onmouseout: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onmouseover: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onmouseup: ((self: GlobalEventHandlers, ev: MouseEvent) -> Any)?
    override var onpause: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onplay: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onplaying: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onpointercancel: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointerdown: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointerenter: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointerleave: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointermove: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointerout: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointerover: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onpointerup: ((self: GlobalEventHandlers, ev: PointerEvent) -> Any)?
    override var onprogress: ((self: GlobalEventHandlers, ev: ProgressEvent__0) -> Any)?
    override var onratechange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onreset: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onresize: ((self: GlobalEventHandlers, ev: UIEvent) -> Any)?
    override var onscroll: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onsecuritypolicyviolation: ((self: GlobalEventHandlers, ev: SecurityPolicyViolationEvent) -> Any)?
    override var onseeked: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onseeking: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onselect: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onselectionchange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onselectstart: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onstalled: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onsubmit: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onsuspend: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var ontimeupdate: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var ontoggle: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var ontouchcancel: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    override var ontouchend: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    override var ontouchmove: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    override var ontouchstart: ((self: GlobalEventHandlers, ev: TouchEvent) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    override var ontransitioncancel: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    override var ontransitionend: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    override var ontransitionrun: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    override var ontransitionstart: ((self: GlobalEventHandlers, ev: TransitionEvent) -> Any)?
    override var onvolumechange: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onwaiting: ((self: GlobalEventHandlers, ev: Event) -> Any)?
    override var onwheel: ((self: GlobalEventHandlers, ev: WheelEvent) -> Any)?
}

external interface SVGElementInstance : EventTarget {
    val correspondingElement: SVGElement
    val correspondingUseElement: SVGUseElement
}

external interface SVGEllipseElement : SVGGeometryElement {
    val cx: SVGAnimatedLength
    val cy: SVGAnimatedLength
    val rx: SVGAnimatedLength
    val ry: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGEllipseElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGEllipseElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGEllipseElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGEllipseElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGEllipseElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGEllipseElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEBlendElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val in2: SVGAnimatedString
    val mode: SVGAnimatedEnumeration
    val SVG_FEBLEND_MODE_COLOR: Number
    val SVG_FEBLEND_MODE_COLOR_BURN: Number
    val SVG_FEBLEND_MODE_COLOR_DODGE: Number
    val SVG_FEBLEND_MODE_DARKEN: Number
    val SVG_FEBLEND_MODE_DIFFERENCE: Number
    val SVG_FEBLEND_MODE_EXCLUSION: Number
    val SVG_FEBLEND_MODE_HARD_LIGHT: Number
    val SVG_FEBLEND_MODE_HUE: Number
    val SVG_FEBLEND_MODE_LIGHTEN: Number
    val SVG_FEBLEND_MODE_LUMINOSITY: Number
    val SVG_FEBLEND_MODE_MULTIPLY: Number
    val SVG_FEBLEND_MODE_NORMAL: Number
    val SVG_FEBLEND_MODE_OVERLAY: Number
    val SVG_FEBLEND_MODE_SATURATION: Number
    val SVG_FEBLEND_MODE_SCREEN: Number
    val SVG_FEBLEND_MODE_SOFT_LIGHT: Number
    val SVG_FEBLEND_MODE_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEBlendElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEBlendElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEBlendElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEBlendElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEBlendElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEBlendElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEColorMatrixElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val type: SVGAnimatedEnumeration
    val values: SVGAnimatedNumberList
    val SVG_FECOLORMATRIX_TYPE_HUEROTATE: Number
    val SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA: Number
    val SVG_FECOLORMATRIX_TYPE_MATRIX: Number
    val SVG_FECOLORMATRIX_TYPE_SATURATE: Number
    val SVG_FECOLORMATRIX_TYPE_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEColorMatrixElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEColorMatrixElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEColorMatrixElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEColorMatrixElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEColorMatrixElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEColorMatrixElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEComponentTransferElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEComponentTransferElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEComponentTransferElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEComponentTransferElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEComponentTransferElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEComponentTransferElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEComponentTransferElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFECompositeElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val in2: SVGAnimatedString
    val k1: SVGAnimatedNumber
    val k2: SVGAnimatedNumber
    val k3: SVGAnimatedNumber
    val k4: SVGAnimatedNumber
    val operator: SVGAnimatedEnumeration
    val SVG_FECOMPOSITE_OPERATOR_ARITHMETIC: Number
    val SVG_FECOMPOSITE_OPERATOR_ATOP: Number
    val SVG_FECOMPOSITE_OPERATOR_IN: Number
    val SVG_FECOMPOSITE_OPERATOR_OUT: Number
    val SVG_FECOMPOSITE_OPERATOR_OVER: Number
    val SVG_FECOMPOSITE_OPERATOR_UNKNOWN: Number
    val SVG_FECOMPOSITE_OPERATOR_XOR: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFECompositeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFECompositeElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFECompositeElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFECompositeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFECompositeElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFECompositeElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEConvolveMatrixElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val bias: SVGAnimatedNumber
    val divisor: SVGAnimatedNumber
    val edgeMode: SVGAnimatedEnumeration
    val in1: SVGAnimatedString
    val kernelMatrix: SVGAnimatedNumberList
    val kernelUnitLengthX: SVGAnimatedNumber
    val kernelUnitLengthY: SVGAnimatedNumber
    val orderX: SVGAnimatedInteger
    val orderY: SVGAnimatedInteger
    val preserveAlpha: SVGAnimatedBoolean
    val targetX: SVGAnimatedInteger
    val targetY: SVGAnimatedInteger
    val SVG_EDGEMODE_DUPLICATE: Number
    val SVG_EDGEMODE_NONE: Number
    val SVG_EDGEMODE_UNKNOWN: Number
    val SVG_EDGEMODE_WRAP: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEConvolveMatrixElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEConvolveMatrixElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEConvolveMatrixElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEConvolveMatrixElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEConvolveMatrixElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEConvolveMatrixElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEDiffuseLightingElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val diffuseConstant: SVGAnimatedNumber
    val in1: SVGAnimatedString
    val kernelUnitLengthX: SVGAnimatedNumber
    val kernelUnitLengthY: SVGAnimatedNumber
    val surfaceScale: SVGAnimatedNumber
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDiffuseLightingElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDiffuseLightingElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDiffuseLightingElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDiffuseLightingElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDiffuseLightingElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDiffuseLightingElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEDisplacementMapElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val in2: SVGAnimatedString
    val scale: SVGAnimatedNumber
    val xChannelSelector: SVGAnimatedEnumeration
    val yChannelSelector: SVGAnimatedEnumeration
    val SVG_CHANNEL_A: Number
    val SVG_CHANNEL_B: Number
    val SVG_CHANNEL_G: Number
    val SVG_CHANNEL_R: Number
    val SVG_CHANNEL_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDisplacementMapElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDisplacementMapElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDisplacementMapElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDisplacementMapElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDisplacementMapElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDisplacementMapElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEDistantLightElement : SVGElement {
    val azimuth: SVGAnimatedNumber
    val elevation: SVGAnimatedNumber
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDistantLightElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDistantLightElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEDistantLightElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDistantLightElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDistantLightElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEDistantLightElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEFloodElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFloodElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFloodElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFloodElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFloodElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFloodElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFloodElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEFuncAElement : SVGComponentTransferFunctionElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncAElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncAElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncAElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncAElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncAElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncAElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEFuncBElement : SVGComponentTransferFunctionElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncBElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncBElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncBElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncBElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncBElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncBElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEFuncGElement : SVGComponentTransferFunctionElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncGElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncGElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncGElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncGElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEFuncRElement : SVGComponentTransferFunctionElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncRElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncRElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEFuncRElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncRElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncRElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEFuncRElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEGaussianBlurElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val stdDeviationX: SVGAnimatedNumber
    val stdDeviationY: SVGAnimatedNumber
    fun setStdDeviation(stdDeviationX: Number, stdDeviationY: Number)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEGaussianBlurElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEGaussianBlurElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEGaussianBlurElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEGaussianBlurElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEGaussianBlurElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEGaussianBlurElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEImageElement : SVGElement, SVGFilterPrimitiveStandardAttributes, SVGURIReference {
    val preserveAspectRatio: SVGAnimatedPreserveAspectRatio
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEImageElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEImageElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEImageElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEImageElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEImageElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEImageElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEMergeElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMergeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMergeElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMergeElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMergeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMergeElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMergeElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEMergeNodeElement : SVGElement {
    val in1: SVGAnimatedString
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMergeNodeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMergeNodeElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMergeNodeElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMergeNodeElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMergeNodeElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMergeNodeElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEMorphologyElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val operator: SVGAnimatedEnumeration
    val radiusX: SVGAnimatedNumber
    val radiusY: SVGAnimatedNumber
    val SVG_MORPHOLOGY_OPERATOR_DILATE: Number
    val SVG_MORPHOLOGY_OPERATOR_ERODE: Number
    val SVG_MORPHOLOGY_OPERATOR_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMorphologyElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMorphologyElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEMorphologyElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMorphologyElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMorphologyElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEMorphologyElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEOffsetElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val dx: SVGAnimatedNumber
    val dy: SVGAnimatedNumber
    val in1: SVGAnimatedString
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEOffsetElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEOffsetElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEOffsetElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEOffsetElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEOffsetElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEOffsetElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFEPointLightElement : SVGElement {
    val x: SVGAnimatedNumber
    val y: SVGAnimatedNumber
    val z: SVGAnimatedNumber
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEPointLightElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEPointLightElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFEPointLightElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEPointLightElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEPointLightElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFEPointLightElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFESpecularLightingElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    val kernelUnitLengthX: SVGAnimatedNumber
    val kernelUnitLengthY: SVGAnimatedNumber
    val specularConstant: SVGAnimatedNumber
    val specularExponent: SVGAnimatedNumber
    val surfaceScale: SVGAnimatedNumber
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFESpecularLightingElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFESpecularLightingElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFESpecularLightingElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFESpecularLightingElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFESpecularLightingElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFESpecularLightingElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFESpotLightElement : SVGElement {
    val limitingConeAngle: SVGAnimatedNumber
    val pointsAtX: SVGAnimatedNumber
    val pointsAtY: SVGAnimatedNumber
    val pointsAtZ: SVGAnimatedNumber
    val specularExponent: SVGAnimatedNumber
    val x: SVGAnimatedNumber
    val y: SVGAnimatedNumber
    val z: SVGAnimatedNumber
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFESpotLightElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFESpotLightElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFESpotLightElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFESpotLightElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFESpotLightElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFESpotLightElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFETileElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val in1: SVGAnimatedString
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFETileElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFETileElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFETileElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFETileElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFETileElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFETileElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFETurbulenceElement : SVGElement, SVGFilterPrimitiveStandardAttributes {
    val baseFrequencyX: SVGAnimatedNumber
    val baseFrequencyY: SVGAnimatedNumber
    val numOctaves: SVGAnimatedInteger
    val seed: SVGAnimatedNumber
    val stitchTiles: SVGAnimatedEnumeration
    val type: SVGAnimatedEnumeration
    val SVG_STITCHTYPE_NOSTITCH: Number
    val SVG_STITCHTYPE_STITCH: Number
    val SVG_STITCHTYPE_UNKNOWN: Number
    val SVG_TURBULENCE_TYPE_FRACTALNOISE: Number
    val SVG_TURBULENCE_TYPE_TURBULENCE: Number
    val SVG_TURBULENCE_TYPE_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFETurbulenceElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFETurbulenceElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFETurbulenceElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFETurbulenceElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFETurbulenceElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFETurbulenceElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFilterElement : SVGElement, SVGURIReference {
    val filterUnits: SVGAnimatedEnumeration
    val height: SVGAnimatedLength
    val primitiveUnits: SVGAnimatedEnumeration
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFilterElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFilterElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGFilterElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFilterElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFilterElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGFilterElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGFilterPrimitiveStandardAttributes {
    val height: SVGAnimatedLength
    val result: SVGAnimatedString
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
}

external interface SVGFitToViewBox {
    val preserveAspectRatio: SVGAnimatedPreserveAspectRatio
    val viewBox: SVGAnimatedRect
}

external interface SVGForeignObjectElement : SVGGraphicsElement {
    val height: SVGAnimatedLength
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGForeignObjectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGForeignObjectElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGForeignObjectElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGForeignObjectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGForeignObjectElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGForeignObjectElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGGElement : SVGGraphicsElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGGeometryElement : SVGGraphicsElement {
    val pathLength: SVGAnimatedNumber
    fun getPointAtLength(distance: Number): DOMPoint
    fun getTotalLength(): Number
    fun isPointInFill(point: DOMPointInit = definedExternally): Boolean
    fun isPointInStroke(point: DOMPointInit = definedExternally): Boolean
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGeometryElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGeometryElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGeometryElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGeometryElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGeometryElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGeometryElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGGradientElement : SVGElement, SVGURIReference {
    val gradientTransform: SVGAnimatedTransformList
    val gradientUnits: SVGAnimatedEnumeration
    val spreadMethod: SVGAnimatedEnumeration
    val SVG_SPREADMETHOD_PAD: Number
    val SVG_SPREADMETHOD_REFLECT: Number
    val SVG_SPREADMETHOD_REPEAT: Number
    val SVG_SPREADMETHOD_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGradientElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGradientElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGradientElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGradientElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGradientElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGradientElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGGraphicsElement : SVGElement, SVGTests {
    val transform: SVGAnimatedTransformList
    fun getBBox(options: SVGBoundingBoxOptions = definedExternally): DOMRect
    fun getCTM(): DOMMatrix?
    fun getScreenCTM(): DOMMatrix?
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGraphicsElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGraphicsElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGGraphicsElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGraphicsElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGraphicsElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGGraphicsElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGImageElement : SVGGraphicsElement, SVGURIReference {
    val height: SVGAnimatedLength
    val preserveAspectRatio: SVGAnimatedPreserveAspectRatio
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGImageElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGImageElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGImageElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGImageElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGImageElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGImageElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGLength {
    val unitType: Number
    var value: Number
    var valueAsString: String
    var valueInSpecifiedUnits: Number
    fun convertToSpecifiedUnits(unitType: Number)
    fun newValueSpecifiedUnits(unitType: Number, valueInSpecifiedUnits: Number)
    val SVG_LENGTHTYPE_CM: Number
    val SVG_LENGTHTYPE_EMS: Number
    val SVG_LENGTHTYPE_EXS: Number
    val SVG_LENGTHTYPE_IN: Number
    val SVG_LENGTHTYPE_MM: Number
    val SVG_LENGTHTYPE_NUMBER: Number
    val SVG_LENGTHTYPE_PC: Number
    val SVG_LENGTHTYPE_PERCENTAGE: Number
    val SVG_LENGTHTYPE_PT: Number
    val SVG_LENGTHTYPE_PX: Number
    val SVG_LENGTHTYPE_UNKNOWN: Number
}

external interface SVGLengthList {
    val length: Number
    val numberOfItems: Number
    fun appendItem(newItem: SVGLength): SVGLength
    fun clear()
    fun getItem(index: Number): SVGLength
    fun initialize(newItem: SVGLength): SVGLength
    fun insertItemBefore(newItem: SVGLength, index: Number): SVGLength
    fun removeItem(index: Number): SVGLength
    fun replaceItem(newItem: SVGLength, index: Number): SVGLength
    @nativeGetter
    operator fun get(index: Number): SVGLength?
    @nativeSetter
    operator fun set(index: Number, value: SVGLength)
}

external interface SVGLineElement : SVGGeometryElement {
    val x1: SVGAnimatedLength
    val x2: SVGAnimatedLength
    val y1: SVGAnimatedLength
    val y2: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGLineElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGLineElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGLineElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGLineElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGLineElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGLineElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGLinearGradientElement : SVGGradientElement {
    val x1: SVGAnimatedLength
    val x2: SVGAnimatedLength
    val y1: SVGAnimatedLength
    val y2: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGLinearGradientElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGLinearGradientElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGLinearGradientElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGLinearGradientElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGLinearGradientElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGLinearGradientElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGMarkerElement : SVGElement, SVGFitToViewBox {
    val markerHeight: SVGAnimatedLength
    val markerUnits: SVGAnimatedEnumeration
    val markerWidth: SVGAnimatedLength
    val orientAngle: SVGAnimatedAngle
    val orientType: SVGAnimatedEnumeration
    val refX: SVGAnimatedLength
    val refY: SVGAnimatedLength
    fun setOrientToAngle(angle: SVGAngle)
    fun setOrientToAuto()
    val SVG_MARKERUNITS_STROKEWIDTH: Number
    val SVG_MARKERUNITS_UNKNOWN: Number
    val SVG_MARKERUNITS_USERSPACEONUSE: Number
    val SVG_MARKER_ORIENT_ANGLE: Number
    val SVG_MARKER_ORIENT_AUTO: Number
    val SVG_MARKER_ORIENT_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMarkerElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMarkerElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMarkerElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMarkerElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMarkerElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMarkerElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGMaskElement : SVGElement {
    val height: SVGAnimatedLength
    val maskContentUnits: SVGAnimatedEnumeration
    val maskUnits: SVGAnimatedEnumeration
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMaskElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMaskElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMaskElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMaskElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMaskElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMaskElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGMetadataElement : SVGElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMetadataElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMetadataElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGMetadataElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMetadataElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMetadataElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGMetadataElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGNumber {
    var value: Number
}

external interface SVGNumberList {
    val length: Number
    val numberOfItems: Number
    fun appendItem(newItem: SVGNumber): SVGNumber
    fun clear()
    fun getItem(index: Number): SVGNumber
    fun initialize(newItem: SVGNumber): SVGNumber
    fun insertItemBefore(newItem: SVGNumber, index: Number): SVGNumber
    fun removeItem(index: Number): SVGNumber
    fun replaceItem(newItem: SVGNumber, index: Number): SVGNumber
    @nativeGetter
    operator fun get(index: Number): SVGNumber?
    @nativeSetter
    operator fun set(index: Number, value: SVGNumber)
}

external interface SVGPathElement : SVGGraphicsElement {
    val pathSegList: SVGPathSegList
    fun createSVGPathSegArcAbs(x: Number, y: Number, r1: Number, r2: Number, angle: Number, largeArcFlag: Boolean, sweepFlag: Boolean): SVGPathSegArcAbs
    fun createSVGPathSegArcRel(x: Number, y: Number, r1: Number, r2: Number, angle: Number, largeArcFlag: Boolean, sweepFlag: Boolean): SVGPathSegArcRel
    fun createSVGPathSegClosePath(): SVGPathSegClosePath
    fun createSVGPathSegCurvetoCubicAbs(x: Number, y: Number, x1: Number, y1: Number, x2: Number, y2: Number): SVGPathSegCurvetoCubicAbs
    fun createSVGPathSegCurvetoCubicRel(x: Number, y: Number, x1: Number, y1: Number, x2: Number, y2: Number): SVGPathSegCurvetoCubicRel
    fun createSVGPathSegCurvetoCubicSmoothAbs(x: Number, y: Number, x2: Number, y2: Number): SVGPathSegCurvetoCubicSmoothAbs
    fun createSVGPathSegCurvetoCubicSmoothRel(x: Number, y: Number, x2: Number, y2: Number): SVGPathSegCurvetoCubicSmoothRel
    fun createSVGPathSegCurvetoQuadraticAbs(x: Number, y: Number, x1: Number, y1: Number): SVGPathSegCurvetoQuadraticAbs
    fun createSVGPathSegCurvetoQuadraticRel(x: Number, y: Number, x1: Number, y1: Number): SVGPathSegCurvetoQuadraticRel
    fun createSVGPathSegCurvetoQuadraticSmoothAbs(x: Number, y: Number): SVGPathSegCurvetoQuadraticSmoothAbs
    fun createSVGPathSegCurvetoQuadraticSmoothRel(x: Number, y: Number): SVGPathSegCurvetoQuadraticSmoothRel
    fun createSVGPathSegLinetoAbs(x: Number, y: Number): SVGPathSegLinetoAbs
    fun createSVGPathSegLinetoHorizontalAbs(x: Number): SVGPathSegLinetoHorizontalAbs
    fun createSVGPathSegLinetoHorizontalRel(x: Number): SVGPathSegLinetoHorizontalRel
    fun createSVGPathSegLinetoRel(x: Number, y: Number): SVGPathSegLinetoRel
    fun createSVGPathSegLinetoVerticalAbs(y: Number): SVGPathSegLinetoVerticalAbs
    fun createSVGPathSegLinetoVerticalRel(y: Number): SVGPathSegLinetoVerticalRel
    fun createSVGPathSegMovetoAbs(x: Number, y: Number): SVGPathSegMovetoAbs
    fun createSVGPathSegMovetoRel(x: Number, y: Number): SVGPathSegMovetoRel
    fun getPathSegAtLength(distance: Number): Number
    fun getPointAtLength(distance: Number): SVGPoint
    fun getTotalLength(): Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPathElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPathElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPathElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPathElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPathElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPathElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGPathSeg {
    val pathSegType: Number
    val pathSegTypeAsLetter: String
    val PATHSEG_ARC_ABS: Number
    val PATHSEG_ARC_REL: Number
    val PATHSEG_CLOSEPATH: Number
    val PATHSEG_CURVETO_CUBIC_ABS: Number
    val PATHSEG_CURVETO_CUBIC_REL: Number
    val PATHSEG_CURVETO_CUBIC_SMOOTH_ABS: Number
    val PATHSEG_CURVETO_CUBIC_SMOOTH_REL: Number
    val PATHSEG_CURVETO_QUADRATIC_ABS: Number
    val PATHSEG_CURVETO_QUADRATIC_REL: Number
    val PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS: Number
    val PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL: Number
    val PATHSEG_LINETO_ABS: Number
    val PATHSEG_LINETO_HORIZONTAL_ABS: Number
    val PATHSEG_LINETO_HORIZONTAL_REL: Number
    val PATHSEG_LINETO_REL: Number
    val PATHSEG_LINETO_VERTICAL_ABS: Number
    val PATHSEG_LINETO_VERTICAL_REL: Number
    val PATHSEG_MOVETO_ABS: Number
    val PATHSEG_MOVETO_REL: Number
    val PATHSEG_UNKNOWN: Number
}

external interface SVGPathSegArcAbs : SVGPathSeg {
    var angle: Number
    var largeArcFlag: Boolean
    var r1: Number
    var r2: Number
    var sweepFlag: Boolean
    var x: Number
    var y: Number
}

external interface SVGPathSegArcRel : SVGPathSeg {
    var angle: Number
    var largeArcFlag: Boolean
    var r1: Number
    var r2: Number
    var sweepFlag: Boolean
    var x: Number
    var y: Number
}

external interface SVGPathSegClosePath : SVGPathSeg

external interface SVGPathSegCurvetoCubicAbs : SVGPathSeg {
    var x: Number
    var x1: Number
    var x2: Number
    var y: Number
    var y1: Number
    var y2: Number
}

external interface SVGPathSegCurvetoCubicRel : SVGPathSeg {
    var x: Number
    var x1: Number
    var x2: Number
    var y: Number
    var y1: Number
    var y2: Number
}

external interface SVGPathSegCurvetoCubicSmoothAbs : SVGPathSeg {
    var x: Number
    var x2: Number
    var y: Number
    var y2: Number
}

external interface SVGPathSegCurvetoCubicSmoothRel : SVGPathSeg {
    var x: Number
    var x2: Number
    var y: Number
    var y2: Number
}

external interface SVGPathSegCurvetoQuadraticAbs : SVGPathSeg {
    var x: Number
    var x1: Number
    var y: Number
    var y1: Number
}

external interface SVGPathSegCurvetoQuadraticRel : SVGPathSeg {
    var x: Number
    var x1: Number
    var y: Number
    var y1: Number
}

external interface SVGPathSegCurvetoQuadraticSmoothAbs : SVGPathSeg {
    var x: Number
    var y: Number
}

external interface SVGPathSegCurvetoQuadraticSmoothRel : SVGPathSeg {
    var x: Number
    var y: Number
}

external interface SVGPathSegLinetoAbs : SVGPathSeg {
    var x: Number
    var y: Number
}

external interface SVGPathSegLinetoHorizontalAbs : SVGPathSeg {
    var x: Number
}

external interface SVGPathSegLinetoHorizontalRel : SVGPathSeg {
    var x: Number
}

external interface SVGPathSegLinetoRel : SVGPathSeg {
    var x: Number
    var y: Number
}

external interface SVGPathSegLinetoVerticalAbs : SVGPathSeg {
    var y: Number
}

external interface SVGPathSegLinetoVerticalRel : SVGPathSeg {
    var y: Number
}

external interface SVGPathSegList {
    val numberOfItems: Number
    fun appendItem(newItem: SVGPathSeg): SVGPathSeg
    fun clear()
    fun getItem(index: Number): SVGPathSeg
    fun initialize(newItem: SVGPathSeg): SVGPathSeg
    fun insertItemBefore(newItem: SVGPathSeg, index: Number): SVGPathSeg
    fun removeItem(index: Number): SVGPathSeg
    fun replaceItem(newItem: SVGPathSeg, index: Number): SVGPathSeg
}

external interface SVGPathSegMovetoAbs : SVGPathSeg {
    var x: Number
    var y: Number
}

external interface SVGPathSegMovetoRel : SVGPathSeg {
    var x: Number
    var y: Number
}

external interface SVGPatternElement : SVGElement, SVGFitToViewBox, SVGURIReference {
    val height: SVGAnimatedLength
    val patternContentUnits: SVGAnimatedEnumeration
    val patternTransform: SVGAnimatedTransformList
    val patternUnits: SVGAnimatedEnumeration
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPatternElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPatternElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPatternElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPatternElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPatternElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPatternElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGPointList {
    val length: Number
    val numberOfItems: Number
    fun appendItem(newItem: DOMPoint): DOMPoint
    fun clear()
    fun getItem(index: Number): DOMPoint
    fun initialize(newItem: DOMPoint): DOMPoint
    fun insertItemBefore(newItem: DOMPoint, index: Number): DOMPoint
    fun removeItem(index: Number): DOMPoint
    fun replaceItem(newItem: DOMPoint, index: Number): DOMPoint
    @nativeGetter
    operator fun get(index: Number): DOMPoint?
    @nativeSetter
    operator fun set(index: Number, value: DOMPoint)
}

external interface SVGPolygonElement : SVGGeometryElement, SVGAnimatedPoints {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPolygonElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPolygonElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPolygonElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPolygonElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPolygonElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPolygonElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGPolylineElement : SVGGeometryElement, SVGAnimatedPoints {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPolylineElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPolylineElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGPolylineElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPolylineElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPolylineElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGPolylineElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGPreserveAspectRatio {
    var align: Number
    var meetOrSlice: Number
    val SVG_MEETORSLICE_MEET: Number
    val SVG_MEETORSLICE_SLICE: Number
    val SVG_MEETORSLICE_UNKNOWN: Number
    val SVG_PRESERVEASPECTRATIO_NONE: Number
    val SVG_PRESERVEASPECTRATIO_UNKNOWN: Number
    val SVG_PRESERVEASPECTRATIO_XMAXYMAX: Number
    val SVG_PRESERVEASPECTRATIO_XMAXYMID: Number
    val SVG_PRESERVEASPECTRATIO_XMAXYMIN: Number
    val SVG_PRESERVEASPECTRATIO_XMIDYMAX: Number
    val SVG_PRESERVEASPECTRATIO_XMIDYMID: Number
    val SVG_PRESERVEASPECTRATIO_XMIDYMIN: Number
    val SVG_PRESERVEASPECTRATIO_XMINYMAX: Number
    val SVG_PRESERVEASPECTRATIO_XMINYMID: Number
    val SVG_PRESERVEASPECTRATIO_XMINYMIN: Number
}

external interface SVGRadialGradientElement : SVGGradientElement {
    val cx: SVGAnimatedLength
    val cy: SVGAnimatedLength
    val fr: SVGAnimatedLength
    val fx: SVGAnimatedLength
    val fy: SVGAnimatedLength
    val r: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGRadialGradientElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGRadialGradientElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGRadialGradientElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGRadialGradientElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGRadialGradientElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGRadialGradientElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGRectElement : SVGGeometryElement {
    val height: SVGAnimatedLength
    val rx: SVGAnimatedLength
    val ry: SVGAnimatedLength
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGRectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGRectElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGRectElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGRectElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGRectElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGRectElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGSVGElementEventMap : SVGElementEventMap {
    var SVGUnload: Event
    var SVGZoom: SVGZoomEvent
}

external interface SVGSVGElement : SVGGraphicsElement, DocumentEvent, SVGFitToViewBox, SVGZoomAndPan {
    var contentScriptType: String
    var contentStyleType: String
    var currentScale: Number
    val currentTranslate: SVGPoint
    val height: SVGAnimatedLength
    var onunload: ((self: SVGSVGElement, ev: Event) -> Any)?
    var onzoom: ((self: SVGSVGElement, ev: SVGZoomEvent) -> Any)?
    val pixelUnitToMillimeterX: Number
    val pixelUnitToMillimeterY: Number
    val screenPixelToMillimeterX: Number
    val screenPixelToMillimeterY: Number
    val viewport: SVGRect
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun animationsPaused(): Boolean
    fun checkEnclosure(element: SVGElement, rect: SVGRect): Boolean
    fun checkIntersection(element: SVGElement, rect: SVGRect): Boolean
    fun createSVGAngle(): SVGAngle
    fun createSVGLength(): SVGLength
    fun createSVGMatrix(): SVGMatrix
    fun createSVGNumber(): SVGNumber
    fun createSVGPoint(): SVGPoint
    fun createSVGRect(): SVGRect
    fun createSVGTransform(): SVGTransform
    fun createSVGTransformFromMatrix(matrix: SVGMatrix): SVGTransform
    fun deselectAll()
    fun forceRedraw()
    fun getComputedStyle(elt: Element, pseudoElt: String? = definedExternally): CSSStyleDeclaration
    fun getCurrentTime(): Number
    fun getElementById(elementId: String): Element
    fun getEnclosureList(rect: SVGRect, referenceElement: SVGElement): NodeListOf<dynamic /* SVGCircleElement | SVGEllipseElement | SVGImageElement | SVGLineElement | SVGPathElement | SVGPolygonElement | SVGPolylineElement | SVGRectElement | SVGTextElement | SVGUseElement */>
    fun getIntersectionList(rect: SVGRect, referenceElement: SVGElement): NodeListOf<dynamic /* SVGCircleElement | SVGEllipseElement | SVGImageElement | SVGLineElement | SVGPathElement | SVGPolygonElement | SVGPolylineElement | SVGRectElement | SVGTextElement | SVGUseElement */>
    fun pauseAnimations()
    fun setCurrentTime(seconds: Number)
    fun suspendRedraw(maxWaitMilliseconds: Number): Number
    fun unpauseAnimations()
    fun unsuspendRedraw(suspendHandleID: Number)
    fun unsuspendRedrawAll()
    fun <K : String> addEventListener(type: K, listener: (self: SVGSVGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: SVGSVGElement, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: SVGSVGElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: SVGSVGElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: SVGSVGElement, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: SVGSVGElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGScriptElement : SVGElement, SVGURIReference {
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGScriptElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGScriptElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGScriptElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGScriptElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGScriptElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGScriptElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGStopElement : SVGElement {
    val offset: SVGAnimatedNumber
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGStopElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGStopElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGStopElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGStopElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGStopElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGStopElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGStringList {
    val length: Number
    val numberOfItems: Number
    fun appendItem(newItem: String): String
    fun clear()
    fun getItem(index: Number): String
    fun initialize(newItem: String): String
    fun insertItemBefore(newItem: String, index: Number): String
    fun removeItem(index: Number): String
    fun replaceItem(newItem: String, index: Number): String
    @nativeGetter
    operator fun get(index: Number): String?
    @nativeSetter
    operator fun set(index: Number, value: String)
}

external interface SVGStyleElement : SVGElement {
    var disabled: Boolean
    var media: String
    var title: String
    var type: String
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGStyleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGStyleElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGStyleElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGStyleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGStyleElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGStyleElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGSwitchElement : SVGGraphicsElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGSwitchElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGSwitchElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGSwitchElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGSwitchElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGSwitchElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGSwitchElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGSymbolElement : SVGElement, SVGFitToViewBox {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGSymbolElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGSymbolElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGSymbolElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGSymbolElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGSymbolElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGSymbolElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTSpanElement : SVGTextPositioningElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTSpanElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTSpanElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTSpanElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTSpanElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTSpanElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTSpanElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTests {
    val requiredExtensions: SVGStringList
    val systemLanguage: SVGStringList
}

external interface SVGTextContentElement : SVGGraphicsElement {
    val lengthAdjust: SVGAnimatedEnumeration
    val textLength: SVGAnimatedLength
    fun getCharNumAtPosition(point: DOMPointInit = definedExternally): Number
    fun getComputedTextLength(): Number
    fun getEndPositionOfChar(charnum: Number): DOMPoint
    fun getExtentOfChar(charnum: Number): DOMRect
    fun getNumberOfChars(): Number
    fun getRotationOfChar(charnum: Number): Number
    fun getStartPositionOfChar(charnum: Number): DOMPoint
    fun getSubStringLength(charnum: Number, nchars: Number): Number
    fun selectSubString(charnum: Number, nchars: Number)
    val LENGTHADJUST_SPACING: Number
    val LENGTHADJUST_SPACINGANDGLYPHS: Number
    val LENGTHADJUST_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextContentElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextContentElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextContentElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextContentElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextContentElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextContentElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTextElement : SVGTextPositioningElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTextPathElement : SVGTextContentElement, SVGURIReference {
    val method: SVGAnimatedEnumeration
    val spacing: SVGAnimatedEnumeration
    val startOffset: SVGAnimatedLength
    val TEXTPATH_METHODTYPE_ALIGN: Number
    val TEXTPATH_METHODTYPE_STRETCH: Number
    val TEXTPATH_METHODTYPE_UNKNOWN: Number
    val TEXTPATH_SPACINGTYPE_AUTO: Number
    val TEXTPATH_SPACINGTYPE_EXACT: Number
    val TEXTPATH_SPACINGTYPE_UNKNOWN: Number
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextPathElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextPathElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextPathElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextPathElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextPathElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextPathElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTextPositioningElement : SVGTextContentElement {
    val dx: SVGAnimatedLengthList
    val dy: SVGAnimatedLengthList
    val rotate: SVGAnimatedNumberList
    val x: SVGAnimatedLengthList
    val y: SVGAnimatedLengthList
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextPositioningElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextPositioningElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTextPositioningElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextPositioningElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextPositioningElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTextPositioningElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTitleElement : SVGElement {
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTitleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTitleElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGTitleElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTitleElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTitleElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGTitleElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGTransform {
    val angle: Number
    val matrix: SVGMatrix
    val type: Number
    fun setMatrix(matrix: SVGMatrix)
    fun setRotate(angle: Number, cx: Number, cy: Number)
    fun setScale(sx: Number, sy: Number)
    fun setSkewX(angle: Number)
    fun setSkewY(angle: Number)
    fun setTranslate(tx: Number, ty: Number)
    val SVG_TRANSFORM_MATRIX: Number
    val SVG_TRANSFORM_ROTATE: Number
    val SVG_TRANSFORM_SCALE: Number
    val SVG_TRANSFORM_SKEWX: Number
    val SVG_TRANSFORM_SKEWY: Number
    val SVG_TRANSFORM_TRANSLATE: Number
    val SVG_TRANSFORM_UNKNOWN: Number
}

external interface SVGTransformList {
    val numberOfItems: Number
    fun appendItem(newItem: SVGTransform): SVGTransform
    fun clear()
    fun consolidate(): SVGTransform
    fun createSVGTransformFromMatrix(matrix: SVGMatrix): SVGTransform
    fun getItem(index: Number): SVGTransform
    fun initialize(newItem: SVGTransform): SVGTransform
    fun insertItemBefore(newItem: SVGTransform, index: Number): SVGTransform
    fun removeItem(index: Number): SVGTransform
    fun replaceItem(newItem: SVGTransform, index: Number): SVGTransform
}

external interface SVGURIReference {
    val href: SVGAnimatedString
}

external interface SVGUseElement : SVGGraphicsElement, SVGURIReference {
    val animatedInstanceRoot: SVGElementInstance?
    val height: SVGAnimatedLength
    val instanceRoot: SVGElementInstance?
    val width: SVGAnimatedLength
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGUseElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGUseElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGUseElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGUseElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGUseElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGUseElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGViewElement : SVGElement, SVGFitToViewBox, SVGZoomAndPan {
    val viewTarget: SVGStringList
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGViewElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGViewElement, ev: Any) -> Any)
    fun <K : Nothing?> addEventListener(type: K, listener: (self: SVGViewElement, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGViewElement, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGViewElement, ev: Any) -> Any)
    fun <K : Nothing?> removeEventListener(type: K, listener: (self: SVGViewElement, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface SVGZoomAndPan {
    val zoomAndPan: Number
}

external interface SVGZoomEvent : UIEvent {
    val newScale: Number
    val newTranslate: SVGPoint
    val previousScale: Number
    val previousTranslate: SVGPoint
    val zoomRectScreen: SVGRect
}

external interface Screen {
    val availHeight: Number
    val availWidth: Number
    val colorDepth: Number
    val height: Number
    val orientation: ScreenOrientation
    val pixelDepth: Number
    val width: Number
}

external interface ScreenOrientationEventMap {
    var change: Event
}

external interface ScreenOrientation : EventTarget {
    val angle: Number
    var onchange: ((self: ScreenOrientation, ev: Event) -> Any)?
    val type: String /* "landscape-primary" | "landscape-secondary" | "portrait-primary" | "portrait-secondary" */
    fun lock(orientation: String /* "any" | "landscape" | "landscape-primary" | "landscape-secondary" | "natural" | "portrait" | "portrait-primary" | "portrait-secondary" */): Promise<Unit>
    fun unlock()
    fun <K : String> addEventListener(type: K, listener: (self: ScreenOrientation, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ScreenOrientation, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ScreenOrientation, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ScreenOrientation, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ScreenOrientation, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ScreenOrientation, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface ScriptProcessorNodeEventMap {
    var audioprocess: AudioProcessingEvent
}

external interface ScriptProcessorNode : AudioNode {
    val bufferSize: Number
    var onaudioprocess: ((self: ScriptProcessorNode, ev: AudioProcessingEvent) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: ScriptProcessorNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ScriptProcessorNode, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ScriptProcessorNode, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ScriptProcessorNode, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ScriptProcessorNode, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ScriptProcessorNode, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface SecurityPolicyViolationEvent : Event {
    val blockedURI: String
    val columnNumber: Number
    val documentURI: String
    val effectiveDirective: String
    val lineNumber: Number
    val originalPolicy: String
    val referrer: String
    val sourceFile: String
    val statusCode: Number
    val violatedDirective: String
}

external interface Selection {
    val anchorNode: Node?
    val anchorOffset: Number
    val focusNode: Node?
    val focusOffset: Number
    val isCollapsed: Boolean
    val rangeCount: Number
    val type: String
    fun addRange(range: Range)
    fun collapse(node: Node?, offset: Number = definedExternally)
    fun collapseToEnd()
    fun collapseToStart()
    fun containsNode(node: Node, allowPartialContainment: Boolean = definedExternally): Boolean
    fun deleteFromDocument()
    fun empty()
    fun extend(node: Node, offset: Number = definedExternally)
    fun getRangeAt(index: Number): Range
    fun removeAllRanges()
    fun removeRange(range: Range)
    fun selectAllChildren(node: Node)
    fun setBaseAndExtent(anchorNode: Node, anchorOffset: Number, focusNode: Node, focusOffset: Number)
    fun setPosition(node: Node?, offset: Number = definedExternally)
    override fun toString(): String
}

external interface ServiceWorkerEventMap : AbstractWorkerEventMap {
    var statechange: Event
}

external interface ServiceWorker : EventTarget, AbstractWorker {
    var onstatechange: ((self: ServiceWorker, ev: Event) -> Any)?
    val scriptURL: String
    val state: String /* "activated" | "activating" | "installed" | "installing" | "parsed" | "redundant" */
    fun postMessage(message: Any, transfer: Array<Any /* ArrayBuffer | MessagePort | ImageBitmap | OffscreenCanvas */>)
    fun postMessage(message: Any, options: PostMessageOptions = definedExternally)
    fun postMessage(message: Any)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorker, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorker, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorker, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorker, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorker, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorker, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface ServiceWorkerContainerEventMap {
    var controllerchange: Event
    var message: MessageEvent
    var messageerror: MessageEvent
}

external interface ServiceWorkerContainer : EventTarget {
    val controller: ServiceWorker?
    var oncontrollerchange: ((self: ServiceWorkerContainer, ev: Event) -> Any)?
    var onmessage: ((self: ServiceWorkerContainer, ev: MessageEvent) -> Any)?
    var onmessageerror: ((self: ServiceWorkerContainer, ev: MessageEvent) -> Any)?
    val ready: Promise<ServiceWorkerRegistration>
    fun getRegistration(clientURL: String = definedExternally): Promise<ServiceWorkerRegistration?>
    fun getRegistrations(): Promise<ReadonlyArray<ServiceWorkerRegistration>>
    fun register(scriptURL: String, options: RegistrationOptions = definedExternally): Promise<ServiceWorkerRegistration>
    fun startMessages()
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorkerContainer, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorkerContainer, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorkerContainer, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorkerContainer, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorkerContainer, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorkerContainer, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface ServiceWorkerMessageEvent : Event {
    val data: Any
    val lastEventId: String
    val origin: String
    val ports: ReadonlyArray<MessagePort>?
    val source: dynamic /* ServiceWorker? | MessagePort? */
        get() = definedExternally
}

external interface ServiceWorkerRegistrationEventMap {
    var updatefound: Event
}

external interface ServiceWorkerRegistration : EventTarget {
    val active: ServiceWorker?
    val installing: ServiceWorker?
    val navigationPreload: NavigationPreloadManager
    var onupdatefound: ((self: ServiceWorkerRegistration, ev: Event) -> Any)?
    val pushManager: PushManager
    val scope: String
    val sync: SyncManager
    val updateViaCache: String /* "all" | "imports" | "none" */
    val waiting: ServiceWorker?
    fun getNotifications(filter: GetNotificationOptions = definedExternally): Promise<Array<Notification>>
    fun showNotification(title: String, options: NotificationOptions = definedExternally): Promise<Unit>
    fun unregister(): Promise<Boolean>
    fun update(): Promise<Unit>
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorkerRegistration, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorkerRegistration, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: ServiceWorkerRegistration, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorkerRegistration, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorkerRegistration, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: ServiceWorkerRegistration, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface ShadowRoot : DocumentFragment, DocumentOrShadowRoot, InnerHTML {
    val host: Element
    val mode: String /* "closed" | "open" */
}

external interface Slotable {
    val assignedSlot: HTMLSlotElement?
}

external interface SourceBufferEventMap {
    var abort: Event
    var error: Event
    var update: Event
    var updateend: Event
    var updatestart: Event
}

external interface SourceBuffer : EventTarget {
    var appendWindowEnd: Number
    var appendWindowStart: Number
    val buffered: TimeRanges
    var mode: String /* "segments" | "sequence" */
    var onabort: ((self: SourceBuffer, ev: Event) -> Any)?
    var onerror: ((self: SourceBuffer, ev: Event) -> Any)?
    var onupdate: ((self: SourceBuffer, ev: Event) -> Any)?
    var onupdateend: ((self: SourceBuffer, ev: Event) -> Any)?
    var onupdatestart: ((self: SourceBuffer, ev: Event) -> Any)?
    var timestampOffset: Number
    val updating: Boolean
    fun abort()
    fun appendBuffer(data: ArrayBufferView)
    fun appendBuffer(data: ArrayBuffer)
    fun remove(start: Number, end: Number)
    fun <K : String> addEventListener(type: K, listener: (self: SourceBuffer, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: SourceBuffer, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: SourceBuffer, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: SourceBuffer, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: SourceBuffer, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: SourceBuffer, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface SourceBufferListEventMap {
    var addsourcebuffer: Event
    var removesourcebuffer: Event
}

external interface SourceBufferList : EventTarget {
    val length: Number
    var onaddsourcebuffer: ((self: SourceBufferList, ev: Event) -> Any)?
    var onremovesourcebuffer: ((self: SourceBufferList, ev: Event) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: SourceBufferList, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: SourceBufferList, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: SourceBufferList, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: SourceBufferList, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: SourceBufferList, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: SourceBufferList, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
    @nativeGetter
    operator fun get(index: Number): SourceBuffer?
    @nativeSetter
    operator fun set(index: Number, value: SourceBuffer)
}

external interface SpeechRecognitionAlternative {
    val confidence: Number
    val transcript: String
}

external interface SpeechRecognitionEvent : Event {
    val resultIndex: Number
    val results: SpeechRecognitionResultList
}

external interface SpeechRecognitionResult {
    val isFinal: Boolean
    val length: Number
    fun item(index: Number): SpeechRecognitionAlternative
    @nativeGetter
    operator fun get(index: Number): SpeechRecognitionAlternative?
    @nativeSetter
    operator fun set(index: Number, value: SpeechRecognitionAlternative)
}

external interface SpeechRecognitionResultList {
    val length: Number
    fun item(index: Number): SpeechRecognitionResult
    @nativeGetter
    operator fun get(index: Number): SpeechRecognitionResult?
    @nativeSetter
    operator fun set(index: Number, value: SpeechRecognitionResult)
}

external interface SpeechSynthesisEventMap {
    var voiceschanged: Event
}

external interface SpeechSynthesis : EventTarget {
    var onvoiceschanged: ((self: SpeechSynthesis, ev: Event) -> Any)?
    val paused: Boolean
    val pending: Boolean
    val speaking: Boolean
    fun cancel()
    fun getVoices(): Array<SpeechSynthesisVoice>
    fun pause()
    fun resume()
    fun speak(utterance: SpeechSynthesisUtterance)
    fun <K : String> addEventListener(type: K, listener: (self: SpeechSynthesis, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: SpeechSynthesis, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: SpeechSynthesis, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: SpeechSynthesis, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: SpeechSynthesis, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: SpeechSynthesis, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface SpeechSynthesisErrorEvent : SpeechSynthesisEvent {
    val error: String /* "audio-busy" | "audio-hardware" | "canceled" | "interrupted" | "invalid-argument" | "language-unavailable" | "network" | "not-allowed" | "synthesis-failed" | "synthesis-unavailable" | "text-too-long" | "voice-unavailable" */
}

external interface SpeechSynthesisEvent : Event {
    val charIndex: Number
    val charLength: Number
    val elapsedTime: Number
    val name: String
    val utterance: SpeechSynthesisUtterance
}

external interface SpeechSynthesisUtteranceEventMap {
    var boundary: SpeechSynthesisEvent
    var end: SpeechSynthesisEvent
    var error: SpeechSynthesisErrorEvent
    var mark: SpeechSynthesisEvent
    var pause: SpeechSynthesisEvent
    var resume: SpeechSynthesisEvent
    var start: SpeechSynthesisEvent
}

external interface SpeechSynthesisUtterance : EventTarget {
    var lang: String
    var onboundary: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisEvent) -> Any)?
    var onend: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisEvent) -> Any)?
    var onerror: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisErrorEvent) -> Any)?
    var onmark: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisEvent) -> Any)?
    var onpause: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisEvent) -> Any)?
    var onresume: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisEvent) -> Any)?
    var onstart: ((self: SpeechSynthesisUtterance, ev: SpeechSynthesisEvent) -> Any)?
    var pitch: Number
    var rate: Number
    var text: String
    var voice: SpeechSynthesisVoice?
    var volume: Number
    fun <K : String> addEventListener(type: K, listener: (self: SpeechSynthesisUtterance, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: SpeechSynthesisUtterance, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: SpeechSynthesisUtterance, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: SpeechSynthesisUtterance, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: SpeechSynthesisUtterance, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: SpeechSynthesisUtterance, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface SpeechSynthesisVoice {
    val default: Boolean
    val lang: String
    val localService: Boolean
    val name: String
    val voiceURI: String
}

external interface StereoPannerNode : AudioNode {
    val pan: AudioParam
}

external interface Storage {
    val length: Number
    fun clear()
    fun getItem(key: String): String?
    fun key(index: Number): String?
    fun removeItem(key: String)
    fun setItem(key: String, value: String)
    @nativeGetter
    operator fun get(name: String): Any?
    @nativeSetter
    operator fun set(name: String, value: Any)
}

external interface StorageEvent : Event {
    val key: String?
    val newValue: String?
    val oldValue: String?
    val storageArea: Storage?
    val url: String
}

external interface StorageManager {
    fun estimate(): Promise<StorageEstimate>
    fun persist(): Promise<Boolean>
    fun persisted(): Promise<Boolean>
}

external interface StyleMedia {
    val type: String
    fun matchMedium(mediaquery: String): Boolean
}

external interface StyleSheet {
    var disabled: Boolean
    val href: String?
    val media: MediaList
    val ownerNode: dynamic /* Element? | ProcessingInstruction? */
        get() = definedExternally
    val parentStyleSheet: CSSStyleSheet?
    val title: String?
    val type: String
}

external interface StyleSheetList {
    val length: Number
    fun item(index: Number): CSSStyleSheet?
    @nativeGetter
    operator fun get(index: Number): CSSStyleSheet?
    @nativeSetter
    operator fun set(index: Number, value: CSSStyleSheet)
}

external interface SubtleCrypto {
    fun decrypt(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: RsaOaepParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCtrParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCbcParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesGcmParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun decrypt(algorithm: AesCfbParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: String, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: Algorithm, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: EcdhKeyDeriveParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: DhKeyDeriveParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: ConcatParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: HkdfCtrParams, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveBits(algorithm: Pbkdf2Params, baseKey: CryptoKey, length: Number): PromiseLike<ArrayBuffer>
    fun deriveKey(algorithm: String, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: Algorithm, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: EcdhKeyDeriveParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: DhKeyDeriveParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: ConcatParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: HkdfCtrParams, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun deriveKey(algorithm: Pbkdf2Params, baseKey: CryptoKey, derivedKeyType: Any /* String | AesDerivedKeyParams | HmacImportParams | ConcatParams | HkdfCtrParams | Pbkdf2Params */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun digest(algorithm: String, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun digest(algorithm: Algorithm, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: RsaOaepParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCtrParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCbcParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesGcmParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun encrypt(algorithm: AesCfbParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun exportKey(format: String /* "jwk" | "raw" | "pkcs8" | "spki" */, key: CryptoKey): dynamic /* PromiseLike */
    fun generateKey(algorithm: String, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<dynamic /* CryptoKeyPair | CryptoKey */>
    fun generateKey(algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<dynamic /* CryptoKeyPair | CryptoKey */>
    fun generateKey(algorithm: RsaHashedKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKeyPair>
    fun generateKey(algorithm: EcKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKeyPair>
    fun generateKey(algorithm: DhKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKeyPair>
    fun generateKey(algorithm: AesKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun generateKey(algorithm: HmacKeyGenParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun generateKey(algorithm: Pbkdf2Params, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: String, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: Algorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: RsaHashedImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: EcKeyImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: HmacImportParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: DhImportKeyParams, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "jwk" */, keyData: JsonWebKey, algorithm: AesKeyAlgorithm, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int8Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int16Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Int32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint8Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint16Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Uint8ClampedArray, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Float32Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: Float64Array, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: DataView, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String /* "raw" | "pkcs8" | "spki" */, keyData: ArrayBuffer, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun importKey(format: String, keyData: JsonWebKey, algorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun sign(algorithm: String, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: Algorithm, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: RsaPssParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: EcdsaParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun sign(algorithm: AesCmacParams, key: CryptoKey, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<ArrayBuffer>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int8Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int16Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Int32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint8Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint16Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Uint8ClampedArray, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Float32Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: Float64Array, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: DataView, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun unwrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, wrappedKey: ArrayBuffer, unwrappingKey: CryptoKey, unwrapAlgorithm: Any /* String | Algorithm | RsaOaepParams | AesCtrParams | AesCbcParams | AesCmacParams | AesGcmParams | AesCfbParams */, unwrappedKeyAlgorithm: Any /* String | Algorithm | RsaHashedImportParams | EcKeyImportParams | HmacImportParams | DhImportKeyParams | AesKeyAlgorithm */, extractable: Boolean, keyUsages: Array<String /* "decrypt" | "deriveBits" | "deriveKey" | "encrypt" | "sign" | "unwrapKey" | "verify" | "wrapKey" */>): PromiseLike<CryptoKey>
    fun verify(algorithm: String, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: Algorithm, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: RsaPssParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: EcdsaParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun verify(algorithm: AesCmacParams, key: CryptoKey, signature: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */, data: Any /* Int8Array | Int16Array | Int32Array | Uint8Array | Uint16Array | Uint32Array | Uint8ClampedArray | Float32Array | Float64Array | DataView | ArrayBuffer */): PromiseLike<Boolean>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: String): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: Algorithm): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: RsaOaepParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCtrParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCbcParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCmacParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesGcmParams): PromiseLike<ArrayBuffer>
    fun wrapKey(format: String /* "raw" | "pkcs8" | "spki" | "jwk" | String */, key: CryptoKey, wrappingKey: CryptoKey, wrapAlgorithm: AesCfbParams): PromiseLike<ArrayBuffer>
}

external interface SyncManager {
    fun getTags(): Promise<Array<String>>
    fun register(tag: String): Promise<Unit>
}

external interface Text : CharacterData, Slotable {
    override val assignedSlot: HTMLSlotElement?
    val wholeText: String
    fun splitText(offset: Number): Text
}

external interface TextEvent : UIEvent {
    val data: String
    fun initTextEvent(typeArg: String, canBubbleArg: Boolean, cancelableArg: Boolean, viewArg: Window, dataArg: String, inputMethod: Number, locale: String)
    val DOM_INPUT_METHOD_DROP: Number
    val DOM_INPUT_METHOD_HANDWRITING: Number
    val DOM_INPUT_METHOD_IME: Number
    val DOM_INPUT_METHOD_KEYBOARD: Number
    val DOM_INPUT_METHOD_MULTIMODAL: Number
    val DOM_INPUT_METHOD_OPTION: Number
    val DOM_INPUT_METHOD_PASTE: Number
    val DOM_INPUT_METHOD_SCRIPT: Number
    val DOM_INPUT_METHOD_UNKNOWN: Number
    val DOM_INPUT_METHOD_VOICE: Number
}

external interface TextMetrics {
    val actualBoundingBoxAscent: Number
    val actualBoundingBoxDescent: Number
    val actualBoundingBoxLeft: Number
    val actualBoundingBoxRight: Number
    val alphabeticBaseline: Number
    val emHeightAscent: Number
    val emHeightDescent: Number
    val fontBoundingBoxAscent: Number
    val fontBoundingBoxDescent: Number
    val hangingBaseline: Number
    val ideographicBaseline: Number
    val width: Number
}

external interface TextTrackEventMap {
    var cuechange: Event
}

external interface TextTrack : EventTarget {
    val activeCues: TextTrackCueList?
    val cues: TextTrackCueList?
    val id: String
    val inBandMetadataTrackDispatchType: String
    val kind: String /* "captions" | "chapters" | "descriptions" | "metadata" | "subtitles" */
    val label: String
    val language: String
    var mode: String /* "disabled" | "hidden" | "showing" */
    var oncuechange: ((self: TextTrack, ev: Event) -> Any)?
    val sourceBuffer: SourceBuffer?
    fun addCue(cue: TextTrackCue)
    fun removeCue(cue: TextTrackCue)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrack, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrack, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrack, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrack, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrack, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrack, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface TextTrackCueEventMap {
    var enter: Event
    var exit: Event
}

external interface TextTrackCue : EventTarget {
    var endTime: Number
    var id: String
    var onenter: ((self: TextTrackCue, ev: Event) -> Any)?
    var onexit: ((self: TextTrackCue, ev: Event) -> Any)?
    var pauseOnExit: Boolean
    var startTime: Number
    val track: TextTrack?
    fun <K : String> addEventListener(type: K, listener: (self: TextTrackCue, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrackCue, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrackCue, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrackCue, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrackCue, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrackCue, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
}

external interface TextTrackCueList {
    val length: Number
    fun getCueById(id: String): TextTrackCue?
    @nativeGetter
    operator fun get(index: Number): TextTrackCue?
    @nativeSetter
    operator fun set(index: Number, value: TextTrackCue)
}

external interface TextTrackListEventMap {
    var addtrack: TrackEvent
    var change: Event
    var removetrack: TrackEvent
}

external interface TextTrackList : EventTarget {
    val length: Number
    var onaddtrack: ((self: TextTrackList, ev: TrackEvent) -> Any)?
    var onchange: ((self: TextTrackList, ev: Event) -> Any)?
    var onremovetrack: ((self: TextTrackList, ev: TrackEvent) -> Any)?
    fun getTrackById(id: String): TextTrack?
    fun <K : String> addEventListener(type: K, listener: (self: TextTrackList, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrackList, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: TextTrackList, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: Boolean)
    fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject?)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListenerObject?, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrackList, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrackList, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: TextTrackList, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: Boolean)
    fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, callback: EventListenerObject?)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, callback: EventListenerObject?, options: EventListenerOptions)
    @nativeGetter
    operator fun get(index: Number): TextTrack?
    @nativeSetter
    operator fun set(index: Number, value: TextTrack)
}

external interface TimeRanges {
    val length: Number
    fun end(index: Number): Number
    fun start(index: Number): Number
}

external interface Touch {
    val altitudeAngle: Number
    val azimuthAngle: Number
    val clientX: Number
    val clientY: Number
    val force: Number
    val identifier: Number
    val pageX: Number
    val pageY: Number
    val radiusX: Number
    val radiusY: Number
    val rotationAngle: Number
    val screenX: Number
    val screenY: Number
    val target: EventTarget
    val touchType: String /* "direct" | "stylus" */
}

external interface TouchEvent : UIEvent {
    val altKey: Boolean
    val changedTouches: TouchList
    val ctrlKey: Boolean
    val metaKey: Boolean
    val shiftKey: Boolean
    val targetTouches: TouchList
    val touches: TouchList
}

external interface TouchList {
    val length: Number
    fun item(index: Number): Touch?
    @nativeGetter
    operator fun get(index: Number): Touch?
    @nativeSetter
    operator fun set(index: Number, value: Touch)
}

external interface TrackEvent : Event {
    val track: TextTrack?
}

external interface TransitionEvent : Event {
    val elapsedTime: Number
    val propertyName: String
    val pseudoElement: String
}

external interface TreeWalker {
    var currentNode: Node
    val filter: NodeFilter?
    val root: Node
    val whatToShow: Number
    fun firstChild(): Node?
    fun lastChild(): Node?
    fun nextNode(): Node?
    fun nextSibling(): Node?
    fun parentNode(): Node?
    fun previousNode(): Node?
    fun previousSibling(): Node?
}

external interface UIEvent : Event {
    val detail: Number
    val view: Window?
    val which: Number
}

external interface URL {
    var hash: String
    var host: String
    var hostname: String
    var href: String
    override fun toString(): String
    val origin: String
    var password: String
    var pathname: String
    var port: String
    var protocol: String
    var search: String
    val searchParams: URLSearchParams
    var username: String
    fun toJSON(): String
}

external interface URLSearchParams {
    fun entries(): IterableIterator<dynamic /* JsTuple<String, String> */>
    fun keys(): IterableIterator<String>
    fun values(): IterableIterator<String>
    fun append(name: String, value: String)
    fun delete(name: String)
    fun get(name: String): String?
    fun getAll(name: String): Array<String>
    fun has(name: String): Boolean
    fun set(name: String, value: String)
    fun sort()
    override fun toString(): String
    fun forEach(callbackfn: (value: String, key: String, parent: URLSearchParams) -> Unit, thisArg: Any = definedExternally)
}

external interface VRDisplay : EventTarget {
    fun requestPresent(layers: Iterable<VRLayer>): Promise<Unit>
    val capabilities: VRDisplayCapabilities
    var depthFar: Number
    var depthNear: Number
    val displayId: Number
    val displayName: String
    val isConnected: Boolean
    val isPresenting: Boolean
    val stageParameters: VRStageParameters?
    fun cancelAnimationFrame(handle: Number)
    fun exitPresent(): Promise<Unit>
    fun getEyeParameters(whichEye: String): VREyeParameters
    fun getFrameData(frameData: VRFrameData): Boolean
    fun getLayers(): Array<VRLayer>
    fun getPose(): VRPose
    fun requestAnimationFrame(callback: FrameRequestCallback): Number
    fun requestPresent(layers: Array<VRLayer>): Promise<Unit>
    fun resetPose()
    fun submitFrame(pose: VRPose = definedExternally)
}

external interface VRDisplayCapabilities {
    val canPresent: Boolean
    val hasExternalDisplay: Boolean
    val hasOrientation: Boolean
    val hasPosition: Boolean
    val maxLayers: Number
}

external interface VRDisplayEvent : Event {
    val display: VRDisplay
    val reason: String /* "mounted" | "navigation" | "requested" | "unmounted" */
}

external interface VREyeParameters {
    val fieldOfView: VRFieldOfView
    val offset: Float32Array
    val renderHeight: Number
    val renderWidth: Number
}

external interface VRFieldOfView {
    val downDegrees: Number
    val leftDegrees: Number
    val rightDegrees: Number
    val upDegrees: Number
}

external interface VRFrameData {
    val leftProjectionMatrix: Float32Array
    val leftViewMatrix: Float32Array
    val pose: VRPose
    val rightProjectionMatrix: Float32Array
    val rightViewMatrix: Float32Array
    val timestamp: Number
}

external interface VRPose {
    val angularAcceleration: Float32Array?
    val angularVelocity: Float32Array?
    val linearAcceleration: Float32Array?
    val linearVelocity: Float32Array?
    val orientation: Float32Array?
    val position: Float32Array?
    val timestamp: Number
}

external interface ValidityState {
    val badInput: Boolean
    val customError: Boolean
    val patternMismatch: Boolean
    val rangeOverflow: Boolean
    val rangeUnderflow: Boolean
    val stepMismatch: Boolean
    val tooLong: Boolean
    val tooShort: Boolean
    val typeMismatch: Boolean
    val valid: Boolean
    val valueMissing: Boolean
}

external interface VideoPlaybackQuality {
    val creationTime: Number
    val droppedVideoFrames: Number
    val totalVideoFrames: Number
}

external interface WEBGL_color_buffer_float {
    val FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE_EXT: GLenum
    val RGBA32F_EXT: GLenum
    val UNSIGNED_NORMALIZED_EXT: GLenum
}

external interface WEBGL_compressed_texture_astc {
    fun getSupportedProfiles(): Array<String>
    val COMPRESSED_RGBA_ASTC_10x10_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_10x5_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_10x6_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_10x8_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_12x10_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_12x12_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_4x4_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_5x4_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_5x5_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_6x5_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_6x6_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_8x5_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_8x6_KHR: GLenum
    val COMPRESSED_RGBA_ASTC_8x8_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_10x10_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_10x5_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_10x6_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_10x8_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_12x10_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_5x4_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_5x5_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_6x5_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_6x6_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_8x5_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_8x6_KHR: GLenum
    val COMPRESSED_SRGB8_ALPHA8_ASTC_8x8_KHR: GLenum
}

external interface WEBGL_compressed_texture_s3tc {
    val COMPRESSED_RGBA_S3TC_DXT1_EXT: GLenum
    val COMPRESSED_RGBA_S3TC_DXT3_EXT: GLenum
    val COMPRESSED_RGBA_S3TC_DXT5_EXT: GLenum
    val COMPRESSED_RGB_S3TC_DXT1_EXT: GLenum
}

external interface WEBGL_compressed_texture_s3tc_srgb {
    val COMPRESSED_SRGB_ALPHA_S3TC_DXT1_EXT: GLenum
    val COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT: GLenum
    val COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT: GLenum
    val COMPRESSED_SRGB_S3TC_DXT1_EXT: GLenum
}

external interface WEBGL_debug_renderer_info {
    val UNMASKED_RENDERER_WEBGL: GLenum
    val UNMASKED_VENDOR_WEBGL: GLenum
}

external interface WEBGL_debug_shaders {
    fun getTranslatedShaderSource(shader: WebGLShader): String
}

external interface WEBGL_depth_texture {
    val UNSIGNED_INT_24_8_WEBGL: GLenum
}

external interface WEBGL_draw_buffers {
    fun drawBuffersWEBGL(buffers: Iterable<GLenum>)
    fun drawBuffersWEBGL(buffers: Array<GLenum>)
    val COLOR_ATTACHMENT0_WEBGL: GLenum
    val COLOR_ATTACHMENT10_WEBGL: GLenum
    val COLOR_ATTACHMENT11_WEBGL: GLenum
    val COLOR_ATTACHMENT12_WEBGL: GLenum
    val COLOR_ATTACHMENT13_WEBGL: GLenum
    val COLOR_ATTACHMENT14_WEBGL: GLenum
    val COLOR_ATTACHMENT15_WEBGL: GLenum
    val COLOR_ATTACHMENT1_WEBGL: GLenum
    val COLOR_ATTACHMENT2_WEBGL: GLenum
    val COLOR_ATTACHMENT3_WEBGL: GLenum
    val COLOR_ATTACHMENT4_WEBGL: GLenum
    val COLOR_ATTACHMENT5_WEBGL: GLenum
    val COLOR_ATTACHMENT6_WEBGL: GLenum
    val COLOR_ATTACHMENT7_WEBGL: GLenum
    val COLOR_ATTACHMENT8_WEBGL: GLenum
    val COLOR_ATTACHMENT9_WEBGL: GLenum
    val DRAW_BUFFER0_WEBGL: GLenum
    val DRAW_BUFFER10_WEBGL: GLenum
    val DRAW_BUFFER11_WEBGL: GLenum
    val DRAW_BUFFER12_WEBGL: GLenum
    val DRAW_BUFFER13_WEBGL: GLenum
    val DRAW_BUFFER14_WEBGL: GLenum
    val DRAW_BUFFER15_WEBGL: GLenum
    val DRAW_BUFFER1_WEBGL: GLenum
    val DRAW_BUFFER2_WEBGL: GLenum
    val DRAW_BUFFER3_WEBGL: GLenum
    val DRAW_BUFFER4_WEBGL: GLenum
    val DRAW_BUFFER5_WEBGL: GLenum
    val DRAW_BUFFER6_WEBGL: GLenum
    val DRAW_BUFFER7_WEBGL: GLenum
    val DRAW_BUFFER8_WEBGL: GLenum
    val DRAW_BUFFER9_WEBGL: GLenum
    val MAX_COLOR_ATTACHMENTS_WEBGL: GLenum
    val MAX_DRAW_BUFFERS_WEBGL: GLenum
}

external interface WEBGL_lose_context {
    fun loseContext()
    fun restoreContext()
}

external interface WaveShaperNode : AudioNode {
    var curve: Float32Array?
    var oversample: String /* "2x" | "4x" | "none" */
}

external interface WebGL2RenderingContext : WebGL2RenderingContextBase, WebGL2RenderingContextOverloads, WebGLRenderingContextBase

external interface WebGL2RenderingContextBase {
    fun clearBufferfv(buffer: GLenum, drawbuffer: GLint, values: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun clearBufferfv(buffer: GLenum, drawbuffer: GLint, values: Iterable<GLfloat>)
    fun clearBufferiv(buffer: GLenum, drawbuffer: GLint, values: Iterable<GLint>, srcOffset: GLuint = definedExternally)
    fun clearBufferiv(buffer: GLenum, drawbuffer: GLint, values: Iterable<GLint>)
    fun clearBufferuiv(buffer: GLenum, drawbuffer: GLint, values: Iterable<GLuint>, srcOffset: GLuint = definedExternally)
    fun clearBufferuiv(buffer: GLenum, drawbuffer: GLint, values: Iterable<GLuint>)
    fun drawBuffers(buffers: Iterable<GLenum>)
    fun getActiveUniforms(program: WebGLProgram, uniformIndices: Iterable<GLuint>, pname: GLenum): Any
    fun getUniformIndices(program: WebGLProgram, uniformNames: Iterable<String>): Iterable<GLuint>?
    fun invalidateFramebuffer(target: GLenum, attachments: Iterable<GLenum>)
    fun invalidateSubFramebuffer(target: GLenum, attachments: Iterable<GLenum>, x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun transformFeedbackVaryings(program: WebGLProgram, varyings: Iterable<String>, bufferMode: GLenum)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Iterable<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun vertexAttribI4iv(index: GLuint, values: Iterable<GLint>)
    fun vertexAttribI4uiv(index: GLuint, values: Iterable<GLuint>)
    fun beginQuery(target: GLenum, query: WebGLQuery)
    fun beginTransformFeedback(primitiveMode: GLenum)
    fun bindBufferBase(target: GLenum, index: GLuint, buffer: WebGLBuffer?)
    fun bindBufferRange(target: GLenum, index: GLuint, buffer: WebGLBuffer?, offset: GLintptr, size: GLsizeiptr)
    fun bindSampler(unit: GLuint, sampler: WebGLSampler?)
    fun bindTransformFeedback(target: GLenum, tf: WebGLTransformFeedback?)
    fun bindVertexArray(array: WebGLVertexArrayObject?)
    fun blitFramebuffer(srcX0: GLint, srcY0: GLint, srcX1: GLint, srcY1: GLint, dstX0: GLint, dstY0: GLint, dstX1: GLint, dstY1: GLint, mask: GLbitfield, filter: GLenum)
    fun clearBufferfi(buffer: GLenum, drawbuffer: GLint, depth: GLfloat, stencil: GLint)
    fun clearBufferfv(buffer: GLenum, drawbuffer: GLint, values: Float32Array, srcOffset: GLuint = definedExternally)
    fun clearBufferfv(buffer: GLenum, drawbuffer: GLint, values: Float32Array)
    fun clearBufferfv(buffer: GLenum, drawbuffer: GLint, values: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun clearBufferfv(buffer: GLenum, drawbuffer: GLint, values: Array<GLfloat>)
    fun clearBufferiv(buffer: GLenum, drawbuffer: GLint, values: Int32Array, srcOffset: GLuint = definedExternally)
    fun clearBufferiv(buffer: GLenum, drawbuffer: GLint, values: Int32Array)
    fun clearBufferiv(buffer: GLenum, drawbuffer: GLint, values: Array<GLint>, srcOffset: GLuint = definedExternally)
    fun clearBufferiv(buffer: GLenum, drawbuffer: GLint, values: Array<GLint>)
    fun clearBufferuiv(buffer: GLenum, drawbuffer: GLint, values: Uint32Array, srcOffset: GLuint = definedExternally)
    fun clearBufferuiv(buffer: GLenum, drawbuffer: GLint, values: Uint32Array)
    fun clearBufferuiv(buffer: GLenum, drawbuffer: GLint, values: Array<GLuint>, srcOffset: GLuint = definedExternally)
    fun clearBufferuiv(buffer: GLenum, drawbuffer: GLint, values: Array<GLuint>)
    fun clientWaitSync(sync: WebGLSync, flags: GLbitfield, timeout: GLuint64): GLenum
    fun compressedTexImage3D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, imageSize: GLsizei, offset: GLintptr)
    fun compressedTexImage3D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally, srcLengthOverride: GLuint = definedExternally)
    fun compressedTexImage3D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, srcData: ArrayBufferView)
    fun compressedTexImage3D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally)
    fun compressedTexSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, imageSize: GLsizei, offset: GLintptr)
    fun compressedTexSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally, srcLengthOverride: GLuint = definedExternally)
    fun compressedTexSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, srcData: ArrayBufferView)
    fun compressedTexSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally)
    fun copyBufferSubData(readTarget: GLenum, writeTarget: GLenum, readOffset: GLintptr, writeOffset: GLintptr, size: GLsizeiptr)
    fun copyTexSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun createQuery(): WebGLQuery?
    fun createSampler(): WebGLSampler?
    fun createTransformFeedback(): WebGLTransformFeedback?
    fun createVertexArray(): WebGLVertexArrayObject?
    fun deleteQuery(query: WebGLQuery?)
    fun deleteSampler(sampler: WebGLSampler?)
    fun deleteSync(sync: WebGLSync?)
    fun deleteTransformFeedback(tf: WebGLTransformFeedback?)
    fun deleteVertexArray(vertexArray: WebGLVertexArrayObject?)
    fun drawArraysInstanced(mode: GLenum, first: GLint, count: GLsizei, instanceCount: GLsizei)
    fun drawBuffers(buffers: Array<GLenum>)
    fun drawElementsInstanced(mode: GLenum, count: GLsizei, type: GLenum, offset: GLintptr, instanceCount: GLsizei)
    fun drawRangeElements(mode: GLenum, start: GLuint, end: GLuint, count: GLsizei, type: GLenum, offset: GLintptr)
    fun endQuery(target: GLenum)
    fun endTransformFeedback()
    fun fenceSync(condition: GLenum, flags: GLbitfield): WebGLSync?
    fun framebufferTextureLayer(target: GLenum, attachment: GLenum, texture: WebGLTexture?, level: GLint, layer: GLint)
    fun getActiveUniformBlockName(program: WebGLProgram, uniformBlockIndex: GLuint): String?
    fun getActiveUniformBlockParameter(program: WebGLProgram, uniformBlockIndex: GLuint, pname: GLenum): Any
    fun getActiveUniforms(program: WebGLProgram, uniformIndices: Array<GLuint>, pname: GLenum): Any
    fun getBufferSubData(target: GLenum, srcByteOffset: GLintptr, dstBuffer: ArrayBufferView, dstOffset: GLuint = definedExternally, length: GLuint = definedExternally)
    fun getFragDataLocation(program: WebGLProgram, name: String): GLint
    fun getIndexedParameter(target: GLenum, index: GLuint): Any
    fun getInternalformatParameter(target: GLenum, internalformat: GLenum, pname: GLenum): Any
    fun getQuery(target: GLenum, pname: GLenum): WebGLQuery?
    fun getQueryParameter(query: WebGLQuery, pname: GLenum): Any
    fun getSamplerParameter(sampler: WebGLSampler, pname: GLenum): Any
    fun getSyncParameter(sync: WebGLSync, pname: GLenum): Any
    fun getTransformFeedbackVarying(program: WebGLProgram, index: GLuint): WebGLActiveInfo?
    fun getUniformBlockIndex(program: WebGLProgram, uniformBlockName: String): GLuint
    fun getUniformIndices(program: WebGLProgram, uniformNames: Array<String>): Array<GLuint>?
    fun invalidateFramebuffer(target: GLenum, attachments: Array<GLenum>)
    fun invalidateSubFramebuffer(target: GLenum, attachments: Array<GLenum>, x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun isQuery(query: WebGLQuery?): GLboolean
    fun isSampler(sampler: WebGLSampler?): GLboolean
    fun isSync(sync: WebGLSync?): GLboolean
    fun isTransformFeedback(tf: WebGLTransformFeedback?): GLboolean
    fun isVertexArray(vertexArray: WebGLVertexArrayObject?): GLboolean
    fun pauseTransformFeedback()
    fun readBuffer(src: GLenum)
    fun renderbufferStorageMultisample(target: GLenum, samples: GLsizei, internalformat: GLenum, width: GLsizei, height: GLsizei)
    fun resumeTransformFeedback()
    fun samplerParameterf(sampler: WebGLSampler, pname: GLenum, param: GLfloat)
    fun samplerParameteri(sampler: WebGLSampler, pname: GLenum, param: GLint)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, pboOffset: GLintptr)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, source: ImageData)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, srcData: ArrayBufferView?)
    fun texImage3D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, border: GLint, format: GLenum, type: GLenum, srcData: ArrayBufferView, srcOffset: GLuint)
    fun texStorage2D(target: GLenum, levels: GLsizei, internalformat: GLenum, width: GLsizei, height: GLsizei)
    fun texStorage3D(target: GLenum, levels: GLsizei, internalformat: GLenum, width: GLsizei, height: GLsizei, depth: GLsizei)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, pboOffset: GLintptr)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, source: ImageData)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, srcData: ArrayBufferView?, srcOffset: GLuint = definedExternally)
    fun texSubImage3D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, zoffset: GLint, width: GLsizei, height: GLsizei, depth: GLsizei, format: GLenum, type: GLenum, srcData: ArrayBufferView?)
    fun transformFeedbackVaryings(program: WebGLProgram, varyings: Array<String>, bufferMode: GLenum)
    fun uniform1ui(location: WebGLUniformLocation?, v0: GLuint)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Uint32Array)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Array<GLuint>)
    fun uniform1uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniform2ui(location: WebGLUniformLocation?, v0: GLuint, v1: GLuint)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Uint32Array)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Array<GLuint>)
    fun uniform2uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniform3ui(location: WebGLUniformLocation?, v0: GLuint, v1: GLuint, v2: GLuint)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Uint32Array)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Array<GLuint>)
    fun uniform3uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniform4ui(location: WebGLUniformLocation?, v0: GLuint, v1: GLuint, v2: GLuint, v3: GLuint)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Uint32Array)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Uint32Array, srcOffset: GLuint = definedExternally)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Array<GLuint>)
    fun uniform4uiv(location: WebGLUniformLocation?, data: Array<GLuint>, srcOffset: GLuint = definedExternally)
    fun uniformBlockBinding(program: WebGLProgram, uniformBlockIndex: GLuint, uniformBlockBinding: GLuint)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix2x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix2x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix3x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix3x4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix4x2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix4x3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun vertexAttribDivisor(index: GLuint, divisor: GLuint)
    fun vertexAttribI4i(index: GLuint, x: GLint, y: GLint, z: GLint, w: GLint)
    fun vertexAttribI4iv(index: GLuint, values: Int32Array)
    fun vertexAttribI4iv(index: GLuint, values: Array<GLint>)
    fun vertexAttribI4ui(index: GLuint, x: GLuint, y: GLuint, z: GLuint, w: GLuint)
    fun vertexAttribI4uiv(index: GLuint, values: Uint32Array)
    fun vertexAttribI4uiv(index: GLuint, values: Array<GLuint>)
    fun vertexAttribIPointer(index: GLuint, size: GLint, type: GLenum, stride: GLsizei, offset: GLintptr)
    fun waitSync(sync: WebGLSync, flags: GLbitfield, timeout: GLint64)
    val ACTIVE_UNIFORM_BLOCKS: GLenum
    val ALREADY_SIGNALED: GLenum
    val ANY_SAMPLES_PASSED: GLenum
    val ANY_SAMPLES_PASSED_CONSERVATIVE: GLenum
    val COLOR: GLenum
    val COLOR_ATTACHMENT1: GLenum
    val COLOR_ATTACHMENT10: GLenum
    val COLOR_ATTACHMENT11: GLenum
    val COLOR_ATTACHMENT12: GLenum
    val COLOR_ATTACHMENT13: GLenum
    val COLOR_ATTACHMENT14: GLenum
    val COLOR_ATTACHMENT15: GLenum
    val COLOR_ATTACHMENT2: GLenum
    val COLOR_ATTACHMENT3: GLenum
    val COLOR_ATTACHMENT4: GLenum
    val COLOR_ATTACHMENT5: GLenum
    val COLOR_ATTACHMENT6: GLenum
    val COLOR_ATTACHMENT7: GLenum
    val COLOR_ATTACHMENT8: GLenum
    val COLOR_ATTACHMENT9: GLenum
    val COMPARE_REF_TO_TEXTURE: GLenum
    val CONDITION_SATISFIED: GLenum
    val COPY_READ_BUFFER: GLenum
    val COPY_READ_BUFFER_BINDING: GLenum
    val COPY_WRITE_BUFFER: GLenum
    val COPY_WRITE_BUFFER_BINDING: GLenum
    val CURRENT_QUERY: GLenum
    val DEPTH: GLenum
    val DEPTH24_STENCIL8: GLenum
    val DEPTH32F_STENCIL8: GLenum
    val DEPTH_COMPONENT24: GLenum
    val DEPTH_COMPONENT32F: GLenum
    val DRAW_BUFFER0: GLenum
    val DRAW_BUFFER1: GLenum
    val DRAW_BUFFER10: GLenum
    val DRAW_BUFFER11: GLenum
    val DRAW_BUFFER12: GLenum
    val DRAW_BUFFER13: GLenum
    val DRAW_BUFFER14: GLenum
    val DRAW_BUFFER15: GLenum
    val DRAW_BUFFER2: GLenum
    val DRAW_BUFFER3: GLenum
    val DRAW_BUFFER4: GLenum
    val DRAW_BUFFER5: GLenum
    val DRAW_BUFFER6: GLenum
    val DRAW_BUFFER7: GLenum
    val DRAW_BUFFER8: GLenum
    val DRAW_BUFFER9: GLenum
    val DRAW_FRAMEBUFFER: GLenum
    val DRAW_FRAMEBUFFER_BINDING: GLenum
    val DYNAMIC_COPY: GLenum
    val DYNAMIC_READ: GLenum
    val FLOAT_32_UNSIGNED_INT_24_8_REV: GLenum
    val FLOAT_MAT2x3: GLenum
    val FLOAT_MAT2x4: GLenum
    val FLOAT_MAT3x2: GLenum
    val FLOAT_MAT3x4: GLenum
    val FLOAT_MAT4x2: GLenum
    val FLOAT_MAT4x3: GLenum
    val FRAGMENT_SHADER_DERIVATIVE_HINT: GLenum
    val FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE: GLenum
    val FRAMEBUFFER_ATTACHMENT_BLUE_SIZE: GLenum
    val FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING: GLenum
    val FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE: GLenum
    val FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE: GLenum
    val FRAMEBUFFER_ATTACHMENT_GREEN_SIZE: GLenum
    val FRAMEBUFFER_ATTACHMENT_RED_SIZE: GLenum
    val FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE: GLenum
    val FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER: GLenum
    val FRAMEBUFFER_DEFAULT: GLenum
    val FRAMEBUFFER_INCOMPLETE_MULTISAMPLE: GLenum
    val HALF_FLOAT: GLenum
    val INTERLEAVED_ATTRIBS: GLenum
    val INT_2_10_10_10_REV: GLenum
    val INT_SAMPLER_2D: GLenum
    val INT_SAMPLER_2D_ARRAY: GLenum
    val INT_SAMPLER_3D: GLenum
    val INT_SAMPLER_CUBE: GLenum
    val INVALID_INDEX: GLenum
    val MAX: GLenum
    val MAX_3D_TEXTURE_SIZE: GLenum
    val MAX_ARRAY_TEXTURE_LAYERS: GLenum
    val MAX_CLIENT_WAIT_TIMEOUT_WEBGL: GLenum
    val MAX_COLOR_ATTACHMENTS: GLenum
    val MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS: GLenum
    val MAX_COMBINED_UNIFORM_BLOCKS: GLenum
    val MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS: GLenum
    val MAX_DRAW_BUFFERS: GLenum
    val MAX_ELEMENTS_INDICES: GLenum
    val MAX_ELEMENTS_VERTICES: GLenum
    val MAX_ELEMENT_INDEX: GLenum
    val MAX_FRAGMENT_INPUT_COMPONENTS: GLenum
    val MAX_FRAGMENT_UNIFORM_BLOCKS: GLenum
    val MAX_FRAGMENT_UNIFORM_COMPONENTS: GLenum
    val MAX_PROGRAM_TEXEL_OFFSET: GLenum
    val MAX_SAMPLES: GLenum
    val MAX_SERVER_WAIT_TIMEOUT: GLenum
    val MAX_TEXTURE_LOD_BIAS: GLenum
    val MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS: GLenum
    val MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS: GLenum
    val MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS: GLenum
    val MAX_UNIFORM_BLOCK_SIZE: GLenum
    val MAX_UNIFORM_BUFFER_BINDINGS: GLenum
    val MAX_VARYING_COMPONENTS: GLenum
    val MAX_VERTEX_OUTPUT_COMPONENTS: GLenum
    val MAX_VERTEX_UNIFORM_BLOCKS: GLenum
    val MAX_VERTEX_UNIFORM_COMPONENTS: GLenum
    val MIN: GLenum
    val MIN_PROGRAM_TEXEL_OFFSET: GLenum
    val OBJECT_TYPE: GLenum
    val PACK_ROW_LENGTH: GLenum
    val PACK_SKIP_PIXELS: GLenum
    val PACK_SKIP_ROWS: GLenum
    val PIXEL_PACK_BUFFER: GLenum
    val PIXEL_PACK_BUFFER_BINDING: GLenum
    val PIXEL_UNPACK_BUFFER: GLenum
    val PIXEL_UNPACK_BUFFER_BINDING: GLenum
    val QUERY_RESULT: GLenum
    val QUERY_RESULT_AVAILABLE: GLenum
    val R11F_G11F_B10F: GLenum
    val R16F: GLenum
    val R16I: GLenum
    val R16UI: GLenum
    val R32F: GLenum
    val R32I: GLenum
    val R32UI: GLenum
    val R8: GLenum
    val R8I: GLenum
    val R8UI: GLenum
    val R8_SNORM: GLenum
    val RASTERIZER_DISCARD: GLenum
    val READ_BUFFER: GLenum
    val READ_FRAMEBUFFER: GLenum
    val READ_FRAMEBUFFER_BINDING: GLenum
    val RED: GLenum
    val RED_INTEGER: GLenum
    val RENDERBUFFER_SAMPLES: GLenum
    val RG: GLenum
    val RG16F: GLenum
    val RG16I: GLenum
    val RG16UI: GLenum
    val RG32F: GLenum
    val RG32I: GLenum
    val RG32UI: GLenum
    val RG8: GLenum
    val RG8I: GLenum
    val RG8UI: GLenum
    val RG8_SNORM: GLenum
    val RGB10_A2: GLenum
    val RGB10_A2UI: GLenum
    val RGB16F: GLenum
    val RGB16I: GLenum
    val RGB16UI: GLenum
    val RGB32F: GLenum
    val RGB32I: GLenum
    val RGB32UI: GLenum
    val RGB8: GLenum
    val RGB8I: GLenum
    val RGB8UI: GLenum
    val RGB8_SNORM: GLenum
    val RGB9_E5: GLenum
    val RGBA16F: GLenum
    val RGBA16I: GLenum
    val RGBA16UI: GLenum
    val RGBA32F: GLenum
    val RGBA32I: GLenum
    val RGBA32UI: GLenum
    val RGBA8: GLenum
    val RGBA8I: GLenum
    val RGBA8UI: GLenum
    val RGBA8_SNORM: GLenum
    val RGBA_INTEGER: GLenum
    val RGB_INTEGER: GLenum
    val RG_INTEGER: GLenum
    val SAMPLER_2D_ARRAY: GLenum
    val SAMPLER_2D_ARRAY_SHADOW: GLenum
    val SAMPLER_2D_SHADOW: GLenum
    val SAMPLER_3D: GLenum
    val SAMPLER_BINDING: GLenum
    val SAMPLER_CUBE_SHADOW: GLenum
    val SEPARATE_ATTRIBS: GLenum
    val SIGNALED: GLenum
    val SIGNED_NORMALIZED: GLenum
    val SRGB: GLenum
    val SRGB8: GLenum
    val SRGB8_ALPHA8: GLenum
    val STATIC_COPY: GLenum
    val STATIC_READ: GLenum
    val STENCIL: GLenum
    val STREAM_COPY: GLenum
    val STREAM_READ: GLenum
    val SYNC_CONDITION: GLenum
    val SYNC_FENCE: GLenum
    val SYNC_FLAGS: GLenum
    val SYNC_FLUSH_COMMANDS_BIT: GLenum
    val SYNC_GPU_COMMANDS_COMPLETE: GLenum
    val SYNC_STATUS: GLenum
    val TEXTURE_2D_ARRAY: GLenum
    val TEXTURE_3D: GLenum
    val TEXTURE_BASE_LEVEL: GLenum
    val TEXTURE_BINDING_2D_ARRAY: GLenum
    val TEXTURE_BINDING_3D: GLenum
    val TEXTURE_COMPARE_FUNC: GLenum
    val TEXTURE_COMPARE_MODE: GLenum
    val TEXTURE_IMMUTABLE_FORMAT: GLenum
    val TEXTURE_IMMUTABLE_LEVELS: GLenum
    val TEXTURE_MAX_LEVEL: GLenum
    val TEXTURE_MAX_LOD: GLenum
    val TEXTURE_MIN_LOD: GLenum
    val TEXTURE_WRAP_R: GLenum
    val TIMEOUT_EXPIRED: GLenum
    val TIMEOUT_IGNORED: GLint64
    val TRANSFORM_FEEDBACK: GLenum
    val TRANSFORM_FEEDBACK_ACTIVE: GLenum
    val TRANSFORM_FEEDBACK_BINDING: GLenum
    val TRANSFORM_FEEDBACK_BUFFER: GLenum
    val TRANSFORM_FEEDBACK_BUFFER_BINDING: GLenum
    val TRANSFORM_FEEDBACK_BUFFER_MODE: GLenum
    val TRANSFORM_FEEDBACK_BUFFER_SIZE: GLenum
    val TRANSFORM_FEEDBACK_BUFFER_START: GLenum
    val TRANSFORM_FEEDBACK_PAUSED: GLenum
    val TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN: GLenum
    val TRANSFORM_FEEDBACK_VARYINGS: GLenum
    val UNIFORM_ARRAY_STRIDE: GLenum
    val UNIFORM_BLOCK_ACTIVE_UNIFORMS: GLenum
    val UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES: GLenum
    val UNIFORM_BLOCK_BINDING: GLenum
    val UNIFORM_BLOCK_DATA_SIZE: GLenum
    val UNIFORM_BLOCK_INDEX: GLenum
    val UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER: GLenum
    val UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER: GLenum
    val UNIFORM_BUFFER: GLenum
    val UNIFORM_BUFFER_BINDING: GLenum
    val UNIFORM_BUFFER_OFFSET_ALIGNMENT: GLenum
    val UNIFORM_BUFFER_SIZE: GLenum
    val UNIFORM_BUFFER_START: GLenum
    val UNIFORM_IS_ROW_MAJOR: GLenum
    val UNIFORM_MATRIX_STRIDE: GLenum
    val UNIFORM_OFFSET: GLenum
    val UNIFORM_SIZE: GLenum
    val UNIFORM_TYPE: GLenum
    val UNPACK_IMAGE_HEIGHT: GLenum
    val UNPACK_ROW_LENGTH: GLenum
    val UNPACK_SKIP_IMAGES: GLenum
    val UNPACK_SKIP_PIXELS: GLenum
    val UNPACK_SKIP_ROWS: GLenum
    val UNSIGNALED: GLenum
    val UNSIGNED_INT_10F_11F_11F_REV: GLenum
    val UNSIGNED_INT_24_8: GLenum
    val UNSIGNED_INT_2_10_10_10_REV: GLenum
    val UNSIGNED_INT_5_9_9_9_REV: GLenum
    val UNSIGNED_INT_SAMPLER_2D: GLenum
    val UNSIGNED_INT_SAMPLER_2D_ARRAY: GLenum
    val UNSIGNED_INT_SAMPLER_3D: GLenum
    val UNSIGNED_INT_SAMPLER_CUBE: GLenum
    val UNSIGNED_INT_VEC2: GLenum
    val UNSIGNED_INT_VEC3: GLenum
    val UNSIGNED_INT_VEC4: GLenum
    val UNSIGNED_NORMALIZED: GLenum
    val VERTEX_ARRAY_BINDING: GLenum
    val VERTEX_ATTRIB_ARRAY_DIVISOR: GLenum
    val VERTEX_ATTRIB_ARRAY_INTEGER: GLenum
    val WAIT_FAILED: GLenum
}

external interface WebGL2RenderingContextOverloads {
    fun uniform1fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>)
    fun uniform1fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform1iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1iv(location: WebGLUniformLocation?, data: Iterable<GLint>)
    fun uniform1iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally)
    fun uniform2fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>)
    fun uniform2fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform2iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2iv(location: WebGLUniformLocation?, data: Iterable<GLint>)
    fun uniform2iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally)
    fun uniform3fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>)
    fun uniform3fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform3iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3iv(location: WebGLUniformLocation?, data: Iterable<GLint>)
    fun uniform3iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally)
    fun uniform4fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>)
    fun uniform4fv(location: WebGLUniformLocation?, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform4iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4iv(location: WebGLUniformLocation?, data: Iterable<GLint>)
    fun uniform4iv(location: WebGLUniformLocation?, data: Iterable<GLint>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Iterable<GLfloat>, srcOffset: GLuint = definedExternally)
    fun bufferData(target: GLenum, size: GLsizeiptr, usage: GLenum)
    fun bufferData(target: GLenum, srcData: ArrayBufferView?, usage: GLenum)
    fun bufferData(target: GLenum, srcData: ArrayBuffer?, usage: GLenum)
    fun bufferData(target: GLenum, srcData: ArrayBufferView, usage: GLenum, srcOffset: GLuint, length: GLuint = definedExternally)
    fun bufferData(target: GLenum, srcData: ArrayBufferView, usage: GLenum, srcOffset: GLuint)
    fun bufferSubData(target: GLenum, dstByteOffset: GLintptr, srcData: ArrayBufferView)
    fun bufferSubData(target: GLenum, dstByteOffset: GLintptr, srcData: ArrayBuffer)
    fun bufferSubData(target: GLenum, dstByteOffset: GLintptr, srcData: ArrayBufferView, srcOffset: GLuint, length: GLuint = definedExternally)
    fun bufferSubData(target: GLenum, dstByteOffset: GLintptr, srcData: ArrayBufferView, srcOffset: GLuint)
    fun compressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, imageSize: GLsizei, offset: GLintptr)
    fun compressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally, srcLengthOverride: GLuint = definedExternally)
    fun compressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, srcData: ArrayBufferView)
    fun compressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally)
    fun compressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, imageSize: GLsizei, offset: GLintptr)
    fun compressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally, srcLengthOverride: GLuint = definedExternally)
    fun compressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, srcData: ArrayBufferView)
    fun compressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, srcData: ArrayBufferView, srcOffset: GLuint = definedExternally)
    fun readPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, dstData: ArrayBufferView?)
    fun readPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, offset: GLintptr)
    fun readPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, dstData: ArrayBufferView, dstOffset: GLuint)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, pixels: ArrayBufferView?)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: ImageData)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, pboOffset: GLintptr)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, source: ImageData)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, srcData: ArrayBufferView, srcOffset: GLuint)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: ArrayBufferView?)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: ImageData)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pboOffset: GLintptr)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, source: ImageData)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, srcData: ArrayBufferView, srcOffset: GLuint)
    fun uniform1fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1fv(location: WebGLUniformLocation?, data: Float32Array)
    fun uniform1fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniform1fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1fv(location: WebGLUniformLocation?, data: Array<GLfloat>)
    fun uniform1fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform1iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1iv(location: WebGLUniformLocation?, data: Int32Array)
    fun uniform1iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally)
    fun uniform1iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform1iv(location: WebGLUniformLocation?, data: Array<GLint>)
    fun uniform1iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally)
    fun uniform2fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2fv(location: WebGLUniformLocation?, data: Float32Array)
    fun uniform2fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniform2fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2fv(location: WebGLUniformLocation?, data: Array<GLfloat>)
    fun uniform2fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform2iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2iv(location: WebGLUniformLocation?, data: Int32Array)
    fun uniform2iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally)
    fun uniform2iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform2iv(location: WebGLUniformLocation?, data: Array<GLint>)
    fun uniform2iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally)
    fun uniform3fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3fv(location: WebGLUniformLocation?, data: Float32Array)
    fun uniform3fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniform3fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3fv(location: WebGLUniformLocation?, data: Array<GLfloat>)
    fun uniform3fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform3iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3iv(location: WebGLUniformLocation?, data: Int32Array)
    fun uniform3iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally)
    fun uniform3iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform3iv(location: WebGLUniformLocation?, data: Array<GLint>)
    fun uniform3iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally)
    fun uniform4fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4fv(location: WebGLUniformLocation?, data: Float32Array)
    fun uniform4fv(location: WebGLUniformLocation?, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniform4fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4fv(location: WebGLUniformLocation?, data: Array<GLfloat>)
    fun uniform4fv(location: WebGLUniformLocation?, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniform4iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4iv(location: WebGLUniformLocation?, data: Int32Array)
    fun uniform4iv(location: WebGLUniformLocation?, data: Int32Array, srcOffset: GLuint = definedExternally)
    fun uniform4iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniform4iv(location: WebGLUniformLocation?, data: Array<GLint>)
    fun uniform4iv(location: WebGLUniformLocation?, data: Array<GLint>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Float32Array, srcOffset: GLuint = definedExternally)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally, srcLength: GLuint = definedExternally)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, data: Array<GLfloat>, srcOffset: GLuint = definedExternally)
}

external interface WebGLActiveInfo {
    val name: String
    val size: GLint
    val type: GLenum
}

external interface WebGLBuffer : WebGLObject

external interface WebGLContextEvent : Event {
    val statusMessage: String
}

external interface WebGLFramebuffer : WebGLObject

external interface WebGLObject

external interface WebGLProgram : WebGLObject

external interface WebGLQuery : WebGLObject

external interface WebGLRenderbuffer : WebGLObject

external interface WebGLRenderingContext : WebGLRenderingContextBase, WebGLRenderingContextOverloads

external interface WebGLRenderingContextBase {
    fun getExtension(extensionName: String /* "OCULUS_multiview" | "EXT_blend_minmax" | "EXT_texture_filter_anisotropic" | "EXT_frag_depth" | "EXT_shader_texture_lod" | "EXT_sRGB" | "OES_vertex_array_object" | "WEBGL_color_buffer_float" | "WEBGL_compressed_texture_astc" | "WEBGL_compressed_texture_s3tc_srgb" | "WEBGL_debug_shaders" | "WEBGL_draw_buffers" | "WEBGL_lose_context" | "WEBGL_depth_texture" | "WEBGL_debug_renderer_info" | "WEBGL_compressed_texture_s3tc" | "OES_texture_half_float_linear" | "OES_texture_half_float" | "OES_texture_float_linear" | "OES_texture_float" | "OES_standard_derivatives" | "OES_element_index_uint" | "ANGLE_instanced_arrays" */): dynamic /* Any */
    fun makeXRCompatible(): Promise<Unit>
    fun vertexAttrib1fv(index: GLuint, values: Iterable<GLfloat>)
    fun vertexAttrib2fv(index: GLuint, values: Iterable<GLfloat>)
    fun vertexAttrib3fv(index: GLuint, values: Iterable<GLfloat>)
    fun vertexAttrib4fv(index: GLuint, values: Iterable<GLfloat>)
    val canvas: dynamic /* HTMLCanvasElement | OffscreenCanvas */
        get() = definedExternally
    val drawingBufferHeight: GLsizei
    val drawingBufferWidth: GLsizei
    fun activeTexture(texture: GLenum)
    fun attachShader(program: WebGLProgram, shader: WebGLShader)
    fun bindAttribLocation(program: WebGLProgram, index: GLuint, name: String)
    fun bindBuffer(target: GLenum, buffer: WebGLBuffer?)
    fun bindFramebuffer(target: GLenum, framebuffer: WebGLFramebuffer?)
    fun bindRenderbuffer(target: GLenum, renderbuffer: WebGLRenderbuffer?)
    fun bindTexture(target: GLenum, texture: WebGLTexture?)
    fun blendColor(red: GLclampf, green: GLclampf, blue: GLclampf, alpha: GLclampf)
    fun blendEquation(mode: GLenum)
    fun blendEquationSeparate(modeRGB: GLenum, modeAlpha: GLenum)
    fun blendFunc(sfactor: GLenum, dfactor: GLenum)
    fun blendFuncSeparate(srcRGB: GLenum, dstRGB: GLenum, srcAlpha: GLenum, dstAlpha: GLenum)
    fun checkFramebufferStatus(target: GLenum): GLenum
    fun clear(mask: GLbitfield)
    fun clearColor(red: GLclampf, green: GLclampf, blue: GLclampf, alpha: GLclampf)
    fun clearDepth(depth: GLclampf)
    fun clearStencil(s: GLint)
    fun colorMask(red: GLboolean, green: GLboolean, blue: GLboolean, alpha: GLboolean)
    fun compileShader(shader: WebGLShader)
    fun copyTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, x: GLint, y: GLint, width: GLsizei, height: GLsizei, border: GLint)
    fun copyTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun createBuffer(): WebGLBuffer?
    fun createFramebuffer(): WebGLFramebuffer?
    fun createProgram(): WebGLProgram?
    fun createRenderbuffer(): WebGLRenderbuffer?
    fun createShader(type: GLenum): WebGLShader?
    fun createTexture(): WebGLTexture?
    fun cullFace(mode: GLenum)
    fun deleteBuffer(buffer: WebGLBuffer?)
    fun deleteFramebuffer(framebuffer: WebGLFramebuffer?)
    fun deleteProgram(program: WebGLProgram?)
    fun deleteRenderbuffer(renderbuffer: WebGLRenderbuffer?)
    fun deleteShader(shader: WebGLShader?)
    fun deleteTexture(texture: WebGLTexture?)
    fun depthFunc(func: GLenum)
    fun depthMask(flag: GLboolean)
    fun depthRange(zNear: GLclampf, zFar: GLclampf)
    fun detachShader(program: WebGLProgram, shader: WebGLShader)
    fun disable(cap: GLenum)
    fun disableVertexAttribArray(index: GLuint)
    fun drawArrays(mode: GLenum, first: GLint, count: GLsizei)
    fun drawElements(mode: GLenum, count: GLsizei, type: GLenum, offset: GLintptr)
    fun enable(cap: GLenum)
    fun enableVertexAttribArray(index: GLuint)
    fun finish()
    fun flush()
    fun framebufferRenderbuffer(target: GLenum, attachment: GLenum, renderbuffertarget: GLenum, renderbuffer: WebGLRenderbuffer?)
    fun framebufferTexture2D(target: GLenum, attachment: GLenum, textarget: GLenum, texture: WebGLTexture?, level: GLint)
    fun frontFace(mode: GLenum)
    fun generateMipmap(target: GLenum)
    fun getActiveAttrib(program: WebGLProgram, index: GLuint): WebGLActiveInfo?
    fun getActiveUniform(program: WebGLProgram, index: GLuint): WebGLActiveInfo?
    fun getAttachedShaders(program: WebGLProgram): Array<WebGLShader>?
    fun getAttribLocation(program: WebGLProgram, name: String): GLint
    fun getBufferParameter(target: GLenum, pname: GLenum): Any
    fun getContextAttributes(): WebGLContextAttributes?
    fun getError(): GLenum
    fun getFramebufferAttachmentParameter(target: GLenum, attachment: GLenum, pname: GLenum): Any
    fun getParameter(pname: GLenum): Any
    fun getProgramInfoLog(program: WebGLProgram): String?
    fun getProgramParameter(program: WebGLProgram, pname: GLenum): Any
    fun getRenderbufferParameter(target: GLenum, pname: GLenum): Any
    fun getShaderInfoLog(shader: WebGLShader): String?
    fun getShaderParameter(shader: WebGLShader, pname: GLenum): Any
    fun getShaderPrecisionFormat(shadertype: GLenum, precisiontype: GLenum): WebGLShaderPrecisionFormat?
    fun getShaderSource(shader: WebGLShader): String?
    fun getSupportedExtensions(): Array<String>?
    fun getTexParameter(target: GLenum, pname: GLenum): Any
    fun getUniform(program: WebGLProgram, location: WebGLUniformLocation): Any
    fun getUniformLocation(program: WebGLProgram, name: String): WebGLUniformLocation?
    fun getVertexAttrib(index: GLuint, pname: GLenum): Any
    fun getVertexAttribOffset(index: GLuint, pname: GLenum): GLintptr
    fun hint(target: GLenum, mode: GLenum)
    fun isBuffer(buffer: WebGLBuffer?): GLboolean
    fun isContextLost(): Boolean
    fun isEnabled(cap: GLenum): GLboolean
    fun isFramebuffer(framebuffer: WebGLFramebuffer?): GLboolean
    fun isProgram(program: WebGLProgram?): GLboolean
    fun isRenderbuffer(renderbuffer: WebGLRenderbuffer?): GLboolean
    fun isShader(shader: WebGLShader?): GLboolean
    fun isTexture(texture: WebGLTexture?): GLboolean
    fun lineWidth(width: GLfloat)
    fun linkProgram(program: WebGLProgram)
    fun pixelStorei(pname: GLenum, param: GLint)
    fun pixelStorei(pname: GLenum, param: GLboolean)
    fun polygonOffset(factor: GLfloat, units: GLfloat)
    fun renderbufferStorage(target: GLenum, internalformat: GLenum, width: GLsizei, height: GLsizei)
    fun sampleCoverage(value: GLclampf, invert: GLboolean)
    fun scissor(x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun shaderSource(shader: WebGLShader, source: String)
    fun stencilFunc(func: GLenum, ref: GLint, mask: GLuint)
    fun stencilFuncSeparate(face: GLenum, func: GLenum, ref: GLint, mask: GLuint)
    fun stencilMask(mask: GLuint)
    fun stencilMaskSeparate(face: GLenum, mask: GLuint)
    fun stencilOp(fail: GLenum, zfail: GLenum, zpass: GLenum)
    fun stencilOpSeparate(face: GLenum, fail: GLenum, zfail: GLenum, zpass: GLenum)
    fun texParameterf(target: GLenum, pname: GLenum, param: GLfloat)
    fun texParameteri(target: GLenum, pname: GLenum, param: GLint)
    fun uniform1f(location: WebGLUniformLocation?, x: GLfloat)
    fun uniform1i(location: WebGLUniformLocation?, x: GLint)
    fun uniform2f(location: WebGLUniformLocation?, x: GLfloat, y: GLfloat)
    fun uniform2i(location: WebGLUniformLocation?, x: GLint, y: GLint)
    fun uniform3f(location: WebGLUniformLocation?, x: GLfloat, y: GLfloat, z: GLfloat)
    fun uniform3i(location: WebGLUniformLocation?, x: GLint, y: GLint, z: GLint)
    fun uniform4f(location: WebGLUniformLocation?, x: GLfloat, y: GLfloat, z: GLfloat, w: GLfloat)
    fun uniform4i(location: WebGLUniformLocation?, x: GLint, y: GLint, z: GLint, w: GLint)
    fun useProgram(program: WebGLProgram?)
    fun validateProgram(program: WebGLProgram)
    fun vertexAttrib1f(index: GLuint, x: GLfloat)
    fun vertexAttrib1fv(index: GLuint, values: Float32Array)
    fun vertexAttrib1fv(index: GLuint, values: Array<GLfloat>)
    fun vertexAttrib2f(index: GLuint, x: GLfloat, y: GLfloat)
    fun vertexAttrib2fv(index: GLuint, values: Float32Array)
    fun vertexAttrib2fv(index: GLuint, values: Array<GLfloat>)
    fun vertexAttrib3f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat)
    fun vertexAttrib3fv(index: GLuint, values: Float32Array)
    fun vertexAttrib3fv(index: GLuint, values: Array<GLfloat>)
    fun vertexAttrib4f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat, w: GLfloat)
    fun vertexAttrib4fv(index: GLuint, values: Float32Array)
    fun vertexAttrib4fv(index: GLuint, values: Array<GLfloat>)
    fun vertexAttribPointer(index: GLuint, size: GLint, type: GLenum, normalized: GLboolean, stride: GLsizei, offset: GLintptr)
    fun viewport(x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    val ACTIVE_ATTRIBUTES: GLenum
    val ACTIVE_TEXTURE: GLenum
    val ACTIVE_UNIFORMS: GLenum
    val ALIASED_LINE_WIDTH_RANGE: GLenum
    val ALIASED_POINT_SIZE_RANGE: GLenum
    val ALPHA: GLenum
    val ALPHA_BITS: GLenum
    val ALWAYS: GLenum
    val ARRAY_BUFFER: GLenum
    val ARRAY_BUFFER_BINDING: GLenum
    val ATTACHED_SHADERS: GLenum
    val BACK: GLenum
    val BLEND: GLenum
    val BLEND_COLOR: GLenum
    val BLEND_DST_ALPHA: GLenum
    val BLEND_DST_RGB: GLenum
    val BLEND_EQUATION: GLenum
    val BLEND_EQUATION_ALPHA: GLenum
    val BLEND_EQUATION_RGB: GLenum
    val BLEND_SRC_ALPHA: GLenum
    val BLEND_SRC_RGB: GLenum
    val BLUE_BITS: GLenum
    val BOOL: GLenum
    val BOOL_VEC2: GLenum
    val BOOL_VEC3: GLenum
    val BOOL_VEC4: GLenum
    val BROWSER_DEFAULT_WEBGL: GLenum
    val BUFFER_SIZE: GLenum
    val BUFFER_USAGE: GLenum
    val BYTE: GLenum
    val CCW: GLenum
    val CLAMP_TO_EDGE: GLenum
    val COLOR_ATTACHMENT0: GLenum
    val COLOR_BUFFER_BIT: GLenum
    val COLOR_CLEAR_VALUE: GLenum
    val COLOR_WRITEMASK: GLenum
    val COMPILE_STATUS: GLenum
    val COMPRESSED_TEXTURE_FORMATS: GLenum
    val CONSTANT_ALPHA: GLenum
    val CONSTANT_COLOR: GLenum
    val CONTEXT_LOST_WEBGL: GLenum
    val CULL_FACE: GLenum
    val CULL_FACE_MODE: GLenum
    val CURRENT_PROGRAM: GLenum
    val CURRENT_VERTEX_ATTRIB: GLenum
    val CW: GLenum
    val DECR: GLenum
    val DECR_WRAP: GLenum
    val DELETE_STATUS: GLenum
    val DEPTH_ATTACHMENT: GLenum
    val DEPTH_BITS: GLenum
    val DEPTH_BUFFER_BIT: GLenum
    val DEPTH_CLEAR_VALUE: GLenum
    val DEPTH_COMPONENT: GLenum
    val DEPTH_COMPONENT16: GLenum
    val DEPTH_FUNC: GLenum
    val DEPTH_RANGE: GLenum
    val DEPTH_STENCIL: GLenum
    val DEPTH_STENCIL_ATTACHMENT: GLenum
    val DEPTH_TEST: GLenum
    val DEPTH_WRITEMASK: GLenum
    val DITHER: GLenum
    val DONT_CARE: GLenum
    val DST_ALPHA: GLenum
    val DST_COLOR: GLenum
    val DYNAMIC_DRAW: GLenum
    val ELEMENT_ARRAY_BUFFER: GLenum
    val ELEMENT_ARRAY_BUFFER_BINDING: GLenum
    val EQUAL: GLenum
    val FASTEST: GLenum
    val FLOAT: GLenum
    val FLOAT_MAT2: GLenum
    val FLOAT_MAT3: GLenum
    val FLOAT_MAT4: GLenum
    val FLOAT_VEC2: GLenum
    val FLOAT_VEC3: GLenum
    val FLOAT_VEC4: GLenum
    val FRAGMENT_SHADER: GLenum
    val FRAMEBUFFER: GLenum
    val FRAMEBUFFER_ATTACHMENT_OBJECT_NAME: GLenum
    val FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE: GLenum
    val FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE: GLenum
    val FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL: GLenum
    val FRAMEBUFFER_BINDING: GLenum
    val FRAMEBUFFER_COMPLETE: GLenum
    val FRAMEBUFFER_INCOMPLETE_ATTACHMENT: GLenum
    val FRAMEBUFFER_INCOMPLETE_DIMENSIONS: GLenum
    val FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: GLenum
    val FRAMEBUFFER_UNSUPPORTED: GLenum
    val FRONT: GLenum
    val FRONT_AND_BACK: GLenum
    val FRONT_FACE: GLenum
    val FUNC_ADD: GLenum
    val FUNC_REVERSE_SUBTRACT: GLenum
    val FUNC_SUBTRACT: GLenum
    val GENERATE_MIPMAP_HINT: GLenum
    val GEQUAL: GLenum
    val GREATER: GLenum
    val GREEN_BITS: GLenum
    val HIGH_FLOAT: GLenum
    val HIGH_INT: GLenum
    val IMPLEMENTATION_COLOR_READ_FORMAT: GLenum
    val IMPLEMENTATION_COLOR_READ_TYPE: GLenum
    val INCR: GLenum
    val INCR_WRAP: GLenum
    val INT: GLenum
    val INT_VEC2: GLenum
    val INT_VEC3: GLenum
    val INT_VEC4: GLenum
    val INVALID_ENUM: GLenum
    val INVALID_FRAMEBUFFER_OPERATION: GLenum
    val INVALID_OPERATION: GLenum
    val INVALID_VALUE: GLenum
    val INVERT: GLenum
    val KEEP: GLenum
    val LEQUAL: GLenum
    val LESS: GLenum
    val LINEAR: GLenum
    val LINEAR_MIPMAP_LINEAR: GLenum
    val LINEAR_MIPMAP_NEAREST: GLenum
    val LINES: GLenum
    val LINE_LOOP: GLenum
    val LINE_STRIP: GLenum
    val LINE_WIDTH: GLenum
    val LINK_STATUS: GLenum
    val LOW_FLOAT: GLenum
    val LOW_INT: GLenum
    val LUMINANCE: GLenum
    val LUMINANCE_ALPHA: GLenum
    val MAX_COMBINED_TEXTURE_IMAGE_UNITS: GLenum
    val MAX_CUBE_MAP_TEXTURE_SIZE: GLenum
    val MAX_FRAGMENT_UNIFORM_VECTORS: GLenum
    val MAX_RENDERBUFFER_SIZE: GLenum
    val MAX_TEXTURE_IMAGE_UNITS: GLenum
    val MAX_TEXTURE_SIZE: GLenum
    val MAX_VARYING_VECTORS: GLenum
    val MAX_VERTEX_ATTRIBS: GLenum
    val MAX_VERTEX_TEXTURE_IMAGE_UNITS: GLenum
    val MAX_VERTEX_UNIFORM_VECTORS: GLenum
    val MAX_VIEWPORT_DIMS: GLenum
    val MEDIUM_FLOAT: GLenum
    val MEDIUM_INT: GLenum
    val MIRRORED_REPEAT: GLenum
    val NEAREST: GLenum
    val NEAREST_MIPMAP_LINEAR: GLenum
    val NEAREST_MIPMAP_NEAREST: GLenum
    val NEVER: GLenum
    val NICEST: GLenum
    val NONE: GLenum
    val NOTEQUAL: GLenum
    val NO_ERROR: GLenum
    val ONE: GLenum
    val ONE_MINUS_CONSTANT_ALPHA: GLenum
    val ONE_MINUS_CONSTANT_COLOR: GLenum
    val ONE_MINUS_DST_ALPHA: GLenum
    val ONE_MINUS_DST_COLOR: GLenum
    val ONE_MINUS_SRC_ALPHA: GLenum
    val ONE_MINUS_SRC_COLOR: GLenum
    val OUT_OF_MEMORY: GLenum
    val PACK_ALIGNMENT: GLenum
    val POINTS: GLenum
    val POLYGON_OFFSET_FACTOR: GLenum
    val POLYGON_OFFSET_FILL: GLenum
    val POLYGON_OFFSET_UNITS: GLenum
    val RED_BITS: GLenum
    val RENDERBUFFER: GLenum
    val RENDERBUFFER_ALPHA_SIZE: GLenum
    val RENDERBUFFER_BINDING: GLenum
    val RENDERBUFFER_BLUE_SIZE: GLenum
    val RENDERBUFFER_DEPTH_SIZE: GLenum
    val RENDERBUFFER_GREEN_SIZE: GLenum
    val RENDERBUFFER_HEIGHT: GLenum
    val RENDERBUFFER_INTERNAL_FORMAT: GLenum
    val RENDERBUFFER_RED_SIZE: GLenum
    val RENDERBUFFER_STENCIL_SIZE: GLenum
    val RENDERBUFFER_WIDTH: GLenum
    val RENDERER: GLenum
    val REPEAT: GLenum
    val REPLACE: GLenum
    val RGB: GLenum
    val RGB565: GLenum
    val RGB5_A1: GLenum
    val RGBA: GLenum
    val RGBA4: GLenum
    val SAMPLER_2D: GLenum
    val SAMPLER_CUBE: GLenum
    val SAMPLES: GLenum
    val SAMPLE_ALPHA_TO_COVERAGE: GLenum
    val SAMPLE_BUFFERS: GLenum
    val SAMPLE_COVERAGE: GLenum
    val SAMPLE_COVERAGE_INVERT: GLenum
    val SAMPLE_COVERAGE_VALUE: GLenum
    val SCISSOR_BOX: GLenum
    val SCISSOR_TEST: GLenum
    val SHADER_TYPE: GLenum
    val SHADING_LANGUAGE_VERSION: GLenum
    val SHORT: GLenum
    val SRC_ALPHA: GLenum
    val SRC_ALPHA_SATURATE: GLenum
    val SRC_COLOR: GLenum
    val STATIC_DRAW: GLenum
    val STENCIL_ATTACHMENT: GLenum
    val STENCIL_BACK_FAIL: GLenum
    val STENCIL_BACK_FUNC: GLenum
    val STENCIL_BACK_PASS_DEPTH_FAIL: GLenum
    val STENCIL_BACK_PASS_DEPTH_PASS: GLenum
    val STENCIL_BACK_REF: GLenum
    val STENCIL_BACK_VALUE_MASK: GLenum
    val STENCIL_BACK_WRITEMASK: GLenum
    val STENCIL_BITS: GLenum
    val STENCIL_BUFFER_BIT: GLenum
    val STENCIL_CLEAR_VALUE: GLenum
    val STENCIL_FAIL: GLenum
    val STENCIL_FUNC: GLenum
    val STENCIL_INDEX8: GLenum
    val STENCIL_PASS_DEPTH_FAIL: GLenum
    val STENCIL_PASS_DEPTH_PASS: GLenum
    val STENCIL_REF: GLenum
    val STENCIL_TEST: GLenum
    val STENCIL_VALUE_MASK: GLenum
    val STENCIL_WRITEMASK: GLenum
    val STREAM_DRAW: GLenum
    val SUBPIXEL_BITS: GLenum
    val TEXTURE: GLenum
    val TEXTURE0: GLenum
    val TEXTURE1: GLenum
    val TEXTURE10: GLenum
    val TEXTURE11: GLenum
    val TEXTURE12: GLenum
    val TEXTURE13: GLenum
    val TEXTURE14: GLenum
    val TEXTURE15: GLenum
    val TEXTURE16: GLenum
    val TEXTURE17: GLenum
    val TEXTURE18: GLenum
    val TEXTURE19: GLenum
    val TEXTURE2: GLenum
    val TEXTURE20: GLenum
    val TEXTURE21: GLenum
    val TEXTURE22: GLenum
    val TEXTURE23: GLenum
    val TEXTURE24: GLenum
    val TEXTURE25: GLenum
    val TEXTURE26: GLenum
    val TEXTURE27: GLenum
    val TEXTURE28: GLenum
    val TEXTURE29: GLenum
    val TEXTURE3: GLenum
    val TEXTURE30: GLenum
    val TEXTURE31: GLenum
    val TEXTURE4: GLenum
    val TEXTURE5: GLenum
    val TEXTURE6: GLenum
    val TEXTURE7: GLenum
    val TEXTURE8: GLenum
    val TEXTURE9: GLenum
    val TEXTURE_2D: GLenum
    val TEXTURE_BINDING_2D: GLenum
    val TEXTURE_BINDING_CUBE_MAP: GLenum
    val TEXTURE_CUBE_MAP: GLenum
    val TEXTURE_CUBE_MAP_NEGATIVE_X: GLenum
    val TEXTURE_CUBE_MAP_NEGATIVE_Y: GLenum
    val TEXTURE_CUBE_MAP_NEGATIVE_Z: GLenum
    val TEXTURE_CUBE_MAP_POSITIVE_X: GLenum
    val TEXTURE_CUBE_MAP_POSITIVE_Y: GLenum
    val TEXTURE_CUBE_MAP_POSITIVE_Z: GLenum
    val TEXTURE_MAG_FILTER: GLenum
    val TEXTURE_MIN_FILTER: GLenum
    val TEXTURE_WRAP_S: GLenum
    val TEXTURE_WRAP_T: GLenum
    val TRIANGLES: GLenum
    val TRIANGLE_FAN: GLenum
    val TRIANGLE_STRIP: GLenum
    val UNPACK_ALIGNMENT: GLenum
    val UNPACK_COLORSPACE_CONVERSION_WEBGL: GLenum
    val UNPACK_FLIP_Y_WEBGL: GLenum
    val UNPACK_PREMULTIPLY_ALPHA_WEBGL: GLenum
    val UNSIGNED_BYTE: GLenum
    val UNSIGNED_INT: GLenum
    val UNSIGNED_SHORT: GLenum
    val UNSIGNED_SHORT_4_4_4_4: GLenum
    val UNSIGNED_SHORT_5_5_5_1: GLenum
    val UNSIGNED_SHORT_5_6_5: GLenum
    val VALIDATE_STATUS: GLenum
    val VENDOR: GLenum
    val VERSION: GLenum
    val VERTEX_ATTRIB_ARRAY_BUFFER_BINDING: GLenum
    val VERTEX_ATTRIB_ARRAY_ENABLED: GLenum
    val VERTEX_ATTRIB_ARRAY_NORMALIZED: GLenum
    val VERTEX_ATTRIB_ARRAY_POINTER: GLenum
    val VERTEX_ATTRIB_ARRAY_SIZE: GLenum
    val VERTEX_ATTRIB_ARRAY_STRIDE: GLenum
    val VERTEX_ATTRIB_ARRAY_TYPE: GLenum
    val VERTEX_SHADER: GLenum
    val VIEWPORT: GLenum
    val ZERO: GLenum
}

external interface WebGLRenderingContextOverloads {
    fun uniform1fv(location: WebGLUniformLocation?, v: Iterable<GLfloat>)
    fun uniform1iv(location: WebGLUniformLocation?, v: Iterable<GLint>)
    fun uniform2fv(location: WebGLUniformLocation?, v: Iterable<GLfloat>)
    fun uniform2iv(location: WebGLUniformLocation?, v: Iterable<GLint>)
    fun uniform3fv(location: WebGLUniformLocation?, v: Iterable<GLfloat>)
    fun uniform3iv(location: WebGLUniformLocation?, v: Iterable<GLint>)
    fun uniform4fv(location: WebGLUniformLocation?, v: Iterable<GLfloat>)
    fun uniform4iv(location: WebGLUniformLocation?, v: Iterable<GLint>)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Iterable<GLfloat>)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Iterable<GLfloat>)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Iterable<GLfloat>)
    fun bufferData(target: GLenum, size: GLsizeiptr, usage: GLenum)
    fun bufferData(target: GLenum, data: ArrayBufferView?, usage: GLenum)
    fun bufferData(target: GLenum, data: ArrayBuffer?, usage: GLenum)
    fun bufferSubData(target: GLenum, offset: GLintptr, data: ArrayBufferView)
    fun bufferSubData(target: GLenum, offset: GLintptr, data: ArrayBuffer)
    fun compressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, data: ArrayBufferView)
    fun compressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, data: ArrayBufferView)
    fun readPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: ArrayBufferView?)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, pixels: ArrayBufferView?)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: ImageData)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: ArrayBufferView?)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: ImageBitmap)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: ImageData)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: HTMLImageElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: HTMLCanvasElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: HTMLVideoElement)
    fun texSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, format: GLenum, type: GLenum, source: OffscreenCanvas)
    fun uniform1fv(location: WebGLUniformLocation?, v: Float32Array)
    fun uniform1fv(location: WebGLUniformLocation?, v: Array<GLfloat>)
    fun uniform1iv(location: WebGLUniformLocation?, v: Int32Array)
    fun uniform1iv(location: WebGLUniformLocation?, v: Array<GLint>)
    fun uniform2fv(location: WebGLUniformLocation?, v: Float32Array)
    fun uniform2fv(location: WebGLUniformLocation?, v: Array<GLfloat>)
    fun uniform2iv(location: WebGLUniformLocation?, v: Int32Array)
    fun uniform2iv(location: WebGLUniformLocation?, v: Array<GLint>)
    fun uniform3fv(location: WebGLUniformLocation?, v: Float32Array)
    fun uniform3fv(location: WebGLUniformLocation?, v: Array<GLfloat>)
    fun uniform3iv(location: WebGLUniformLocation?, v: Int32Array)
    fun uniform3iv(location: WebGLUniformLocation?, v: Array<GLint>)
    fun uniform4fv(location: WebGLUniformLocation?, v: Float32Array)
    fun uniform4fv(location: WebGLUniformLocation?, v: Array<GLfloat>)
    fun uniform4iv(location: WebGLUniformLocation?, v: Int32Array)
    fun uniform4iv(location: WebGLUniformLocation?, v: Array<GLint>)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Float32Array)
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Array<GLfloat>)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Float32Array)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Array<GLfloat>)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Float32Array)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: GLboolean, value: Array<GLfloat>)
}

external interface WebGLSampler : WebGLObject

external interface WebGLShader : WebGLObject

external interface WebGLShaderPrecisionFormat {
    val precision: GLint
    val rangeMax: GLint
    val rangeMin: GLint
}

external interface WebGLSync : WebGLObject

external interface WebGLTexture : WebGLObject

external interface WebGLTransformFeedback : WebGLObject

external interface WebGLUniformLocation

external interface WebGLVertexArrayObject : WebGLObject

external interface WebGLVertexArrayObjectOES : WebGLObject

external interface WebKitPoint {
    var x: Number
    var y: Number
}

external interface WheelEvent : MouseEvent {
    val deltaMode: Number
    val deltaX: Number
    val deltaY: Number
    val deltaZ: Number
    val DOM_DELTA_LINE: Number
    val DOM_DELTA_PAGE: Number
    val DOM_DELTA_PIXEL: Number
}

external interface WindowEventMap : GlobalEventHandlersEventMap, WindowEventHandlersEventMap {
    override var abort: UIEvent
    override var afterprint: Event
    override var beforeprint: Event
    override var beforeunload: BeforeUnloadEvent
    override var blur: FocusEvent
    override var canplay: Event
    override var canplaythrough: Event
    override var change: Event
    override var click: MouseEvent
    var compassneedscalibration: Event
    override var contextmenu: MouseEvent
    override var dblclick: MouseEvent
    var devicelight: DeviceLightEvent
    var devicemotion: DeviceMotionEvent
    var deviceorientation: DeviceOrientationEvent
    var deviceorientationabsolute: DeviceOrientationEvent
    override var drag: DragEvent
    override var dragend: DragEvent
    override var dragenter: DragEvent
    override var dragleave: DragEvent
    override var dragover: DragEvent
    override var dragstart: DragEvent
    override var drop: DragEvent
    override var durationchange: Event
    override var emptied: Event
    override var ended: Event
    override var error: ErrorEvent
    override var focus: FocusEvent
    override var hashchange: HashChangeEvent
    override var input: Event
    override var invalid: Event
    override var keydown: KeyboardEvent
    override var keypress: KeyboardEvent
    override var keyup: KeyboardEvent
    override var load: Event
    override var loadeddata: Event
    override var loadedmetadata: Event
    override var loadstart: Event
    override var message: MessageEvent
    override var mousedown: MouseEvent
    override var mouseenter: MouseEvent
    override var mouseleave: MouseEvent
    override var mousemove: MouseEvent
    override var mouseout: MouseEvent
    override var mouseover: MouseEvent
    override var mouseup: MouseEvent
    var mousewheel: Event
    var MSGestureChange: Event
    var MSGestureDoubleTap: Event
    var MSGestureEnd: Event
    var MSGestureHold: Event
    var MSGestureStart: Event
    var MSGestureTap: Event
    var MSInertiaStart: Event
    var MSPointerCancel: Event
    var MSPointerDown: Event
    var MSPointerEnter: Event
    var MSPointerLeave: Event
    var MSPointerMove: Event
    var MSPointerOut: Event
    var MSPointerOver: Event
    var MSPointerUp: Event
    override var offline: Event
    override var online: Event
    var orientationchange: Event
    override var pagehide: PageTransitionEvent
    override var pageshow: PageTransitionEvent
    override var pause: Event
    override var play: Event
    override var playing: Event
    override var popstate: PopStateEvent
    override var ratechange: Event
    var readystatechange: ProgressEvent<Window>
    override var reset: Event
    override var resize: UIEvent
    override var scroll: Event
    override var seeked: Event
    override var seeking: Event
    override var select: Event
    override var stalled: Event
    override var storage: StorageEvent
    override var submit: Event
    override var suspend: Event
    override var timeupdate: Event
    override var unload: Event
    override var volumechange: Event
    var vrdisplayactivate: Event
    var vrdisplayblur: Event
    var vrdisplayconnect: Event
    var vrdisplaydeactivate: Event
    var vrdisplaydisconnect: Event
    var vrdisplayfocus: Event
    var vrdisplaypointerrestricted: Event
    var vrdisplaypointerunrestricted: Event
    var vrdisplaypresentchange: Event
    override var waiting: Event
}

external interface Window : EventTarget, AnimationFrameProvider, GlobalEventHandlers, WindowEventHandlers, WindowLocalStorage, WindowOrWorkerGlobalScope, WindowSessionStorage {
    val applicationCache: ApplicationCache
    val clientInformation: Navigator
    val closed: Boolean
    var customElements: CustomElementRegistry
    var defaultStatus: String
    val devicePixelRatio: Number
    val doNotTrack: String
    val document: Document
    val event: Event?
    val external: External
    val frameElement: Element
    val frames: Window
    val history: History
    val innerHeight: Number
    val innerWidth: Number
    val length: Number
    var location: Location
    val locationbar: BarProp
    val menubar: BarProp
    val msContentScript: ExtensionScriptApis
    var name: String
    val navigator: Navigator
    var offscreenBuffering: dynamic /* String | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var oncompassneedscalibration: ((self: Window, ev: Event) -> Any)?
    var ondevicelight: ((self: Window, ev: DeviceLightEvent) -> Any)?
    var ondevicemotion: ((self: Window, ev: DeviceMotionEvent) -> Any)?
    var ondeviceorientation: ((self: Window, ev: DeviceOrientationEvent) -> Any)?
    var ondeviceorientationabsolute: ((self: Window, ev: DeviceOrientationEvent) -> Any)?
    var onmousewheel: ((self: Window, ev: Event) -> Any)?
    var onmsgesturechange: ((self: Window, ev: Event) -> Any)?
    var onmsgesturedoubletap: ((self: Window, ev: Event) -> Any)?
    var onmsgestureend: ((self: Window, ev: Event) -> Any)?
    var onmsgesturehold: ((self: Window, ev: Event) -> Any)?
    var onmsgesturestart: ((self: Window, ev: Event) -> Any)?
    var onmsgesturetap: ((self: Window, ev: Event) -> Any)?
    var onmsinertiastart: ((self: Window, ev: Event) -> Any)?
    var onmspointercancel: ((self: Window, ev: Event) -> Any)?
    var onmspointerdown: ((self: Window, ev: Event) -> Any)?
    var onmspointerenter: ((self: Window, ev: Event) -> Any)?
    var onmspointerleave: ((self: Window, ev: Event) -> Any)?
    var onmspointermove: ((self: Window, ev: Event) -> Any)?
    var onmspointerout: ((self: Window, ev: Event) -> Any)?
    var onmspointerover: ((self: Window, ev: Event) -> Any)?
    var onmspointerup: ((self: Window, ev: Event) -> Any)?
    var onorientationchange: ((self: Window, ev: Event) -> Any)?
    var onreadystatechange: ((self: Window, ev: ProgressEvent<Window>) -> Any)?
    var onvrdisplayactivate: ((self: Window, ev: Event) -> Any)?
    var onvrdisplayblur: ((self: Window, ev: Event) -> Any)?
    var onvrdisplayconnect: ((self: Window, ev: Event) -> Any)?
    var onvrdisplaydeactivate: ((self: Window, ev: Event) -> Any)?
    var onvrdisplaydisconnect: ((self: Window, ev: Event) -> Any)?
    var onvrdisplayfocus: ((self: Window, ev: Event) -> Any)?
    var onvrdisplaypointerrestricted: ((self: Window, ev: Event) -> Any)?
    var onvrdisplaypointerunrestricted: ((self: Window, ev: Event) -> Any)?
    var onvrdisplaypresentchange: ((self: Window, ev: Event) -> Any)?
    var opener: Any
    val orientation: dynamic /* String | Number */
        get() = definedExternally
    val outerHeight: Number
    val outerWidth: Number
    val pageXOffset: Number
    val pageYOffset: Number
    val parent: Window
    val personalbar: BarProp
    val screen: Screen
    val screenLeft: Number
    val screenTop: Number
    val screenX: Number
    val screenY: Number
    val scrollX: Number
    val scrollY: Number
    val scrollbars: BarProp
    val self: Window /* Window & Any */
    val speechSynthesis: SpeechSynthesis
    var status: String
    val statusbar: BarProp
    val styleMedia: StyleMedia
    val toolbar: BarProp
    val top: Window
    val window: Window /* Window & Any */
    fun alert(message: Any = definedExternally)
    fun blur()
    fun captureEvents()
    fun close()
    fun confirm(message: String = definedExternally): Boolean
    fun departFocus(navigationReason: String /* "down" | "left" | "right" | "up" */, origin: FocusNavigationOrigin)
    fun focus()
    fun getComputedStyle(elt: Element, pseudoElt: String? = definedExternally): CSSStyleDeclaration
    fun getMatchedCSSRules(elt: Element, pseudoElt: String? = definedExternally): CSSRuleList
    fun getSelection(): Selection?
    fun matchMedia(query: String): MediaQueryList
    fun moveBy(x: Number, y: Number)
    fun moveTo(x: Number, y: Number)
    fun msWriteProfilerMark(profilerMarkName: String)
    fun open(url: String = definedExternally, target: String = definedExternally, features: String = definedExternally, replace: Boolean = definedExternally): Window?
    fun postMessage(message: Any, targetOrigin: String, transfer: Array<Any /* ArrayBuffer | MessagePort | ImageBitmap | OffscreenCanvas */> = definedExternally)
    fun print()
    fun prompt(message: String = definedExternally, _default: String = definedExternally): String?
    fun releaseEvents()
    fun resizeBy(x: Number, y: Number)
    fun resizeTo(x: Number, y: Number)
    fun scroll(options: ScrollToOptions = definedExternally)
    fun scroll()
    fun scroll(x: Number, y: Number)
    fun scrollBy(options: ScrollToOptions = definedExternally)
    fun scrollBy()
    fun scrollBy(x: Number, y: Number)
    fun scrollTo(options: ScrollToOptions = definedExternally)
    fun scrollTo()
    fun scrollTo(x: Number, y: Number)
    fun stop()
    fun webkitCancelAnimationFrame(handle: Number)
    fun webkitConvertPointFromNodeToPage(node: Node, pt: WebKitPoint): WebKitPoint
    fun webkitConvertPointFromPageToNode(node: Node, pt: WebKitPoint): WebKitPoint
    fun webkitRequestAnimationFrame(callback: FrameRequestCallback): Number
    fun <K : String> addEventListener(type: K, listener: (self: Window, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Window, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Window, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Window, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Window, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Window, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
    @nativeGetter
    operator fun get(index: Number): Window?
    @nativeSetter
    operator fun set(index: Number, value: Window)
}

external interface WindowEventHandlersEventMap {
    var afterprint: Event
    var beforeprint: Event
    var beforeunload: BeforeUnloadEvent
    var hashchange: HashChangeEvent
    var languagechange: Event
    var message: MessageEvent
    var messageerror: MessageEvent
    var offline: Event
    var online: Event
    var pagehide: PageTransitionEvent
    var pageshow: PageTransitionEvent
    var popstate: PopStateEvent
    var rejectionhandled: PromiseRejectionEvent
    var storage: StorageEvent
    var unhandledrejection: PromiseRejectionEvent
    var unload: Event
}

external interface WindowEventHandlers {
    var onafterprint: ((self: WindowEventHandlers, ev: Event) -> Any)?
    var onbeforeprint: ((self: WindowEventHandlers, ev: Event) -> Any)?
    var onbeforeunload: ((self: WindowEventHandlers, ev: BeforeUnloadEvent) -> Any)?
    var onhashchange: ((self: WindowEventHandlers, ev: HashChangeEvent) -> Any)?
    var onlanguagechange: ((self: WindowEventHandlers, ev: Event) -> Any)?
    var onmessage: ((self: WindowEventHandlers, ev: MessageEvent) -> Any)?
    var onmessageerror: ((self: WindowEventHandlers, ev: MessageEvent) -> Any)?
    var onoffline: ((self: WindowEventHandlers, ev: Event) -> Any)?
    var ononline: ((self: WindowEventHandlers, ev: Event) -> Any)?
    var onpagehide: ((self: WindowEventHandlers, ev: PageTransitionEvent) -> Any)?
    var onpageshow: ((self: WindowEventHandlers, ev: PageTransitionEvent) -> Any)?
    var onpopstate: ((self: WindowEventHandlers, ev: PopStateEvent) -> Any)?
    var onrejectionhandled: ((self: WindowEventHandlers, ev: PromiseRejectionEvent) -> Any)?
    var onstorage: ((self: WindowEventHandlers, ev: StorageEvent) -> Any)?
    var onunhandledrejection: ((self: WindowEventHandlers, ev: PromiseRejectionEvent) -> Any)?
    var onunload: ((self: WindowEventHandlers, ev: Event) -> Any)?
    fun <K : String> addEventListener(type: K, listener: (self: WindowEventHandlers, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: WindowEventHandlers, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: WindowEventHandlers, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListener)
    fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun addEventListener(type: String, listener: EventListenerObject)
    fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: WindowEventHandlers, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: WindowEventHandlers, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: WindowEventHandlers, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListener, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListener)
    fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean = definedExternally)
    fun removeEventListener(type: String, listener: EventListenerObject)
    fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions = definedExternally)
}

external interface WindowLocalStorage {
    val localStorage: Storage
}

external interface WindowOrWorkerGlobalScope {
    val caches: CacheStorage
    val crypto: Crypto
    val indexedDB: IDBFactory
    val isSecureContext: Boolean
    val origin: String
    val performance: Performance
    fun atob(data: String): String
    fun btoa(data: String): String
    fun clearInterval(handle: Number = definedExternally)
    fun clearTimeout(handle: Number = definedExternally)
    fun createImageBitmap(image: HTMLImageElement, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLImageElement): Promise<ImageBitmap>
    fun createImageBitmap(image: SVGImageElement, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: SVGImageElement): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLVideoElement, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLVideoElement): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLCanvasElement, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLCanvasElement): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageBitmap, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageBitmap): Promise<ImageBitmap>
    fun createImageBitmap(image: OffscreenCanvas, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: OffscreenCanvas): Promise<ImageBitmap>
    fun createImageBitmap(image: Blob, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: Blob): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageData, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageData): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLImageElement, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLImageElement, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: SVGImageElement, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: SVGImageElement, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLVideoElement, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLVideoElement, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLCanvasElement, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: HTMLCanvasElement, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageBitmap, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageBitmap, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: OffscreenCanvas, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: OffscreenCanvas, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: Blob, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: Blob, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageData, sx: Number, sy: Number, sw: Number, sh: Number, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
    fun createImageBitmap(image: ImageData, sx: Number, sy: Number, sw: Number, sh: Number): Promise<ImageBitmap>
    fun fetch(input: Request, init: RequestInit = definedExternally): Promise<Response>
    fun fetch(input: Request): Promise<Response>
    fun fetch(input: String, init: RequestInit = definedExternally): Promise<Response>
    fun fetch(input: String): Promise<Response>
    fun queueMicrotask(callback: VoidFunction)
    fun setInterval(handler: String, timeout: Number = definedExternally, vararg arguments: Any): Number
    fun setInterval(handler: Function<*>, timeout: Number = definedExternally, vararg arguments: Any): Number
    fun setTimeout(handler: String, timeout: Number = definedExternally, vararg arguments: Any): Number
    fun setTimeout(handler: Function<*>, timeout: Number = definedExternally, vararg arguments: Any): Number
}

external interface WindowSessionStorage {
    val sessionStorage: Storage
}

external interface WorkerEventMap : AbstractWorkerEventMap {
    var message: MessageEvent
    var messageerror: MessageEvent
}

external interface Worker : EventTarget, AbstractWorker {
    var onmessage: ((self: Worker, ev: MessageEvent) -> Any)?
    var onmessageerror: ((self: Worker, ev: MessageEvent) -> Any)?
    fun postMessage(message: Any, transfer: Array<Any /* ArrayBuffer | MessagePort | ImageBitmap | OffscreenCanvas */>)
    fun postMessage(message: Any, options: PostMessageOptions = definedExternally)
    fun postMessage(message: Any)
    fun terminate()
    fun <K : String> addEventListener(type: K, listener: (self: Worker, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: Worker, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: Worker, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: Worker, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: Worker, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: Worker, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface Worklet {
    fun addModule(moduleURL: String, options: WorkletOptions = definedExternally): Promise<Unit>
}

external interface WritableStream<W> {
    val locked: Boolean
    fun abort(reason: Any = definedExternally): Promise<Unit>
    fun getWriter(): WritableStreamDefaultWriter<W>
}

external interface WritableStreamDefaultController {
    fun error(error: Any = definedExternally)
}

external interface WritableStreamDefaultWriter<W> {
    val closed: Promise<Unit>
    val desiredSize: Number?
    val ready: Promise<Unit>
    fun abort(reason: Any = definedExternally): Promise<Unit>
    fun close(): Promise<Unit>
    fun releaseLock()
    fun write(chunk: W): Promise<Unit>
}

external interface XMLDocument : Document {
    fun <K : String> addEventListener(type: K, listener: (self: XMLDocument, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> addEventListener(type: K, listener: (self: XMLDocument, ev: Any) -> Any)
    fun <K : String> addEventListener(type: K, listener: (self: XMLDocument, ev: Any) -> Any, options: AddEventListenerOptions = definedExternally)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    fun <K : String> removeEventListener(type: K, listener: (self: XMLDocument, ev: Any) -> Any, options: Boolean = definedExternally)
    fun <K : String> removeEventListener(type: K, listener: (self: XMLDocument, ev: Any) -> Any)
    fun <K : String> removeEventListener(type: K, listener: (self: XMLDocument, ev: Any) -> Any, options: EventListenerOptions = definedExternally)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}

external interface XPathEvaluatorBase {
    fun createExpression(expression: String, resolver: ((prefix: String?) -> String?)? = definedExternally): XPathExpression
    fun createExpression(expression: String): XPathExpression
    fun createExpression(expression: String, resolver: `T$15`? = definedExternally): XPathExpression
    fun createNSResolver(nodeResolver: Node): dynamic /* (prefix: String?) -> String? | `T$15` */
    fun evaluate(expression: String, contextNode: Node, resolver: ((prefix: String?) -> String?)? = definedExternally, type: Number = definedExternally, result: XPathResult? = definedExternally): XPathResult
    fun evaluate(expression: String, contextNode: Node): XPathResult
    fun evaluate(expression: String, contextNode: Node, resolver: ((prefix: String?) -> String?)? = definedExternally): XPathResult
    fun evaluate(expression: String, contextNode: Node, resolver: ((prefix: String?) -> String?)? = definedExternally, type: Number = definedExternally): XPathResult
    fun evaluate(expression: String, contextNode: Node, resolver: `T$15`? = definedExternally, type: Number = definedExternally, result: XPathResult? = definedExternally): XPathResult
    fun evaluate(expression: String, contextNode: Node, resolver: `T$15`? = definedExternally): XPathResult
    fun evaluate(expression: String, contextNode: Node, resolver: `T$15`? = definedExternally, type: Number = definedExternally): XPathResult
}

external interface XPathExpression {
    fun evaluate(contextNode: Node, type: Number = definedExternally, result: XPathResult? = definedExternally): XPathResult
}

external interface XPathResult {
    val booleanValue: Boolean
    val invalidIteratorState: Boolean
    val numberValue: Number
    val resultType: Number
    val singleNodeValue: Node?
    val snapshotLength: Number
    val stringValue: String
    fun iterateNext(): Node?
    fun snapshotItem(index: Number): Node?
    val ANY_TYPE: Number
    val ANY_UNORDERED_NODE_TYPE: Number
    val BOOLEAN_TYPE: Number
    val FIRST_ORDERED_NODE_TYPE: Number
    val NUMBER_TYPE: Number
    val ORDERED_NODE_ITERATOR_TYPE: Number
    val ORDERED_NODE_SNAPSHOT_TYPE: Number
    val STRING_TYPE: Number
    val UNORDERED_NODE_ITERATOR_TYPE: Number
    val UNORDERED_NODE_SNAPSHOT_TYPE: Number
}

external interface BlobCallback {
    @nativeInvoke
    operator fun invoke(blob: Blob?)
}

external interface CustomElementConstructor

external interface DecodeErrorCallback {
    @nativeInvoke
    operator fun invoke(error: DOMException)
}

external interface DecodeSuccessCallback {
    @nativeInvoke
    operator fun invoke(decodedData: AudioBuffer)
}

external interface FrameRequestCallback {
    @nativeInvoke
    operator fun invoke(time: Number)
}

external interface FunctionStringCallback {
    @nativeInvoke
    operator fun invoke(data: String)
}

external interface MSLaunchUriCallback {
    @nativeInvoke
    operator fun invoke()
}

external interface NavigatorUserMediaErrorCallback {
    @nativeInvoke
    operator fun invoke(error: MediaStreamError)
}

external interface NavigatorUserMediaSuccessCallback {
    @nativeInvoke
    operator fun invoke(stream: MediaStream)
}

external interface NotificationPermissionCallback {
    @nativeInvoke
    operator fun invoke(permission: String /* "default" | "denied" | "granted" */)
}

external interface OnErrorEventHandlerNonNull {
    @nativeInvoke
    operator fun invoke(event: Event, source: String = definedExternally, lineno: Number = definedExternally, colno: Number = definedExternally, error: Error = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: Event): Any
    @nativeInvoke
    operator fun invoke(event: Event, source: String = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: Event, source: String = definedExternally, lineno: Number = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: Event, source: String = definedExternally, lineno: Number = definedExternally, colno: Number = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: String, source: String = definedExternally, lineno: Number = definedExternally, colno: Number = definedExternally, error: Error = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: String): Any
    @nativeInvoke
    operator fun invoke(event: String, source: String = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: String, source: String = definedExternally, lineno: Number = definedExternally): Any
    @nativeInvoke
    operator fun invoke(event: String, source: String = definedExternally, lineno: Number = definedExternally, colno: Number = definedExternally): Any
}

external interface PositionCallback {
    @nativeInvoke
    operator fun invoke(position: Position)
}

external interface PositionErrorCallback {
    @nativeInvoke
    operator fun invoke(positionError: PositionError)
}

external interface QueuingStrategySizeCallback<T> {
    @nativeInvoke
    operator fun invoke(chunk: T): Number
}

external interface ReadableByteStreamControllerCallback {
    @nativeInvoke
    operator fun invoke(controller: ReadableByteStreamController): dynamic /* Unit | PromiseLike<Unit> */
}

external interface ReadableStreamDefaultControllerCallback<R> {
    @nativeInvoke
    operator fun invoke(controller: ReadableStreamDefaultController<R>): dynamic /* Unit | PromiseLike<Unit> */
}

external interface ReadableStreamErrorCallback {
    @nativeInvoke
    operator fun invoke(reason: Any): dynamic /* Unit | PromiseLike<Unit> */
}

external interface VoidFunction {
    @nativeInvoke
    operator fun invoke()
}

external interface WritableStreamDefaultControllerCloseCallback {
    @nativeInvoke
    operator fun invoke(): dynamic /* Unit | PromiseLike<Unit> */
}

external interface WritableStreamDefaultControllerStartCallback {
    @nativeInvoke
    operator fun invoke(controller: WritableStreamDefaultController): dynamic /* Unit | PromiseLike<Unit> */
}

external interface WritableStreamDefaultControllerWriteCallback<W> {
    @nativeInvoke
    operator fun invoke(chunk: W, controller: WritableStreamDefaultController): dynamic /* Unit | PromiseLike<Unit> */
}

external interface WritableStreamErrorCallback {
    @nativeInvoke
    operator fun invoke(reason: Any): dynamic /* Unit | PromiseLike<Unit> */
}

external interface HTMLElementTagNameMap {
    var a: HTMLAnchorElement
    var abbr: HTMLElement
    var address: HTMLElement
    var applet: HTMLAppletElement
    var area: HTMLAreaElement
    var article: HTMLElement
    var aside: HTMLElement
    var audio: HTMLAudioElement
    var b: HTMLElement
    var base: HTMLBaseElement
    var basefont: HTMLBaseFontElement
    var bdi: HTMLElement
    var bdo: HTMLElement
    var blockquote: HTMLQuoteElement
    var body: HTMLBodyElement
    var br: HTMLBRElement
    var button: HTMLButtonElement
    var canvas: HTMLCanvasElement
    var caption: HTMLTableCaptionElement
    var cite: HTMLElement
    var code: HTMLElement
    var col: HTMLTableColElement
    var colgroup: HTMLTableColElement
    var data: HTMLDataElement
    var datalist: HTMLDataListElement
    var dd: HTMLElement
    var del: HTMLModElement
    var details: HTMLDetailsElement
    var dfn: HTMLElement
    var dialog: HTMLDialogElement
    var dir: HTMLDirectoryElement
    var div: HTMLDivElement
    var dl: HTMLDListElement
    var dt: HTMLElement
    var em: HTMLElement
    var embed: HTMLEmbedElement
    var fieldset: HTMLFieldSetElement
    var figcaption: HTMLElement
    var figure: HTMLElement
    var font: HTMLFontElement
    var footer: HTMLElement
    var form: HTMLFormElement
    var frame: HTMLFrameElement
    var frameset: HTMLFrameSetElement
    var h1: HTMLHeadingElement
    var h2: HTMLHeadingElement
    var h3: HTMLHeadingElement
    var h4: HTMLHeadingElement
    var h5: HTMLHeadingElement
    var h6: HTMLHeadingElement
    var head: HTMLHeadElement
    var header: HTMLElement
    var hgroup: HTMLElement
    var hr: HTMLHRElement
    var html: HTMLHtmlElement
    var i: HTMLElement
    var iframe: HTMLIFrameElement
    var img: HTMLImageElement
    var input: HTMLInputElement
    var ins: HTMLModElement
    var kbd: HTMLElement
    var label: HTMLLabelElement
    var legend: HTMLLegendElement
    var li: HTMLLIElement
    var link: HTMLLinkElement
    var main: HTMLElement
    var map: HTMLMapElement
    var mark: HTMLElement
    var marquee: HTMLMarqueeElement
    var menu: HTMLMenuElement
    var meta: HTMLMetaElement
    var meter: HTMLMeterElement
    var nav: HTMLElement
    var noscript: HTMLElement
    var `object`: HTMLObjectElement
    var ol: HTMLOListElement
    var optgroup: HTMLOptGroupElement
    var option: HTMLOptionElement
    var output: HTMLOutputElement
    var p: HTMLParagraphElement
    var param: HTMLParamElement
    var picture: HTMLPictureElement
    var pre: HTMLPreElement
    var progress: HTMLProgressElement
    var q: HTMLQuoteElement
    var rp: HTMLElement
    var rt: HTMLElement
    var ruby: HTMLElement
    var s: HTMLElement
    var samp: HTMLElement
    var script: HTMLScriptElement
    var section: HTMLElement
    var select: HTMLSelectElement
    var slot: HTMLSlotElement
    var small: HTMLElement
    var source: HTMLSourceElement
    var span: HTMLSpanElement
    var strong: HTMLElement
    var style: HTMLStyleElement
    var sub: HTMLElement
    var summary: HTMLElement
    var sup: HTMLElement
    var table: HTMLTableElement
    var tbody: HTMLTableSectionElement
    var td: HTMLTableDataCellElement
    var template: HTMLTemplateElement
    var textarea: HTMLTextAreaElement
    var tfoot: HTMLTableSectionElement
    var th: HTMLTableHeaderCellElement
    var thead: HTMLTableSectionElement
    var time: HTMLTimeElement
    var title: HTMLTitleElement
    var tr: HTMLTableRowElement
    var track: HTMLTrackElement
    var u: HTMLElement
    var ul: HTMLUListElement
    var `var`: HTMLElement
    var video: HTMLVideoElement
    var wbr: HTMLElement
}

external interface HTMLElementDeprecatedTagNameMap {
    var listing: HTMLPreElement
    var xmp: HTMLPreElement
}

external interface SVGElementTagNameMap {
    var a: SVGAElement
    var circle: SVGCircleElement
    var clipPath: SVGClipPathElement
    var defs: SVGDefsElement
    var desc: SVGDescElement
    var ellipse: SVGEllipseElement
    var feBlend: SVGFEBlendElement
    var feColorMatrix: SVGFEColorMatrixElement
    var feComponentTransfer: SVGFEComponentTransferElement
    var feComposite: SVGFECompositeElement
    var feConvolveMatrix: SVGFEConvolveMatrixElement
    var feDiffuseLighting: SVGFEDiffuseLightingElement
    var feDisplacementMap: SVGFEDisplacementMapElement
    var feDistantLight: SVGFEDistantLightElement
    var feFlood: SVGFEFloodElement
    var feFuncA: SVGFEFuncAElement
    var feFuncB: SVGFEFuncBElement
    var feFuncG: SVGFEFuncGElement
    var feFuncR: SVGFEFuncRElement
    var feGaussianBlur: SVGFEGaussianBlurElement
    var feImage: SVGFEImageElement
    var feMerge: SVGFEMergeElement
    var feMergeNode: SVGFEMergeNodeElement
    var feMorphology: SVGFEMorphologyElement
    var feOffset: SVGFEOffsetElement
    var fePointLight: SVGFEPointLightElement
    var feSpecularLighting: SVGFESpecularLightingElement
    var feSpotLight: SVGFESpotLightElement
    var feTile: SVGFETileElement
    var feTurbulence: SVGFETurbulenceElement
    var filter: SVGFilterElement
    var foreignObject: SVGForeignObjectElement
    var g: SVGGElement
    var image: SVGImageElement
    var line: SVGLineElement
    var linearGradient: SVGLinearGradientElement
    var marker: SVGMarkerElement
    var mask: SVGMaskElement
    var metadata: SVGMetadataElement
    var path: SVGPathElement
    var pattern: SVGPatternElement
    var polygon: SVGPolygonElement
    var polyline: SVGPolylineElement
    var radialGradient: SVGRadialGradientElement
    var rect: SVGRectElement
    var script: SVGScriptElement
    var stop: SVGStopElement
    var style: SVGStyleElement
    var svg: SVGSVGElement
    var switch: SVGSwitchElement
    var symbol: SVGSymbolElement
    var text: SVGTextElement
    var textPath: SVGTextPathElement
    var title: SVGTitleElement
    var tspan: SVGTSpanElement
    var use: SVGUseElement
    var view: SVGViewElement
}

typealias DOMHighResTimeStamp = Number

typealias PerformanceEntryList = Array<PerformanceEntry>

typealias COSEAlgorithmIdentifier = Number

typealias AuthenticatorSelectionList = Array<dynamic /* typealias AAGUID = dynamic */>

typealias BigInteger = Uint8Array

typealias NamedCurve = String

typealias GLenum = Number

typealias GLboolean = Boolean

typealias GLbitfield = Number

typealias GLint = Number

typealias GLsizei = Number

typealias GLintptr = Number

typealias GLsizeiptr = Number

typealias GLuint = Number

typealias GLfloat = Number

typealias GLclampf = Number

typealias GLint64 = Number

typealias GLuint64 = Number