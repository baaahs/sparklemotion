@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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