@file:JsModule("three")
@file:JsNonModule
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

open external class WebGL3DRenderTarget(width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally, options: RenderTargetOptions = definedExternally) : WebGLRenderTarget<Texture> {
//    override var textures: Array<Data3DTexture>
    open val isWebGL3DRenderTarget: Boolean
}