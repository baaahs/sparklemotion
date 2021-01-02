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

open external class AnimationMixer : EventDispatcher {
    constructor(root: Object3D)
    constructor(root: AnimationObjectGroup)
    open var time: Number
    open var timeScale: Number
    open fun clipAction(clip: AnimationClip, root: Object3D = definedExternally, blendMode: AnimationBlendMode = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally, blendMode: AnimationBlendMode = definedExternally): AnimationAction
    open fun existingAction(clip: AnimationClip, root: Object3D = definedExternally): AnimationAction?
    open fun existingAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally): AnimationAction?
    open fun stopAllAction(): AnimationMixer
    open fun update(deltaTime: Number): AnimationMixer
    open fun setTime(timeInSeconds: Number): AnimationMixer
    open fun getRoot(): dynamic /* Object3D | AnimationObjectGroup */
    open fun uncacheClip(clip: AnimationClip)
    open fun uncacheRoot(root: Object3D)
    open fun uncacheRoot(root: AnimationObjectGroup)
    open fun uncacheAction(clip: AnimationClip, root: Object3D = definedExternally)
    open fun uncacheAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally)
}