@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.AnimationClip
import three.Vector3

@Suppress("EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface AnimationClipCreator {
    fun CreateRotationAnimation(period: Number, axis: String): AnimationClip
    fun CreateScaleAxisAnimation(period: Number, axis: String): AnimationClip
    fun CreateShakeAnimation(duration: Number, shakeScale: Vector3): AnimationClip
    fun CreatePulsationAnimation(duration: Number, pulseScale: Number): AnimationClip
    fun CreateVisibilityAnimation(duration: Number): AnimationClip
    fun CreateMaterialColorAnimation(duration: Number, colors: Array<Number>): AnimationClip

    companion object : AnimationClipCreator by definedExternally
}