@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebXRManager(renderer: Any, gl: WebGLRenderingContext) {
    open var enabled: Boolean
    open var isPresenting: Boolean
    open fun getController(id: Number): Group
    open fun getControllerGrip(id: Number): Group
    open fun setFramebufferScaleFactor(value: Number)
    open fun setReferenceSpaceType(value: String)
    open fun getReferenceSpace(): Any
    open fun getSession(): Any
    open fun setSession(value: Any)
    open fun getCamera(camera: Camera): Camera
    open fun setAnimationLoop(callback: Function<*>)
    open fun dispose()
}