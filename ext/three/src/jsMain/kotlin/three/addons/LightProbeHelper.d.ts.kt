@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class LightProbeHelper(lightProbe: LightProbe, size: Number) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var lightProbe: LightProbe
    open var size: Number
    open fun dispose()
}