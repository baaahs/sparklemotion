package three.addons

import three.*

open external class MorphAnimMesh(geometry: BufferGeometry<NormalOrGLBufferAttributes>, material: Material) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var mixer: AnimationMixer
    open var activeAction: AnimationAction?
    open fun setDirectionForward()
    open fun setDirectionBackward()
    open fun playAnimation(label: String, fps: Number)
    open fun updateAnimation(delta: Number)
    open fun copy(source: MorphAnimMesh, recursive: Boolean = definedExternally): MorphAnimMesh /* this */
}