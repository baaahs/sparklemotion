@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Material
import three.Mesh
import three.NormalOrGLBufferAttributes

open external class Wireframe(geometry: LineSegmentsGeometry = definedExternally, material: LineMaterial = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open val isWireframe: Boolean
    open fun computeLineDistances(): Wireframe /* this */
}