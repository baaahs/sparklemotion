@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.DataView
import three.Object3D

external interface STLExporterOptionsBinary {
    var binary: Boolean
}

external interface STLExporterOptionsString {
    var binary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface STLExporterOptions {
    var binary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class STLExporter {
    open fun parse(scene: Object3D, options: STLExporterOptionsBinary): DataView
    open fun parse(scene: Object3D, options: STLExporterOptionsString = definedExternally): String
    open fun parse(scene: Object3D): dynamic /* String */
    open fun parse(scene: Object3D, options: STLExporterOptions = definedExternally): dynamic /* String | DataView */
}