@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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