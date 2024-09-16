@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import three.*

external interface `T$85` {
    var x: Number
    var y: Number
    var z: Number
}

external interface Chunk {
    var palette: Array<Number>
    var size: `T$85`
    var data: Uint8Array
}

open external class VOXLoader(manager: LoadingManager = definedExternally) : Loader__1<Array<Chunk>> {
    open fun parse(data: ArrayBuffer): Array<Any?>
}

open external class VOXMesh(chunk: Chunk) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material>

open external class VOXData3DTexture(chunk: Chunk) : Data3DTexture