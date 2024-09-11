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

open external class WebGLProgram(renderer: WebGLRenderer, cacheKey: String, parameters: Any?) {
    open var name: String
    open var id: Number
    open var cacheKey: String
    open var usedTimes: Number
    open var program: Any
    open var vertexShader: WebGLShader
    open var fragmentShader: WebGLShader
    open var uniforms: Any
    open var attributes: Any
    open fun getUniforms(): WebGLUniforms
    open fun getAttributes(): Any
    open fun destroy()
}