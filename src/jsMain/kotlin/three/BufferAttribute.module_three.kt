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

external interface `T$1` {
    var offset: Number
    var count: Number
}

external interface `T$2` {
    var r: Number
    var g: Number
    var b: Number
}

external interface `T$3` {
    var x: Number
    var y: Number
}

external interface `T$4` {
    var x: Number
    var y: Number
    var z: Number
}

external interface `T$5` {
    var x: Number
    var y: Number
    var z: Number
    var w: Number
}

external interface `T$6` {
    var itemSize: Number
    var type: String
    var array: Array<Number>
    var normalized: Boolean
}

open external class BufferAttribute(array: ArrayLike<Number>, itemSize: Number, normalized: Boolean = definedExternally) {
    open var name: String
    open var array: ArrayLike<Number>
    open var itemSize: Number
    open var usage: Usage
    open var updateRange: `T$1`
    open var version: Number
    open var normalized: Boolean
    open var count: Number
    open var isBufferAttribute: Boolean
    open var onUploadCallback: () -> Unit
    open fun onUpload(callback: () -> Unit): BufferAttribute /* this */
    open fun setUsage(usage: Usage): BufferAttribute /* this */
    open fun clone(): BufferAttribute
    open fun copy(source: BufferAttribute): BufferAttribute /* this */
    open fun copyAt(index1: Number, attribute: BufferAttribute, index2: Number): BufferAttribute /* this */
    open fun copyArray(array: ArrayLike<Number>): BufferAttribute /* this */
    open fun copyColorsArray(colors: Array<`T$2`>): BufferAttribute /* this */
    open fun copyVector2sArray(vectors: Array<`T$3`>): BufferAttribute /* this */
    open fun copyVector3sArray(vectors: Array<`T$4`>): BufferAttribute /* this */
    open fun copyVector4sArray(vectors: Array<`T$5`>): BufferAttribute /* this */
    open fun applyMatrix3(m: Matrix3): BufferAttribute /* this */
    open fun applyMatrix4(m: Matrix4): BufferAttribute /* this */
    open fun applyNormalMatrix(m: Matrix3): BufferAttribute /* this */
    open fun transformDirection(m: Matrix4): BufferAttribute /* this */
    open fun set(value: ArrayLike<Number>, offset: Number = definedExternally): BufferAttribute /* this */
    open fun set(value: ArrayBufferView, offset: Number = definedExternally): BufferAttribute /* this */
    open fun getX(index: Number): Number
    open fun setX(index: Number, x: Number): BufferAttribute /* this */
    open fun getY(index: Number): Number
    open fun setY(index: Number, y: Number): BufferAttribute /* this */
    open fun getZ(index: Number): Number
    open fun setZ(index: Number, z: Number): BufferAttribute /* this */
    open fun getW(index: Number): Number
    open fun setW(index: Number, z: Number): BufferAttribute /* this */
    open fun setXY(index: Number, x: Number, y: Number): BufferAttribute /* this */
    open fun setXYZ(index: Number, x: Number, y: Number, z: Number): BufferAttribute /* this */
    open fun setXYZW(index: Number, x: Number, y: Number, z: Number, w: Number): BufferAttribute /* this */
    open fun toJSON(): `T$6`
}