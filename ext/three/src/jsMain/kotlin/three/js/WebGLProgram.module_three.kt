@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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