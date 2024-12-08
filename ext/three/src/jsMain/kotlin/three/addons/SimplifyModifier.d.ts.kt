package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class SimplifyModifier {
    open fun modify(geometry: BufferGeometry<NormalOrGLBufferAttributes>, count: Number): BufferGeometry<NormalOrGLBufferAttributes>
}