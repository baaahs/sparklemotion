@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.*

open external class LoaderUtils {
    companion object {
        fun decodeText(array: Int8Array): String
        fun decodeText(array: Uint8Array): String
        fun decodeText(array: Uint8ClampedArray): String
        fun decodeText(array: Int16Array): String
        fun decodeText(array: Uint16Array): String
        fun decodeText(array: Int32Array): String
        fun decodeText(array: Uint32Array): String
        fun decodeText(array: Float32Array): String
        fun decodeText(array: Float64Array): String
        fun extractUrlBase(url: String): String
    }
}