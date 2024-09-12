@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.Uint8Array
import three.DataTexture
import three.WebGLRenderTarget
import three.WebGLRenderer

external var NO_COMPRESSION: Number /* 0 */

external var ZIPS_COMPRESSION: Number /* 2 */

external var ZIP_COMPRESSION: Number /* 3 */

external interface EXRExporterParseOptions {
    var compression: Number?
        get() = definedExternally
        set(value) = definedExternally
    var type: Any?
        get() = definedExternally
        set(value) = definedExternally
}

open external class EXRExporter {
    open fun parse(renderer: WebGLRenderer, renderTarget: WebGLRenderTarget<*>, options: EXRExporterParseOptions = definedExternally): Uint8Array
    open fun parse(renderer: WebGLRenderer, renderTarget: WebGLRenderTarget<*>): Uint8Array
    open fun parse(dataTexture: DataTexture, options: EXRExporterParseOptions = definedExternally): Uint8Array
    open fun parse(dataTexture: DataTexture): Uint8Array
}