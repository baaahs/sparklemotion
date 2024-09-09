@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class AnimationAction(mixer: AnimationMixer, clip: AnimationClip, localRoot: Object3D = definedExternally, blendMode: AnimationBlendMode = definedExternally) {
    open var blendMode: AnimationBlendMode
    open var loop: AnimationActionLoopStyles
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
    open fun setLoop(mode: AnimationActionLoopStyles, repetitions: Number): AnimationAction
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
    open fun getRoot(): Object3D
}