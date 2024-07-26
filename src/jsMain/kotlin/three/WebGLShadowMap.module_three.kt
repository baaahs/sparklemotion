@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGLShadowMap(_renderer: WebGLRenderer, _objects: WebGLObjects, maxTextureSize: Number) {
    open var enabled: Boolean
    open var autoUpdate: Boolean
    open var needsUpdate: Boolean
    open var type: ShadowMapType
    open fun render(shadowsArray: Array<Light>, scene: Scene, camera: Camera)
    open var cullFace: Any
}