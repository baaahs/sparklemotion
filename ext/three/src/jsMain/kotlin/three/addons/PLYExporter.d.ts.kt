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

external interface PLYExporterOptionsBase {
    var excludeAttributes: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var littleEndian: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PLYExporterOptionsBinary : PLYExporterOptionsBase {
    var binary: Boolean
}

external interface PLYExporterOptionsString : PLYExporterOptionsBase {
    var binary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PLYExporterOptions : PLYExporterOptionsBase {
    var binary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class PLYExporter {
    open fun parse(obj: Object3D__0, onDone: (res: ArrayBuffer) -> Unit, options: PLYExporterOptionsBinary): ArrayBuffer?
    open fun parse(obj: Object3D__0, onDone: (res: String) -> Unit, options: PLYExporterOptionsString = definedExternally): String?
    open fun parse(obj: Object3D__0, onDone: (res: String) -> Unit): String?
    open fun parse(obj: Object3D__0, onDone: (res: Any /* String | ArrayBuffer */) -> Unit, options: PLYExporterOptions = definedExternally): dynamic /* String? | ArrayBuffer? */
    open fun parse(obj: Object3D__0, onDone: (res: Any /* String | ArrayBuffer */) -> Unit): dynamic /* String? | ArrayBuffer? */
}