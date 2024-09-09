@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

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

external interface AnimationClipJSON {
    var name: String
    var duration: Number
    var tracks: Array<KeyframeTrackJSON>
    var uuid: String
    var blendMode: Any
}

external interface MorphTarget {
    var name: String
    var vertices: Array<Vector3>
}

external open class AnimationClip(name: String = definedExternally, duration: Number = definedExternally, tracks: Array<KeyframeTrack> = definedExternally, blendMode: Any = definedExternally) {
    open var name: String
    open var tracks: Array<KeyframeTrack>
    open var blendMode: Any
    open var duration: Number
    open var uuid: String
    open var results: Array<Any>
    open fun resetDuration(): AnimationClip
    open fun trim(): AnimationClip
    open fun validate(): Boolean
    open fun optimize(): AnimationClip
    open fun clone(): AnimationClip /* this */
    open fun toJSON(clip: AnimationClip): Any

    companion object {
        fun CreateFromMorphTargetSequence(name: String, morphTargetSequence: Array<MorphTarget>, fps: Number, noLoop: Boolean): AnimationClip
        fun findByName(clipArray: Array<AnimationClip>, name: String): AnimationClip
        fun CreateClipsFromMorphTargetSequences(morphTargets: Array<MorphTarget>, fps: Number, noLoop: Boolean): Array<AnimationClip>
        fun parse(json: AnimationClipJSON): AnimationClip
        fun parseAnimation(animation: AnimationClipJSON, bones: Array<Bone__0>): AnimationClip
        fun toJSON(clip: AnimationClip): AnimationClipJSON
    }
}