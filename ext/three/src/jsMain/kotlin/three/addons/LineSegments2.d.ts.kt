@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Material
import three.Mesh
import three.NormalOrGLBufferAttributes

open external class LineSegments2(geometry: LineSegmentsGeometry = definedExternally, material: LineMaterial = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
//    override var geometry: LineSegmentsGeometry
//    override var material: LineMaterial
    open val isLineSegments2: Boolean
    open fun computeLineDistances(): LineSegments2 /* this */
}