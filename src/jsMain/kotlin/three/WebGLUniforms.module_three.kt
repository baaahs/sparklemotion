@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLUniforms(gl: WebGLRenderingContext, program: WebGLProgram) {
    open fun setValue(gl: WebGLRenderingContext, name: String, value: Any, textures: WebGLTextures)
    open fun setOptional(gl: WebGLRenderingContext, obj: Any, name: String)

    companion object {
        fun upload(gl: WebGLRenderingContext, seq: Any, values: Array<Any>, textures: WebGLTextures)
        fun seqWithValue(seq: Any, values: Array<Any>): Array<Any>
    }
}