package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class EdgeSplitModifier {
    open fun modify(geometry: BufferGeometry<NormalOrGLBufferAttributes>, cutOffPoint: Number, tryKeepNormals: Boolean): BufferGeometry<NormalOrGLBufferAttributes>
}