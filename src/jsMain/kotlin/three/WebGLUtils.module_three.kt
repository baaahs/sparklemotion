@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLUtils {
    constructor(gl: WebGLRenderingContext, extensions: Any, capabilities: Any)
    constructor(gl: WebGL2RenderingContext, extensions: Any, capabilities: Any)
    open fun convert(p: Any)
}