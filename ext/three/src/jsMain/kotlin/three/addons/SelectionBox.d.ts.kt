@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import js.objects.Record
import three.*

open external class SelectionBox(camera: Camera, scene: Scene, deep: Number = definedExternally) {
    open var camera: Camera
    open var collection: Array<Mesh<*, *>>
    open var deep: Number
    open var endPoint: Vector3
    open var scene: Scene
    open var startPoint: Vector3
    open var instances: Record<String, Array<Number>>
    open fun select(startPoint: Vector3 = definedExternally, endPoint: Vector3 = definedExternally): Array<Mesh<*, *>>
    open fun updateFrustum(startPoint: Vector3, endPoint: Vector3)
    open fun searchChildInFrustum(frustum: Frustum, obj: Object3D)
}