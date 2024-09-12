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

external open class LineSegmentsGeometry : InstancedBufferGeometry {
    open val isLineSegmentsGeometry: Boolean
    open fun applyMatrix4(matrix: Matrix4): LineSegmentsGeometry /* this */
    override fun computeBoundingBox()
    override fun computeBoundingSphere()
    open fun fromEdgesGeometry(geometry: EdgesGeometry__0): LineSegmentsGeometry /* this */
    open fun fromLineSegments(lineSegments: LineSegments__0): LineSegmentsGeometry /* this */
    open fun fromMesh(mesh: Mesh__0): LineSegmentsGeometry /* this */
    open fun fromWireframeGeometry(geometry: WireframeGeometry__0): LineSegmentsGeometry /* this */
    open fun setColors(array: Array<Number>): LineSegmentsGeometry /* this */
    open fun setColors(array: Float32Array): LineSegmentsGeometry /* this */
    open fun setPositions(array: Array<Number>): LineSegmentsGeometry /* this */
    open fun setPositions(array: Float32Array): LineSegmentsGeometry /* this */
}