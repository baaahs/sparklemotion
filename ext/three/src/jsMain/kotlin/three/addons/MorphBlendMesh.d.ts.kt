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

external open class MorphBlendMesh(geometry: BufferGeometry__0, material: Material) : Mesh__0 {
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