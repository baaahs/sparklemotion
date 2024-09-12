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

external open class EXRExporter {
    open fun parse(renderer: WebGLRenderer, renderTarget: WebGLRenderTarget__0, options: EXRExporterParseOptions = definedExternally): Uint8Array
    open fun parse(renderer: WebGLRenderer, renderTarget: WebGLRenderTarget__0): Uint8Array
    open fun parse(dataTexture: DataTexture, options: EXRExporterParseOptions = definedExternally): Uint8Array
    open fun parse(dataTexture: DataTexture): Uint8Array
}