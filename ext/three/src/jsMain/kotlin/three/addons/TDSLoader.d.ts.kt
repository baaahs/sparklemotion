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

external open class TDSLoader(manager: LoadingManager = definedExternally) : Loader__1<Group__0> {
    open var debug: Boolean
    open var group: Group__0
    override var manager: LoadingManager
    open var materials: Array<Material>
    open var meshes: Array<Mesh__0>
    open var position: Number
    open fun parse(arraybuffer: ArrayBuffer, path: String): Group__0
    open fun debugMessage(message: Any?)
    open fun endChunk(chunk: Any?)
    open fun nextChunk(data: DataView, chunk: Any?)
    open fun readByte(data: DataView): Number
    open fun readChunk(data: DataView): Any?
    open fun readColor(data: DataView): Color
    open fun readDWord(data: DataView): Number
    open fun readFaceArray(data: DataView, mesh: Mesh__0)
    open fun readFile(arraybuffer: ArrayBuffer, path: String)
    open fun readFloat(data: DataView): Number
    open fun readInt(data: DataView): Number
    open fun readMap(data: DataView, path: String): Texture
    open fun readMesh(data: DataView): Mesh__0
    open fun readMeshData(data: DataView, path: String)
    open fun readMaterialEntry(data: DataView, path: String)
    open fun readMaterialGroup(data: DataView): Any?
    open fun readNamedObject(data: DataView)
    open fun readShort(data: DataView): Number
    open fun readString(data: DataView, maxLength: Number): String
    open fun readWord(data: DataView): Number
    open fun resetPosition()
}