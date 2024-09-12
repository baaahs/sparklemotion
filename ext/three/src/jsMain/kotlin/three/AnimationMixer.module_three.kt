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

open external class AnimationMixer : EventDispatcher/*<AnimationMixerEventMap>*/ {
    constructor(root: Object3D/*<Object3DEventMap>*/)
    constructor(root: AnimationObjectGroup)
    open var time: Number
    open var timeScale: Number
    open fun clipAction(clip: AnimationClip, root: Object3D/*<Object3DEventMap>*/ = definedExternally, blendMode: Any = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip): AnimationAction
    open fun clipAction(clip: AnimationClip, root: Object3D/*<Object3DEventMap>*/ = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally, blendMode: Any = definedExternally): AnimationAction
    open fun clipAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally): AnimationAction
    open fun existingAction(clip: AnimationClip, root: Object3D/*<Object3DEventMap>*/ = definedExternally): AnimationAction?
    open fun existingAction(clip: AnimationClip): AnimationAction?
    open fun existingAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally): AnimationAction?
    open fun stopAllAction(): AnimationMixer
    open fun update(deltaTime: Number): AnimationMixer
    open fun setTime(timeInSeconds: Number): AnimationMixer
    open fun getRoot(): dynamic /* Object3D/*<Object3DEventMap>*/ | AnimationObjectGroup */
    open fun uncacheClip(clip: AnimationClip)
    open fun uncacheRoot(root: Object3D/*<Object3DEventMap>*/)
    open fun uncacheRoot(root: AnimationObjectGroup)
    open fun uncacheAction(clip: AnimationClip, root: Object3D/*<Object3DEventMap>*/ = definedExternally)
    open fun uncacheAction(clip: AnimationClip)
    open fun uncacheAction(clip: AnimationClip, root: AnimationObjectGroup = definedExternally)
}