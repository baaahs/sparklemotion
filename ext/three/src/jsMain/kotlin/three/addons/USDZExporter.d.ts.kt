package three.addons

import org.khronos.webgl.Uint8Array
import three.Object3D
import kotlin.js.Promise

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

open external class USDZExporter {
    open fun parse(scene: Object3D, onDone: (result: Uint8Array) -> Unit, onError: (error: Any) -> Unit, options: USDZExporterOptions = definedExternally)
    open fun parseAsync(scene: Object3D, options: USDZExporterOptions = definedExternally): Promise<Uint8Array>
}