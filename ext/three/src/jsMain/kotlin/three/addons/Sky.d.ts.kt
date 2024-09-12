@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class Sky : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
//    override var geometry: BoxGeometry
//    override var material: ShaderMaterial

    companion object {
        var SkyShader: Any?
    }
}