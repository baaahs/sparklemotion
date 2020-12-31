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

external interface `T$24` {
    var uuid: String
    var buffer: String
    var type: String
    var stride: Number
}

open external class InterleavedBuffer(array: ArrayLike<Number>, stride: Number) {
    open var array: ArrayLike<Number>
    open var stride: Number
    open var usage: Usage
    open var updateRange: `T$1`
    open var version: Number
    open var length: Number
    open var count: Number
    open var needsUpdate: Boolean
    open var uuid: String
    open fun setUsage(usage: Usage): InterleavedBuffer
    open fun clone(data: Any?): InterleavedBuffer /* this */
    open fun copy(source: InterleavedBuffer): InterleavedBuffer /* this */
    open fun copyAt(index1: Number, attribute: InterleavedBufferAttribute, index2: Number): InterleavedBuffer
    open fun set(value: ArrayLike<Number>, index: Number): InterleavedBuffer
    open fun toJSON(data: Any?): `T$24`
}