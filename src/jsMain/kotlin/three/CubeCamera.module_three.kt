@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class CubeCamera(near: Number, far: Number, renderTarget: WebGLCubeRenderTarget) : Object3D {
    override var type: String /* 'CubeCamera' */
    open var renderTarget: WebGLCubeRenderTarget
    open fun update(renderer: WebGLRenderer, scene: Scene)
    open fun clear(renderer: WebGLRenderer, color: Boolean, depth: Boolean, stencil: Boolean)
}