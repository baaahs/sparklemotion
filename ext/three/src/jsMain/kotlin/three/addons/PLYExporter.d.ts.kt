@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.ArrayBuffer
import three.Object3D

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

open external class PLYExporter {
    open fun parse(obj: Object3D, onDone: (res: ArrayBuffer) -> Unit, options: PLYExporterOptionsBinary): ArrayBuffer?
    open fun parse(obj: Object3D, onDone: (res: String) -> Unit, options: PLYExporterOptionsString = definedExternally): String?
    open fun parse(obj: Object3D, onDone: (res: String) -> Unit): String?
    open fun parse(obj: Object3D, onDone: (res: Any /* String | ArrayBuffer */) -> Unit, options: PLYExporterOptions = definedExternally): dynamic /* String? | ArrayBuffer? */
    open fun parse(obj: Object3D, onDone: (res: Any /* String | ArrayBuffer */) -> Unit): dynamic /* String? | ArrayBuffer? */
}