@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class DirectionalLightShadow : LightShadow {
    override var camera: /*Orthographic*/Camera
    open var isDirectionalLightShadow: Boolean
}