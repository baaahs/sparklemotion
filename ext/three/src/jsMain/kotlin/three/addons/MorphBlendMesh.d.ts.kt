@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Material
import three.Mesh
import three.NormalOrGLBufferAttributes

open external class MorphBlendMesh(geometry: BufferGeometry<NormalOrGLBufferAttributes>, material: Material) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var animationsMap: Any?
    open var animationsList: Array<Any?>
    open fun createAnimation(name: String, start: Number, end: Number, fps: Number)
    open fun autoCreateAnimations(fps: Number)
    open fun setAnimationDirectionForward(name: String)
    open fun setAnimationDirectionBackward(name: String)
    open fun setAnimationFPS(name: String, fps: Number)
    open fun setAnimationDuration(name: String, duration: Number)
    open fun setAnimationWeight(name: String, weight: Number)
    open fun setAnimationTime(name: String, time: Number)
    open fun getAnimationTime(name: String): Number
    open fun getAnimationDuration(name: String): Number
    open fun playAnimation(name: String)
    open fun stopAnimation(name: String)
    open fun update(delta: Number)
}