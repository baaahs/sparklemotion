package three.addons

import three.*

open external class Sky : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
//    override var geometry: BoxGeometry
//    override var material: ShaderMaterial

    companion object {
        var SkyShader: Any?
    }
}