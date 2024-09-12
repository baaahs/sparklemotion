@file:JsModule("three")
@file:JsNonModule
package three

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

open external class WebGLTextures(gl: WebGLRenderingContext, extensions: WebGLExtensions, state: WebGLState, properties: WebGLProperties, capabilities: WebGLCapabilities, utils: WebGLUtils, info: WebGLInfo) {
    open fun allocateTextureUnit()
    open fun resetTextureUnits()
    open fun setTexture2D(texture: Any, slot: Number)
    open fun setTexture2DArray(texture: Any, slot: Number)
    open fun setTexture3D(texture: Any, slot: Number)
    open fun setTextureCube(texture: Any, slot: Number)
    open fun setupRenderTarget(renderTarget: Any)
    open fun updateRenderTargetMipmap(renderTarget: Any)
    open fun updateMultisampleRenderTarget(renderTarget: Any)
    open fun safeSetTexture2D(texture: Any, slot: Number)
    open fun safeSetTextureCube(texture: Any, slot: Number)
}