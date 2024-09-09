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

external interface `T$66` {
    var action: AnimationAction
    var loopDelta: Number
}

external interface `T$67` {
    var action: AnimationAction
    var direction: Number
}

external interface AnimationMixerEventMap {
    var loop: `T$66`
    var finished: `T$67`
}

external open class AnimationMixer : EventDispatcher<AnimationMixerEventMap> {
    constructor(root: Object3D__0)
    constructor(root: AnimationObjectGroup)
    open var time: Number
    open var timeScale: Number
    open fun clipAction(clip: AnimationClip, root: Object3D__0 = definedExternally, blendMode: Any = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip): AnimationAction
    open fun clipAction(clip: AnimationClip, root: Object3D__0 = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally, blendMode: Any = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally): AnimationAction
    open fun existingAction(clip: AnimationClip, root: Object3D__0 = definedExternally): AnimationAction?
    open fun existingAction(clip: AnimationClip): AnimationAction?
    open fun existingAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally): AnimationAction?
    open fun stopAllAction(): AnimationMixer
    open fun update(deltaTime: Number): AnimationMixer
    open fun setTime(timeInSeconds: Number): AnimationMixer
    open fun getRoot(): dynamic /* Object3D__0 | AnimationObjectGroup */
    open fun uncacheClip(clip: AnimationClip)
    open fun uncacheRoot(root: Object3D__0)
    open fun uncacheRoot(root: AnimationObjectGroup)
    open fun uncacheAction(clip: AnimationClip, root: Object3D__0 = definedExternally)
    open fun uncacheAction(clip: AnimationClip)
    open fun uncacheAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally)
}