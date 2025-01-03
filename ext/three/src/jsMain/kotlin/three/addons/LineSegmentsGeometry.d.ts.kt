package three.addons

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
import three.*
import kotlin.js.*

open external class LineSegmentsGeometry : InstancedBufferGeometry {
    open val isLineSegmentsGeometry: Boolean
    override fun applyMatrix4(matrix: Matrix4): LineSegmentsGeometry /* this */
    override fun computeBoundingBox()
    override fun computeBoundingSphere()
    open fun fromEdgesGeometry(geometry: EdgesGeometry<BufferGeometry<NormalOrGLBufferAttributes>>): LineSegmentsGeometry /* this */
    open fun fromLineSegments(lineSegments: LineSegments<BufferGeometry<NormalOrGLBufferAttributes>, Material>): LineSegmentsGeometry /* this */
    open fun fromMesh(mesh: Mesh<*, *>): LineSegmentsGeometry /* this */
    open fun fromWireframeGeometry(geometry: WireframeGeometry<BufferGeometry<NormalOrGLBufferAttributes>>): LineSegmentsGeometry /* this */
    open fun setColors(array: Array<Number>): LineSegmentsGeometry /* this */
    open fun setColors(array: Float32Array): LineSegmentsGeometry /* this */
    open fun setPositions(array: Array<Number>): LineSegmentsGeometry /* this */
    open fun setPositions(array: Float32Array): LineSegmentsGeometry /* this */
}