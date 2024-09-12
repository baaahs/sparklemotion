@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class WireframeGeometry2(geometry: BufferGeometry<NormalOrGLBufferAttributes>) : LineSegmentsGeometry {
    open val sWireframeGeometry2: Boolean
}