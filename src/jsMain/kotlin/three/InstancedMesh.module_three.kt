@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class InstancedMesh<TGeometry, TMaterial>(geometry: TGeometry, material: TMaterial, count: Number) : Mesh<TGeometry, TMaterial> {
    open var count: Number
    open var instanceMatrix: BufferAttribute
    open var isInstancedMesh: Boolean
    open fun getMatrixAt(index: Number, matrix: Matrix4)
    open fun setMatrixAt(index: Number, matrix: Matrix4)
}