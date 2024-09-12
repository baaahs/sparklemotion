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

external open class GLTFExporter {
    open fun register(callback: (writer: GLTFWriter) -> GLTFExporterPlugin): GLTFExporter /* this */
    open fun unregister(callback: (writer: GLTFWriter) -> GLTFExporterPlugin): GLTFExporter /* this */
    open fun parse(input: Object3D__0, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit, options: GLTFExporterOptions = definedExternally)
    open fun parse(input: Object3D__0, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit)
    open fun parse(input: Array<Object3D__0>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit, options: GLTFExporterOptions = definedExternally)
    open fun parse(input: Array<Object3D__0>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, onError: (error: ErrorEvent) -> Unit)
    open fun parseAsync(input: Object3D__0, options: GLTFExporterOptions = definedExternally): Promise<dynamic /* ArrayBuffer | Json */>
    open fun parseAsync(input: Object3D__0): Promise<dynamic /* ArrayBuffer | Json */>
    open fun parseAsync(input: Array<Object3D__0>, options: GLTFExporterOptions = definedExternally): Promise<dynamic /* ArrayBuffer | Json */>
    open fun parseAsync(input: Array<Object3D__0>): Promise<dynamic /* ArrayBuffer | Json */>
}

external open class GLTFWriter {
    open fun setPlugins(plugins: Array<GLTFExporterPlugin>)
    open fun write(input: Object3D__0, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, options: GLTFExporterOptions = definedExternally): Promise<Unit>
    open fun write(input: Object3D__0, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit): Promise<Unit>
    open fun write(input: Array<Object3D__0>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit, options: GLTFExporterOptions = definedExternally): Promise<Unit>
    open fun write(input: Array<Object3D__0>, onDone: (gltf: Any /* ArrayBuffer | Json */) -> Unit): Promise<Unit>
}

external interface GLTFExporterPlugin {
    var writeTexture: ((map: Texture, textureDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var writeMaterial: ((material: Material, materialDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var writeMesh: ((mesh: Mesh__0, meshDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var writeNode: ((obj: Object3D__0, nodeDef: Json) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var beforeParse: ((input: dynamic /* Object3D__0 | Array<Object3D__0> */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var afterParse: ((input: dynamic /* Object3D__0 | Array<Object3D__0> */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}