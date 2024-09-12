@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external open class KTX2Exporter {
    open fun parse(arg1: Data3DTexture, arg2: WebGLRenderTarget__0 = definedExternally): Uint8Array
    open fun parse(arg1: Data3DTexture): Uint8Array
    open fun parse(arg1: DataTexture, arg2: WebGLRenderTarget__0 = definedExternally): Uint8Array
    open fun parse(arg1: DataTexture): Uint8Array
    open fun parse(arg1: WebGLRenderer, arg2: WebGLRenderTarget__0 = definedExternally): Uint8Array
    open fun parse(arg1: WebGLRenderer): Uint8Array
}