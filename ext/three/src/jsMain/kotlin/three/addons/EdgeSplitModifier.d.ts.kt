@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class EdgeSplitModifier {
    open fun modify(geometry: BufferGeometry<NormalOrGLBufferAttributes>, cutOffPoint: Number, tryKeepNormals: Boolean): BufferGeometry<NormalOrGLBufferAttributes>
}