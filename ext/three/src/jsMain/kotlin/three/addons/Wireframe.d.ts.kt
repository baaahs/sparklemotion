package three.addons

import three.BufferGeometry
import three.Material
import three.Mesh
import three.NormalOrGLBufferAttributes

open external class Wireframe(geometry: LineSegmentsGeometry = definedExternally, material: LineMaterial = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open val isWireframe: Boolean
    open fun computeLineDistances(): Wireframe /* this */
}