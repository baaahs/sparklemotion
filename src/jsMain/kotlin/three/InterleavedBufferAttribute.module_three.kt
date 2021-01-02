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

external interface `T$23` {
    var isInterleavedBufferAttribute: Boolean
    var itemSize: Number
    var data: String
    var offset: Number
    var normalized: Boolean
}

open external class InterleavedBufferAttribute(interleavedBuffer: InterleavedBuffer, itemSize: Number, offset: Number, normalized: Boolean = definedExternally) {
    open var name: String
    open var data: InterleavedBuffer
    open var itemSize: Number
    open var offset: Number
    open var normalized: Boolean
    open var isInterleavedBufferAttribute: Boolean
    open fun applyMatrix4(m: Matrix4): InterleavedBufferAttribute /* this */
    open fun clone(data: Any? = definedExternally): BufferAttribute
    open fun getX(index: Number): Number
    open fun setX(index: Number, x: Number): InterleavedBufferAttribute
    open fun getY(index: Number): Number
    open fun setY(index: Number, y: Number): InterleavedBufferAttribute
    open fun getZ(index: Number): Number
    open fun setZ(index: Number, z: Number): InterleavedBufferAttribute
    open fun getW(index: Number): Number
    open fun setW(index: Number, z: Number): InterleavedBufferAttribute
    open fun setXY(index: Number, x: Number, y: Number): InterleavedBufferAttribute
    open fun setXYZ(index: Number, x: Number, y: Number, z: Number): InterleavedBufferAttribute
    open fun setXYZW(index: Number, x: Number, y: Number, z: Number, w: Number): InterleavedBufferAttribute
    open fun toJSON(data: Any? = definedExternally): `T$23`
}