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

external interface DRACOExporterOptions {
    var decodeSpeed: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encodeSpeed: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encoderMethod: Number?
        get() = definedExternally
        set(value) = definedExternally
    var quantization: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var exportUvs: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var exportNormals: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var exportColor: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class DRACOExporter {
    open fun parse(obj: Mesh__0, options: DRACOExporterOptions = definedExternally): Int8Array
    open fun parse(obj: Mesh__0): Int8Array
    open fun parse(obj: Points__0, options: DRACOExporterOptions = definedExternally): Int8Array
    open fun parse(obj: Points__0): Int8Array
}