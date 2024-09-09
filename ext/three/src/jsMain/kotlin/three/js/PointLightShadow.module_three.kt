@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PointLightShadow : LightShadow {
    override var camera: /*Perspective*/Camera
}