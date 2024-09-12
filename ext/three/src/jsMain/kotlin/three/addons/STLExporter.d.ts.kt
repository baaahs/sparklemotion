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

external open class STLExporter {
    open fun parse(scene: Object3D__0, options: STLExporterOptionsBinary): DataView
    open fun parse(scene: Object3D__0, options: STLExporterOptionsString = definedExternally): String
    open fun parse(scene: Object3D__0): dynamic /* String */
    open fun parse(scene: Object3D__0, options: STLExporterOptions = definedExternally): dynamic /* String | DataView */
}