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

external interface UltraHDRLoaderTextureData {
    var width: Number
    var height: Number
    var hdrBuffer: dynamic /* Uint16Array | Float32Array */
        get() = definedExternally
        set(value) = definedExternally
    var format: Any
    var type: Any
}

external open class UltraHDRLoader(manager: LoadingManager = definedExternally) : Loader__1<DataTexture> {
    open var type: Any
    open fun setDataType(value: Any): UltraHDRLoader /* this */
    open fun parse(buffer: ArrayBuffer, onLoad: (texData: UltraHDRLoaderTextureData) -> Unit)
}