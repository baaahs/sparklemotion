@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class SkinnedMesh<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, useVertexTexture: Boolean = definedExternally) : Mesh<TGeometry, TMaterial> {
    open var bindMode: String
    open var bindMatrix: Matrix4
    open var bindMatrixInverse: Matrix4
    open var skeleton: Skeleton
    open var isSkinnedMesh: Boolean
    open fun bind(skeleton: Skeleton, bindMatrix: Matrix4 = definedExternally)
    open fun pose()
    open fun normalizeSkinWeights()
    override fun updateMatrixWorld(force: Boolean)
}