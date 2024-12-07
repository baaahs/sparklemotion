@file:JsModule("three")
@file:JsNonModule
package three

import js.objects.Record
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

external interface `T$9` {
    var type: String
    var array: Array<Number>
}

external interface `T$10` {
    var center: dynamic /* JsTuple<Number, Number, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number
}

external interface `T$11` {
    var attributes: Record<String, BufferAttributeJSON>
    var index: `T$9`?
        get() = definedExternally
        set(value) = definedExternally
    var morphAttributes: Record<String, Array<BufferAttributeJSON>>?
        get() = definedExternally
        set(value) = definedExternally
    var morphTargetsRelative: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var groups: Array<GeometryGroup>?
        get() = definedExternally
        set(value) = definedExternally
    var boundingSphere: `T$10`?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BufferGeometryJSON {
    var metadata: `T$0_Object3D`?
        get() = definedExternally
        set(value) = definedExternally
    var uuid: String
    var type: String
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var userData: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
    var data: `T$11`?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GeometryGroup {
    var start: Number
    var count: Number
    var materialIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$12`

open external class BufferGeometry<Attributes : NormalOrGLBufferAttributes> : EventDispatcher/*<`T$8`>*/ {
    open var id: Number
    open var uuid: String
    open var name: String
    open val type: String /* String | "BufferGeometry" */
    open var index: BufferAttribute?
    open var attributes: Attributes
    open var morphAttributes: `T$12`
    open var morphTargetsRelative: Boolean
    open var groups: Array<GeometryGroup>
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var drawRange: `T$3`
    open var userData: Record<String, Any>
    open val isBufferGeometry: Boolean
    open fun getIndex(): BufferAttribute?
    open fun setIndex(index: BufferAttribute?): BufferGeometry<Attributes> /* this */
    open fun setIndex(index: Array<Number>?): BufferGeometry<Attributes> /* this */
    open fun <K : Any> setAttribute(name: K, attribute: Any): BufferGeometry<Attributes> /* this */
    open fun <K : Any> getAttribute(name: K): Any
    open fun deleteAttribute(name: Any): BufferGeometry<Attributes> /* this */
    open fun hasAttribute(name: Any): Boolean
    open fun addGroup(start: Number, count: Number, materialIndex: Number = definedExternally)
    open fun clearGroups()
    open fun setDrawRange(start: Number, count: Number)
    open fun applyMatrix4(matrix: Matrix4): BufferGeometry<Attributes> /* this */
    open fun applyQuaternion(quaternion: Quaternion): BufferGeometry<Attributes> /* this */
    open fun rotateX(angle: Number): BufferGeometry<Attributes> /* this */
    open fun rotateY(angle: Number): BufferGeometry<Attributes> /* this */
    open fun rotateZ(angle: Number): BufferGeometry<Attributes> /* this */
    open fun translate(x: Number, y: Number, z: Number): BufferGeometry<Attributes> /* this */
    open fun scale(x: Number, y: Number, z: Number): BufferGeometry<Attributes> /* this */
    open fun lookAt(vector: Vector3): BufferGeometry<Attributes> /* this */
    open fun center(): BufferGeometry<Attributes> /* this */
    open fun setFromPoints(points: Array<Vector3>): BufferGeometry<Attributes> /* this */
    open fun setFromPoints(points: Array<Vector2>): BufferGeometry<Attributes> /* this */
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun computeTangents()
    open fun computeVertexNormals()
    open fun normalizeNormals()
    open fun toNonIndexed(): BufferGeometry<NormalBufferAttributes>
    open fun toJSON(): BufferGeometryJSON
    open fun clone(): BufferGeometry<Attributes> /* this */
    open fun copy(source: BufferGeometry<NormalBufferAttributes>): BufferGeometry<Attributes> /* this */
    open fun dispose()
}

//typealias BufferGeometry__0 = BufferGeometry<NormalBufferAttributes>