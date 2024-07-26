@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class SpotLightShadow : LightShadow {
    override var camera: /*Perspective*/Camera
    open var isSpotLightShadow: Boolean
}