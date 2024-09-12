@file:JsModule("three")
@file:JsNonModule
package three

import org.khronos.webgl.WebGLRenderingContext
import web.gl.WebGL2RenderingContext

open external class WebGLUtils {
    constructor(gl: WebGLRenderingContext, extensions: WebGLExtensions)
    constructor(gl: WebGL2RenderingContext, extensions: WebGLExtensions)
    open fun convert(p: Any, colorSpace: Any = definedExternally): Number?
}