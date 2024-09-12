@file:JsModule("three")
@file:JsNonModule
package three

external interface `T$88` {
    @nativeGetter
    operator fun get(id: String): Array<Number>?
    @nativeSetter
    operator fun set(id: String, value: Array<Number>)
}

open external class CameraHelper(camera: Camera) : LineSegments<BufferGeometry<NormalBufferAttributes>, dynamic> {
    override var override: Any
    override val type: String /* String | "CameraHelper" */
    open var camera: Camera
    open var pointMap: `T$88`
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open fun setColors(frustum: Color, cone: Color, up: Color, target: Color, cross: Color): CameraHelper /* this */
    open fun update()
    open fun dispose()
}