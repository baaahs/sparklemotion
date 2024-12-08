package three.addons

import three.BufferGeometry
import three.Loader__1
import three.LoadingManager
import three.NormalOrGLBufferAttributes

open external class XYZLoader(manager: LoadingManager = definedExternally) : Loader__1<BufferGeometry<NormalOrGLBufferAttributes>> {
    open fun parse(data: String, onLoad: (geometry: BufferGeometry<NormalOrGLBufferAttributes>) -> Unit): Any?
}