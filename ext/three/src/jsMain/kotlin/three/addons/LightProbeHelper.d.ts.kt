package three.addons

import three.*

open external class LightProbeHelper(lightProbe: LightProbe, size: Number) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var lightProbe: LightProbe
    open var size: Number
    open fun dispose()
}