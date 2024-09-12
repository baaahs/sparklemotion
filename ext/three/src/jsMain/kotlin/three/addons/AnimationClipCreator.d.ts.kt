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