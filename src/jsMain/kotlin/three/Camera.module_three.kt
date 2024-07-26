@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Camera : Object3D {
    open var matrixWorldInverse: Matrix4
    open var projectionMatrix: Matrix4
    open var projectionMatrixInverse: Matrix4
    open var isCamera: Boolean
    override fun getWorldDirection(target: Vector3): Vector3
    override fun updateMatrixWorld(force: Boolean)
}