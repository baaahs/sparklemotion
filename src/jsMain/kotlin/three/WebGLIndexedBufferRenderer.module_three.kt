@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLIndexedBufferRenderer(gl: WebGLRenderingContext, extensions: Any, info: Any, capabilities: Any) {
    open fun setMode(value: Any)
    open fun setIndex(index: Any)
    open fun render(start: Any, count: Number)
    open fun renderInstances(start: Any, count: Number, primcount: Number)
}