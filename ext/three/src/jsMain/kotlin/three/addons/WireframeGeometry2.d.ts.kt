package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class WireframeGeometry2(geometry: BufferGeometry<NormalOrGLBufferAttributes>) : LineSegmentsGeometry {
    open val sWireframeGeometry2: Boolean
}