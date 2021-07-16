package baaahs.fixtures

import baaahs.ui.nuffin
import com.danielgergely.kgl.Buffer
import com.danielgergely.kgl.ByteBuffer
import kotlinext.js.jsObject
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array

actual fun nuffinBuffer(): Buffer = ByteBuffer(Uint8Array(0).also {
    it.asDynamic().subarray = fun(offset: Int): ArrayBufferView? {
        return null
    }
}).also {
}

object NuffinABV : ArrayBufferView {
    override val buffer: ArrayBuffer
        get() = jsObject<ArrayBuffer>().apply {
            asDynamic().subarray = fun(offset: Int): ArrayBufferView {
                return nuffin()
            }
        }
    override val byteLength: Int
        get() = 0
    override val byteOffset: Int
        get() = 0

}

//class NuffinBuffer : Buffer(NuffinABV) {
//    override val size: Int
//        get() = 0
//    override val sizeInBytes: Int
//        get() = 0
//
//}