@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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

open external class PMREMGenerator(renderer: WebGLRenderer) {
    open fun fromScene(scene: Scene, sigma: Number = definedExternally, near: Number = definedExternally, far: Number = definedExternally): WebGLRenderTarget<Texture>
    open fun fromEquirectangular(equirectangular: Texture, renderTarget: WebGLRenderTarget<Texture>? = definedExternally): WebGLRenderTarget<Texture>
    open fun fromCubemap(cubemap: CubeTexture, renderTarget: WebGLRenderTarget<Texture>? = definedExternally): WebGLRenderTarget<Texture>
    open fun compileCubemapShader()
    open fun compileEquirectangularShader()
    open fun dispose()
}