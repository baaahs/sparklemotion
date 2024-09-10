package three.js

open external class Camera : Object3D__0 {
    open val isCamera: Boolean
    open var override: Any
    override val type: String /* String | "Camera" */
    override var layers: Layers
    open var matrixWorldInverse: Matrix4
    open var projectionMatrix: Matrix4
    open var projectionMatrixInverse: Matrix4
    open var coordinateSystem: Any
    open var viewport: Vector4
    override fun getWorldDirection(target: Vector3): Vector3
}