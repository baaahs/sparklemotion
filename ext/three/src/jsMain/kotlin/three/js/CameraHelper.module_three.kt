@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$51` {
    @nativeGetter
    operator fun get(id: String): Array<Number>?
    @nativeSetter
    operator fun set(id: String, value: Array<Number>)
}

open external class CameraHelper(camera: Camera) : LineSegments<dynamic, dynamic> {
    open var camera: Camera
    open var pointMap: `T$51`
    override var type: String
    open fun update()
}