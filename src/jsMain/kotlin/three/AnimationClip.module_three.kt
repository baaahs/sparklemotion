@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class AnimationClip(name: String = definedExternally, duration: Number = definedExternally, tracks: Array<KeyframeTrack> = definedExternally, blendMode: AnimationBlendMode = definedExternally) {
    open var name: String
    open var tracks: Array<KeyframeTrack>
    open var blendMode: AnimationBlendMode
    open var duration: Number
    open var uuid: String
    open var results: Array<Any>
    open fun resetDuration(): AnimationClip
    open fun trim(): AnimationClip
    open fun validate(): Boolean
    open fun optimize(): AnimationClip
    open fun clone(): AnimationClip

    companion object {
        fun CreateFromMorphTargetSequence(name: String, morphTargetSequence: Array<MorphTarget>, fps: Number, noLoop: Boolean): AnimationClip
        fun findByName(clipArray: Array<AnimationClip>, name: String): AnimationClip
        fun CreateClipsFromMorphTargetSequences(morphTargets: Array<MorphTarget>, fps: Number, noLoop: Boolean): Array<AnimationClip>
        fun parse(json: Any): AnimationClip
        fun parseAnimation(animation: Any, bones: Array<Bone>): AnimationClip
        fun toJSON(): Any
    }
}