@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class TessellateModifier(maxEdgeLength: Number = definedExternally, maxIterations: Number = definedExternally) {
    open var maxEdgeLength: Number
    open var maxIterations: Number
    open fun <TGeometry : BufferGeometry<NormalOrGLBufferAttributes>> modify(geometry: TGeometry): TGeometry
}