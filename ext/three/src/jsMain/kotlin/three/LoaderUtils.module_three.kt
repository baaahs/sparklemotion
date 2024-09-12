@file:JsModule("three")
@file:JsNonModule
package three

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView

open external class LoaderUtils {
    companion object {
        fun decodeText(array: ArrayBufferView): String
        fun decodeText(array: ArrayBuffer): String
        fun extractUrlBase(url: String): String
        fun resolveURL(url: String, path: String): String
    }
}