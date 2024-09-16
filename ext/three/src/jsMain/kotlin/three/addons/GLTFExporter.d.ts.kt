@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.ErrorEvent
import three.*
import kotlin.js.Json
import kotlin.js.Promise

external interface GLTFExporterOptions {
    var trs: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var onlyVisible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var truncateDrawRange: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var binary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var embedImages: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxTextureSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var animations: Array<AnimationClip>?
        get() = definedExternally
        set(value) = definedExternally
    var forceIndices: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var includeCustomExtensions: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class GLTFExporter {
    open fun register(callback: (writer: GLTFWriter) -> GLTFExporterPlugin): GLTFExporter /* this */
    open fun unregister(callback: (writer: GLTFWriter) -> GLTFExporterPlugin): GLTFExporter /* this */
    open fun parse(input: Object3D, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit, options: GLTFExporterOptions = definedExternally)
    open fun parse(input: Object3D, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit)
    open fun parse(input: Array<Object3D>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit, options: GLTFExporterOptions = definedExternally)
    open fun parse(input: Array<Object3D>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit)
    open fun parseAsync(input: Object3D, options: GLTFExporterOptions = definedExternally): Promise<dynamic /* ArrayBuffer | Json */>
    open fun parseAsync(input: Object3D): Promise<dynamic /* ArrayBuffer | Json */>
    open fun parseAsync(input: Array<Object3D>, options: GLTFExporterOptions = definedExternally): Promise<dynamic /* ArrayBuffer | Json */>
    open fun parseAsync(input: Array<Object3D>): Promise<dynamic /* ArrayBuffer | Json */>
}

open external class GLTFWriter {
    open fun setPlugins(plugins: Array<GLTFExporterPlugin>)
    open fun write(input: Object3D, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, options: GLTFExporterOptions = definedExternally): Promise<Unit>
    open fun write(input: Object3D, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit): Promise<Unit>
    open fun write(input: Array<Object3D>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, options: GLTFExporterOptions = definedExternally): Promise<Unit>
    open fun write(input: Array<Object3D>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit): Promise<Unit>
}

external interface GLTFExporterPlugin {
    var writeTexture: ((map: Texture, textureDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var writeMaterial: ((material: Material, materialDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var writeMesh: ((mesh: Mesh<*, *>, meshDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var writeNode: ((obj: Object3D, nodeDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var beforeParse: ((input: dynamic /* Object3D | Array<Object3D> */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var afterParse: ((input: dynamic /* Object3D | Array<Object3D> */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}