@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.Float32Array

open external class Skeleton(bones: Array<Bone>, boneInverses: Array<Matrix4> = definedExternally) {
    open var useVertexTexture: Boolean
    open var bones: Array<Bone>
    open var boneMatrices: Float32Array
    open var boneTexture: DataTexture?
    open var boneInverses: Array<Matrix4>
    open fun calculateInverses(bone: Bone)
    open fun pose()
    open fun update()
    open fun clone(): Skeleton
    open fun getBoneByName(name: String): Bone?
    open fun dispose()
}