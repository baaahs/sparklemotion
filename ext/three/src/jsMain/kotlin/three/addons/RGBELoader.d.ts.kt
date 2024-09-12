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

external interface RGBE {
    var width: Number
    var height: Number
    var data: dynamic /* Float32Array | Uint8Array */
        get() = definedExternally
        set(value) = definedExternally
    var header: String
    var gamma: Number
    var exposure: Number
    var type: Any
}

external open class RGBELoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open var type: Any
    open fun parse(buffer: ArrayBuffer): RGBE
    open fun setDataType(type: Any): RGBELoader /* this */
}