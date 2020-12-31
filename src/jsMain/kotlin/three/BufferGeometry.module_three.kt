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

external interface `T$16` {
    @nativeGetter
    operator fun get(name: String): dynamic /* BufferAttribute? | InterleavedBufferAttribute? */
    @nativeSetter
    operator fun set(name: String, value: BufferAttribute)
    @nativeSetter
    operator fun set(name: String, value: InterleavedBufferAttribute)
}

external interface `T$17` {
    @nativeGetter
    operator fun get(name: String): Array<dynamic /* BufferAttribute | InterleavedBufferAttribute */>?
    @nativeSetter
    operator fun set(name: String, value: Array<dynamic /* BufferAttribute | InterleavedBufferAttribute */>)
}

external interface `T$18` {
    var start: Number
    var count: Number
    var materialIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$19` {
    var start: Number
    var count: Number
}

open external class BufferGeometry : EventDispatcher {
    open var id: Number
    open var uuid: String
    open var name: String
    open var type: String
    open var index: BufferAttribute?
    open var attributes: `T$16`
    open var morphAttributes: `T$17`
    open var morphTargetsRelative: Boolean
    open var groups: Array<`T$18`>
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var drawRange: `T$19`
    open var userData: Json
    open var isBufferGeometry: Boolean
    open fun getIndex(): BufferAttribute?
    open fun setIndex(index: BufferAttribute?)
    open fun setIndex(index: Array<Number>?)
    open fun setAttribute(name: String, attribute: BufferAttribute): BufferGeometry
    open fun setAttribute(name: String, attribute: InterleavedBufferAttribute): BufferGeometry
    open fun getAttribute(name: String): dynamic /* BufferAttribute | InterleavedBufferAttribute */
    open fun deleteAttribute(name: String): BufferGeometry
    open fun addGroup(start: Number, count: Number, materialIndex: Number = definedExternally)
    open fun clearGroups()
    open fun setDrawRange(start: Number, count: Number)
    open fun applyMatrix4(matrix: Matrix4): BufferGeometry
    open fun rotateX(angle: Number): BufferGeometry
    open fun rotateY(angle: Number): BufferGeometry
    open fun rotateZ(angle: Number): BufferGeometry
    open fun translate(x: Number, y: Number, z: Number): BufferGeometry
    open fun scale(x: Number, y: Number, z: Number): BufferGeometry
    open fun lookAt(v: Vector3)
    open fun center(): BufferGeometry
    open fun setFromObject(obj: Object3D): BufferGeometry
    open fun setFromPoints(points: Array<Vector3>): BufferGeometry
    open fun setFromPoints(points: Array<Vector2>): BufferGeometry
    open fun updateFromObject(obj: Object3D)
    open fun fromGeometry(geometry: Geometry, settings: Any = definedExternally): BufferGeometry
    open fun fromDirectGeometry(geometry: DirectGeometry): BufferGeometry
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun computeVertexNormals()
    open fun merge(geometry: BufferGeometry, offset: Number = definedExternally): BufferGeometry
    open fun normalizeNormals()
    open fun toNonIndexed(): BufferGeometry
    open fun toJSON(): Any
    open fun clone(): BufferGeometry /* this */
    open fun copy(source: BufferGeometry): BufferGeometry /* this */
    open fun dispose()
    open var drawcalls: Any
    open var offsets: Any
    open fun addIndex(index: Any)
    open fun addDrawCall(start: Any, count: Any, indexOffset: Any = definedExternally)
    open fun clearDrawCalls()
    open fun addAttribute(name: String, attribute: BufferAttribute): BufferGeometry
    open fun addAttribute(name: String, attribute: InterleavedBufferAttribute): BufferGeometry
    open fun removeAttribute(name: String): BufferGeometry
    open fun addAttribute(name: Any, array: Any, itemSize: Any): Any

    companion object {
        var MaxIndex: Number
    }
}