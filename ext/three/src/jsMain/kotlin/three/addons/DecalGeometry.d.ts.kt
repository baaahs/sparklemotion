@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class DecalGeometry(mesh: Mesh<*, *>, position: Vector3, orientation: Euler, size: Vector3) : BufferGeometry<NormalOrGLBufferAttributes>

open external class DecalVertex(position: Vector3, normal: Vector3) {
    open fun clone(): DecalVertex /* this */
}