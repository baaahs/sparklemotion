package three.addons

import three.*

open external class DecalGeometry(mesh: Mesh<*, *>, position: Vector3, orientation: Euler, size: Vector3) : BufferGeometry<NormalOrGLBufferAttributes>

open external class DecalVertex(position: Vector3, normal: Vector3) {
    open fun clone(): DecalVertex /* this */
}