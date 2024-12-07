package three.addons

import js.collections.WeakMap
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
import three.*
import three.Audio
import kotlin.js.*

external interface MMDAnimationHelperParameter {
    var sync: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var afterglow: Number?
        get() = definedExternally
        set(value) = definedExternally
    var resetPhysicsOnLoop: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var pmxAnimation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MMDAnimationHelperAddParameter {
    var animation: dynamic /* AnimationClip? | Array<AnimationClip>? */
        get() = definedExternally
        set(value) = definedExternally
    var physics: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var warmup: Number?
        get() = definedExternally
        set(value) = definedExternally
    var unitStep: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxStepNum: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gravity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var delayTime: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MMDAnimationHelperPoseParameter {
    var resetPose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ik: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var grant: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MMDAnimationHelperMixer {
    var looped: Boolean
    var mixer: AnimationMixer?
        get() = definedExternally
        set(value) = definedExternally
    var ikSolver: CCDIKSolver
    var grantSolver: GrantSolver
    var physics: MMDPhysics?
        get() = definedExternally
        set(value) = definedExternally
    var duration: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$37` {
    var sync: Boolean
    var afterglow: Number
    var resetPhysicsOnLoop: Boolean
    var pmxAnimation: Boolean
}

external interface `T$38` {
    var animation: Boolean
    var ik: Boolean
    var grant: Boolean
    var physics: Boolean
    var cameraAnimation: Boolean
}

open external class MMDAnimationHelper(params: MMDAnimationHelperParameter = definedExternally) {
    open var meshes: Array<SkinnedMesh<*, *>>
    open var camera: Camera?
    open var cameraTarget: Object3D
    open var audio: Audio<*>
    open var audioManager: AudioManager
    open var configuration: `T$37`
    open var enabled: `T$38`
    open var objects: WeakMap<dynamic /* SkinnedMesh<*, *> | Camera | AudioManager */, MMDAnimationHelperMixer>
    open var onBeforePhysics: (mesh: SkinnedMesh<*, *>) -> Unit
    open var sharedPhysics: Boolean
    open var masterPhysics: Any?
    open fun add(obj: SkinnedMesh<*, *>, params: MMDAnimationHelperAddParameter = definedExternally): MMDAnimationHelper /* this */
    open fun add(obj: SkinnedMesh<*, *>): MMDAnimationHelper /* this */
    open fun add(obj: Camera, params: MMDAnimationHelperAddParameter = definedExternally): MMDAnimationHelper /* this */
    open fun add(obj: Camera): MMDAnimationHelper /* this */
    open fun add(obj: Audio<*>, params: MMDAnimationHelperAddParameter = definedExternally): MMDAnimationHelper /* this */
    open fun add(obj: Audio<*>): MMDAnimationHelper /* this */
    open fun remove(obj: SkinnedMesh<*, *>): MMDAnimationHelper /* this */
    open fun remove(obj: Camera): MMDAnimationHelper /* this */
    open fun remove(obj: Audio<*>): MMDAnimationHelper /* this */
    open fun update(delta: Number): MMDAnimationHelper /* this */
    open fun pose(mesh: SkinnedMesh<*, *>, vpd: Any?, params: MMDAnimationHelperPoseParameter = definedExternally): MMDAnimationHelper /* this */
    open fun enable(key: String, enabled: Boolean): MMDAnimationHelper /* this */
    open fun createGrantSolver(mesh: SkinnedMesh<*, *>): GrantSolver
}

external interface AudioManagerParameter {
    var delayTime: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class AudioManager(audio: Audio<*>, params: AudioManagerParameter = definedExternally) {
    open var audio: Audio<*>
    open var elapsedTime: Number
    open var currentTime: Number
    open var delayTime: Number
    open var audioDuration: Number
    open var duration: Number
    open fun control(delta: Number): AudioManager /* this */
}

open external class GrantSolver(mesh: SkinnedMesh<*, *>, grants: Array<Any?>) {
    open var mesh: SkinnedMesh<*, *>
    open var grants: Array<Any?>
    open fun update(): GrantSolver /* this */
    open fun updateOne(gran: Array<Any?>): GrantSolver /* this */
    open fun addGrantRotation(bone: Bone__0, q: Quaternion, ratio: Number): GrantSolver /* this */
}