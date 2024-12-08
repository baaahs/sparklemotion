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