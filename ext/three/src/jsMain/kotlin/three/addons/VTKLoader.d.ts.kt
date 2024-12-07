package three.addons

import org.khronos.webgl.ArrayBuffer
import three.BufferGeometry
import three.Loader__1
import three.LoadingManager
import three.NormalOrGLBufferAttributes

open external class VTKLoader(manager: LoadingManager = definedExternally) : Loader__1<BufferGeometry<NormalOrGLBufferAttributes>> {
    open fun parse(data: ArrayBuffer, path: String): BufferGeometry<NormalOrGLBufferAttributes>
    open fun parse(data: String, path: String): BufferGeometry<NormalOrGLBufferAttributes>
}