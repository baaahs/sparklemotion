@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGLCubeMaps(renderer: WebGLRenderer) {
    open fun get(texture: Any): Any
    open fun dispose()
}