package three.addons

import org.khronos.webgl.ArrayBuffer
import three.BufferGeometry
import three.Loader__1
import three.LoadingManager
import three.NormalOrGLBufferAttributes

open external class MD2Loader(manager: LoadingManager = definedExternally) : Loader__1<BufferGeometry<NormalOrGLBufferAttributes>> {
    open fun parse(data: ArrayBuffer): BufferGeometry<NormalOrGLBufferAttributes>
}