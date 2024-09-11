@file:JsModule("three")
@file:JsNonModule
package three.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

open external class WebGLUniforms(gl: WebGLRenderingContext, program: WebGLProgram) {
    open fun setValue(gl: WebGLRenderingContext, name: String, value: Any, textures: WebGLTextures)
    open fun setOptional(gl: WebGLRenderingContext, obj: Any, name: String)

    companion object {
        fun upload(gl: WebGLRenderingContext, seq: Any, values: Array<Any>, textures: WebGLTextures)
        fun seqWithValue(seq: Any, values: Array<Any>): Array<Any>
    }
}