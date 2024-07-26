@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLExtensions(gl: WebGLRenderingContext) {
    open fun has(name: String): Boolean
    open fun get(name: String): Any
}