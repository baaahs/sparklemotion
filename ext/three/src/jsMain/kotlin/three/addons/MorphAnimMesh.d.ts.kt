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

external open class MorphAnimMesh(geometry: BufferGeometry__0, material: Material) : Mesh__0 {
    open var mixer: AnimationMixer
    open var activeAction: AnimationAction?
    open fun setDirectionForward()
    open fun setDirectionBackward()
    open fun playAnimation(label: String, fps: Number)
    open fun updateAnimation(delta: Number)
    open fun copy(source: MorphAnimMesh, recursive: Boolean = definedExternally): MorphAnimMesh /* this */
}