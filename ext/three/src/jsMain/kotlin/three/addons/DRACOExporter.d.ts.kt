@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.Int8Array
import three.Mesh
import three.Points

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

open external class DRACOExporter {
    open fun parse(obj: Mesh<*, *>, options: DRACOExporterOptions = definedExternally): Int8Array
    open fun parse(obj: Mesh<*, *>): Int8Array
    open fun parse(obj: Points<*, *>, options: DRACOExporterOptions = definedExternally): Int8Array
    open fun parse(obj: Points<*, *>): Int8Array
}