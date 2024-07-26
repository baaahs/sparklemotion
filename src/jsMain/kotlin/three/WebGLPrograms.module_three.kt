@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGLPrograms(renderer: WebGLRenderer, cubemaps: WebGLCubeMaps, extensions: WebGLExtensions, capabilities: WebGLCapabilities, bindingStates: WebGLBindingStates, clipping: WebGLClipping) {
    open var programs: Array<WebGLProgram>
    open fun getParameters(material: Material, lights: Any, shadows: Array<Any?>, scene: Scene, obj: Any): Any
    open fun getProgramCacheKey(parameters: Any): String
    open fun getUniforms(material: Material): Any?
    open fun acquireProgram(parameters: Any, cacheKey: String): WebGLProgram
    open fun releaseProgram(program: WebGLProgram)
}