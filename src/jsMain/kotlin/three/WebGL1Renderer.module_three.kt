@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGL1Renderer(parameters: WebGLRendererParameters = definedExternally) : WebGLRenderer {
    open var isWebGL1Renderer: Boolean
}