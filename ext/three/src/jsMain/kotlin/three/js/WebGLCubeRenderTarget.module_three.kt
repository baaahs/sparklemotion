@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGLCubeRenderTarget(size: Number, options: WebGLRenderTargetOptions = definedExternally) : WebGLRenderTarget {
    open fun fromEquirectangularTexture(renderer: WebGLRenderer, texture: Texture): WebGLCubeRenderTarget /* this */
}