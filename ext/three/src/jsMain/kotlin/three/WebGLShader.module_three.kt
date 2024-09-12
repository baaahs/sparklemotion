@file:JsModule("three")
@file:JsNonModule
package three

import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLShader

external fun WebGLShader(gl: WebGLRenderingContext, type: String, string: String): WebGLShader