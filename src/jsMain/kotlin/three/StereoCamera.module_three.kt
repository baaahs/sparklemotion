@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class StereoCamera : Camera {
    override var type: String /* 'StereoCamera' */
    open var aspect: Number
    open var eyeSep: Number
    open var cameraL: PerspectiveCamera
    open var cameraR: PerspectiveCamera
    open fun update(camera: PerspectiveCamera)
}