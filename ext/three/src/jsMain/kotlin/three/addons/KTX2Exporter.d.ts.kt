package three.addons

import org.khronos.webgl.Uint8Array
import three.Data3DTexture
import three.DataTexture
import three.WebGLRenderTarget
import three.WebGLRenderer

open external class KTX2Exporter {
    open fun parse(arg1: Data3DTexture, arg2: WebGLRenderTarget<*> = definedExternally): Uint8Array
    open fun parse(arg1: Data3DTexture): Uint8Array
    open fun parse(arg1: DataTexture, arg2: WebGLRenderTarget<*> = definedExternally): Uint8Array
    open fun parse(arg1: DataTexture): Uint8Array
    open fun parse(arg1: WebGLRenderer, arg2: WebGLRenderTarget<*> = definedExternally): Uint8Array
    open fun parse(arg1: WebGLRenderer): Uint8Array
}