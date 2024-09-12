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

external interface `T$64` {
    var type: String /* "plane" */
}

external interface `T$65` {
    var alignment: String /* "horizontal" | "vertical" | "any" */
}

external interface `T$66` {
    var anchoring: `T$64`
    var planeAnchoring: `T$65`
}

external interface USDZExporterOptions {
    var ar: `T$66`?
        get() = definedExternally
        set(value) = definedExternally
    var includeAnchoringProperties: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var quickLookCompatible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxTextureSize: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class USDZExporter {
    open fun parse(scene: Object3D__0, onDone: (result: Uint8Array) -> Unit, onError: (error: Any) -> Unit, options: USDZExporterOptions = definedExternally)
    open fun parseAsync(scene: Object3D__0, options: USDZExporterOptions = definedExternally): Promise<Uint8Array>
}