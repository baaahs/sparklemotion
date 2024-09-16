@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.xhr.ProgressEvent
import three.BufferGeometry
import three.Loader__1
import three.LoadingManager
import three.NormalOrGLBufferAttributes

open external class DRACOLoader(manager: LoadingManager = definedExternally) : Loader__1<BufferGeometry<NormalOrGLBufferAttributes>> {
    open fun setDecoderPath(path: String): DRACOLoader
    open fun setDecoderConfig(config: Any?): DRACOLoader
    open fun setWorkerLimit(workerLimit: Number): DRACOLoader
    override fun load(url: String, onLoad: (data: BufferGeometry<NormalOrGLBufferAttributes>) -> Unit, onProgress: (event: ProgressEvent) -> Unit, onError: (err: Any) -> Unit)
    open fun preload(): DRACOLoader
    open fun dispose(): DRACOLoader
}