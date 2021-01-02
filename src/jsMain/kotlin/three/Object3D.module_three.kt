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

external interface `T$0` {
    var geometries: Any
    var materials: Any
    var textures: Any
    var images: Any
}

open external class Object3D : EventDispatcher {
    open var id: Number
    open var uuid: String
    open var name: String
    open var type: String
    open var parent: Object3D?
    open var children: Array<Object3D>
    open var up: Vector3
    open var position: Vector3
    open var rotation: Euler
    open var quaternion: Quaternion
    open var scale: Vector3
    open var modelViewMatrix: Matrix4
    open var normalMatrix: Matrix3
    open var matrix: Matrix4
    open var matrixWorld: Matrix4
    open var matrixAutoUpdate: Boolean
    open var matrixWorldNeedsUpdate: Boolean
    open var layers: Layers
    open var visible: Boolean
    open var castShadow: Boolean
    open var receiveShadow: Boolean
    open var frustumCulled: Boolean
    open var renderOrder: Number
    open var userData: Json
    open var customDepthMaterial: Material
    open var customDistanceMaterial: Material
    open var isObject3D: Boolean
    open var onBeforeRender: (renderer: WebGLRenderer, scene: Scene, camera: Camera, geometry: dynamic /* Geometry | BufferGeometry */, material: Material, group: Group) -> Unit
    open var onAfterRender: (renderer: WebGLRenderer, scene: Scene, camera: Camera, geometry: dynamic /* Geometry | BufferGeometry */, material: Material, group: Group) -> Unit
    open fun applyMatrix4(matrix: Matrix4)
    open fun applyQuaternion(quaternion: Quaternion): Object3D /* this */
    open fun setRotationFromAxisAngle(axis: Vector3, angle: Number)
    open fun setRotationFromEuler(euler: Euler)
    open fun setRotationFromMatrix(m: Matrix4)
    open fun setRotationFromQuaternion(q: Quaternion)
    open fun rotateOnAxis(axis: Vector3, angle: Number): Object3D /* this */
    open fun rotateOnWorldAxis(axis: Vector3, angle: Number): Object3D /* this */
    open fun rotateX(angle: Number): Object3D /* this */
    open fun rotateY(angle: Number): Object3D /* this */
    open fun rotateZ(angle: Number): Object3D /* this */
    open fun translateOnAxis(axis: Vector3, distance: Number): Object3D /* this */
    open fun translateX(distance: Number): Object3D /* this */
    open fun translateY(distance: Number): Object3D /* this */
    open fun translateZ(distance: Number): Object3D /* this */
    open fun localToWorld(vector: Vector3): Vector3
    open fun worldToLocal(vector: Vector3): Vector3
    open fun lookAt(vector: Vector3, y: Number = definedExternally, z: Number = definedExternally)
    open fun lookAt(vector: Number, y: Number = definedExternally, z: Number = definedExternally)
    open fun add(vararg obj: Object3D): Object3D /* this */
    open fun remove(vararg obj: Object3D): Object3D /* this */
    open fun attach(obj: Object3D): Object3D /* this */
    open fun getObjectById(id: Number): Object3D?
    open fun getObjectByName(name: String): Object3D?
    open fun getObjectByProperty(name: String, value: String): Object3D?
    open fun getWorldPosition(target: Vector3): Vector3
    open fun getWorldQuaternion(target: Quaternion): Quaternion
    open fun getWorldScale(target: Vector3): Vector3
    open fun getWorldDirection(target: Vector3): Vector3
    open fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun traverse(callback: (obj: Object3D) -> Any)
    open fun traverseVisible(callback: (obj: Object3D) -> Any)
    open fun traverseAncestors(callback: (obj: Object3D) -> Any)
    open fun updateMatrix()
    open fun updateMatrixWorld(force: Boolean = definedExternally)
    open fun updateWorldMatrix(updateParents: Boolean, updateChildren: Boolean)
    open fun toJSON(meta: `T$0` = definedExternally): Any
    open fun clone(recursive: Boolean = definedExternally): Object3D /* this */
    open fun copy(source: Object3D /* this */, recursive: Boolean = definedExternally): Object3D /* this */

    companion object {
        var DefaultUp: Vector3
        var DefaultMatrixAutoUpdate: Boolean
    }
}