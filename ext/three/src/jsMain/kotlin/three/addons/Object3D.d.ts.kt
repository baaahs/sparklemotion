@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface Object3DJSONObject {
    var uuid: String
    var type: String
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var castShadow: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var receiveShadow: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var visible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var frustumCulled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var renderOrder: Number?
        get() = definedExternally
        set(value) = definedExternally
    var userData: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
    var layers: Number
    var matrix: dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n14, Number, n21, Number, n22, Number, n23, Number, n24, Number, n31, Number, n32, Number, n33, Number, n34, Number, n41, Number, n42, Number, n43, Number, n44, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var up: dynamic /* JsTuple<Number, Number, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var matrixAutoUpdate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var material: dynamic /* String? | Array<String>? */
        get() = definedExternally
        set(value) = definedExternally
    var children: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var animations: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var version: Number
    var type: String
    var generator: String
}

external interface Object3DJSON {
    var metadata: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
    var `object`: Object3DJSONObject
}

external interface JSONMeta {
    var geometries: Record<String, BufferGeometryJSON>
    var materials: Record<String, MaterialJSON>
    var textures: Record<String, TextureJSON>
    var images: Record<String, SourceJSON>
    var shapes: Record<String, ShapeJSON>
    var skeletons: Record<String, SkeletonJSON>
    var animations: Record<String, AnimationClipJSON>
    var nodes: Record<String, Any>
}

external interface `T$1` {
    var child: Object3D__0
}

external interface Object3DEventMap {
    var added: Any
    var removed: Any
    var childadded: `T$1`
    var childremoved: `T$1`
}

external open class Object3D<TEventMap : Object3DEventMap> : EventDispatcher<TEventMap> {
    open val isObject3D: Boolean
    open val id: Number
    open var uuid: String
    open var name: String
    open val type: String /* String | "Object3D" */
    open var parent: Object3D__0?
    open var children: Array<Object3D__0>
    open var up: Vector3
    open val position: Vector3
    open val rotation: Euler
    open val quaternion: Quaternion
    open val scale: Vector3
    open val modelViewMatrix: Matrix4
    open val normalMatrix: Matrix3
    open var matrix: Matrix4
    open var matrixWorld: Matrix4
    open var matrixAutoUpdate: Boolean
    open var matrixWorldAutoUpdate: Boolean
    open var matrixWorldNeedsUpdate: Boolean
    open var layers: Layers
    open var visible: Boolean
    open var castShadow: Boolean
    open var receiveShadow: Boolean
    open var frustumCulled: Boolean
    open var renderOrder: Number
    open var animations: Array<AnimationClip>
    open var userData: Record<String, Any>
    open var customDepthMaterial: Material?
    open var customDistanceMaterial: Material?
    open fun onBeforeShadow(renderer: WebGLRenderer, scene: Scene, camera: Camera, shadowCamera: Camera, geometry: BufferGeometry__0, depthMaterial: Material, group: Group__0)
    open fun onAfterShadow(renderer: WebGLRenderer, scene: Scene, camera: Camera, shadowCamera: Camera, geometry: BufferGeometry__0, depthMaterial: Material, group: Group__0)
    open fun onBeforeRender(renderer: WebGLRenderer, scene: Scene, camera: Camera, geometry: BufferGeometry__0, material: Material, group: Group__0)
    open fun onAfterRender(renderer: WebGLRenderer, scene: Scene, camera: Camera, geometry: BufferGeometry__0, material: Material, group: Group__0)
    open fun applyMatrix4(matrix: Matrix4)
    open fun applyQuaternion(quaternion: Quaternion): Object3D<TEventMap> /* this */
    open fun setRotationFromAxisAngle(axis: Vector3, angle: Number)
    open fun setRotationFromEuler(euler: Euler)
    open fun setRotationFromMatrix(m: Matrix4)
    open fun setRotationFromQuaternion(q: Quaternion)
    open fun rotateOnAxis(axis: Vector3, angle: Number): Object3D<TEventMap> /* this */
    open fun rotateOnWorldAxis(axis: Vector3, angle: Number): Object3D<TEventMap> /* this */
    open fun rotateX(angle: Number): Object3D<TEventMap> /* this */
    open fun rotateY(angle: Number): Object3D<TEventMap> /* this */
    open fun rotateZ(angle: Number): Object3D<TEventMap> /* this */
    open fun translateOnAxis(axis: Vector3, distance: Number): Object3D<TEventMap> /* this */
    open fun translateX(distance: Number): Object3D<TEventMap> /* this */
    open fun translateY(distance: Number): Object3D<TEventMap> /* this */
    open fun translateZ(distance: Number): Object3D<TEventMap> /* this */
    open fun localToWorld(vector: Vector3): Vector3
    open fun worldToLocal(vector: Vector3): Vector3
    open fun lookAt(vector: Vector3)
    open fun lookAt(x: Number, y: Number, z: Number)
    open fun add(vararg obj: Object3D__0): Object3D<TEventMap> /* this */
    open fun remove(vararg obj: Object3D__0): Object3D<TEventMap> /* this */
    open fun removeFromParent(): Object3D<TEventMap> /* this */
    open fun clear(): Object3D<TEventMap> /* this */
    open fun attach(obj: Object3D__0): Object3D<TEventMap> /* this */
    open fun getObjectById(id: Number): Object3D__0?
    open fun getObjectByName(name: String): Object3D__0?
    open fun getObjectByProperty(name: String, value: Any): Object3D__0?
    open fun getObjectsByProperty(name: String, value: Any, optionalTarget: Array<Object3D__0> = definedExternally): Array<Object3D__0>
    open fun getWorldPosition(target: Vector3): Vector3
    open fun getWorldQuaternion(target: Quaternion): Quaternion
    open fun getWorldScale(target: Vector3): Vector3
    open fun getWorldDirection(target: Vector3): Vector3
    open fun raycast(raycaster: Raycaster, intersects: Array<Intersection__0>)
    open fun traverse(callback: (obj: Object3D__0) -> Any)
    open fun traverseVisible(callback: (obj: Object3D__0) -> Any)
    open fun traverseAncestors(callback: (obj: Object3D__0) -> Any)
    open fun updateMatrix()
    open fun updateMatrixWorld(force: Boolean = definedExternally)
    open fun updateWorldMatrix(updateParents: Boolean, updateChildren: Boolean)
    open fun toJSON(meta: JSONMeta = definedExternally): Object3DJSON
    open fun clone(recursive: Boolean = definedExternally): Object3D<TEventMap> /* this */
    open fun copy(obj: Object3D__0, recursive: Boolean = definedExternally): Object3D<TEventMap> /* this */

    companion object {
        var DEFAULT_UP: Vector3
        var DEFAULT_MATRIX_AUTO_UPDATE: Boolean
        var DEFAULT_MATRIX_WORLD_AUTO_UPDATE: Boolean
    }
}

external open class Object3D__0 : Object3D<Object3DEventMap>