package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class TessellateModifier(maxEdgeLength: Number = definedExternally, maxIterations: Number = definedExternally) {
    open var maxEdgeLength: Number
    open var maxIterations: Number
    open fun <TGeometry : BufferGeometry<NormalOrGLBufferAttributes>> modify(geometry: TGeometry): TGeometry
}