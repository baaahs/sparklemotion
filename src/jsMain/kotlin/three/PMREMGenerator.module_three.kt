@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PMREMGenerator(renderer: WebGLRenderer) {
    open fun fromScene(scene: Scene, sigma: Number = definedExternally, near: Number = definedExternally, far: Number = definedExternally): WebGLRenderTarget
    open fun fromEquirectangular(equirectangular: Texture): WebGLRenderTarget
    open fun fromCubemap(cubemap: CubeTexture): WebGLRenderTarget
    open fun compileCubemapShader()
    open fun compileEquirectangularShader()
    open fun dispose()
}