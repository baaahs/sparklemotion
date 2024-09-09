@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLBufferRenderer(gl: WebGLRenderingContext, extensions: WebGLExtensions, info: WebGLInfo, capabilities: WebGLCapabilities) {
    open fun setMode(value: Any)
    open fun render(start: Any, count: Number)
    open fun renderInstances(start: Any, count: Number, primcount: Number)
}