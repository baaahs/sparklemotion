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

external open class VOXLoader(manager: LoadingManager = definedExternally) : Loader__1<Array<Chunk>> {
    open fun parse(data: ArrayBuffer): Array<Any?>
}

external open class VOXMesh(chunk: Chunk) : Mesh__0

external open class VOXData3DTexture(chunk: Chunk) : Data3DTexture