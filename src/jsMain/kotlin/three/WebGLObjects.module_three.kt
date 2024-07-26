@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLObjects(gl: WebGLRenderingContext, geometries: Any, attributes: Any, info: Any) {
    open fun update(obj: Any): Any
    open fun dispose()
}