package three.addons

import three.AnimationClip
import three.Vector3

external object AnimationClipCreator {
    fun CreateRotationAnimation(period: Number, axis: String): AnimationClip
    fun CreateScaleAxisAnimation(period: Number, axis: String): AnimationClip
    fun CreateShakeAnimation(duration: Number, shakeScale: Vector3): AnimationClip
    fun CreatePulsationAnimation(duration: Number, pulseScale: Number): AnimationClip
    fun CreateVisibilityAnimation(duration: Number): AnimationClip
    fun CreateMaterialColorAnimation(duration: Number, colors: Array<Number>): AnimationClip
}