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

open external class AnimationAction(mixer: AnimationMixer, clip: AnimationClip, localRoot: Object3D/*<Object3DEventMap>*/ = definedExternally, blendMode: Any = definedExternally) {
    open var blendMode: Any
    open var loop: Any
    open var time: Number
    open var timeScale: Number
    open var weight: Number
    open var repetitions: Number
    open var paused: Boolean
    open var enabled: Boolean
    open var clampWhenFinished: Boolean
    open var zeroSlopeAtStart: Boolean
    open var zeroSlopeAtEnd: Boolean
    open fun play(): AnimationAction
    open fun stop(): AnimationAction
    open fun reset(): AnimationAction
    open fun isRunning(): Boolean
    open fun isScheduled(): Boolean
    open fun startAt(time: Number): AnimationAction
    open fun setLoop(mode: Any, repetitions: Number): AnimationAction
    open fun setEffectiveWeight(weight: Number): AnimationAction
    open fun getEffectiveWeight(): Number
    open fun fadeIn(duration: Number): AnimationAction
    open fun fadeOut(duration: Number): AnimationAction
    open fun crossFadeFrom(fadeOutAction: AnimationAction, duration: Number, warp: Boolean): AnimationAction
    open fun crossFadeTo(fadeInAction: AnimationAction, duration: Number, warp: Boolean): AnimationAction
    open fun stopFading(): AnimationAction
    open fun setEffectiveTimeScale(timeScale: Number): AnimationAction
    open fun getEffectiveTimeScale(): Number
    open fun setDuration(duration: Number): AnimationAction
    open fun syncWith(action: AnimationAction): AnimationAction
    open fun halt(duration: Number): AnimationAction
    open fun warp(statTimeScale: Number, endTimeScale: Number, duration: Number): AnimationAction
    open fun stopWarping(): AnimationAction
    open fun getMixer(): AnimationMixer
    open fun getClip(): AnimationClip
    open fun getRoot(): Object3D/*<Object3DEventMap>*/
}