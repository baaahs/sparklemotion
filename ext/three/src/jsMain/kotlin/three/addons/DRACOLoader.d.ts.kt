@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external open class DRACOLoader(manager: LoadingManager = definedExternally) : Loader__1<BufferGeometry__0> {
    open fun setDecoderPath(path: String): DRACOLoader
    open fun setDecoderConfig(config: Any?): DRACOLoader
    open fun setWorkerLimit(workerLimit: Number): DRACOLoader
    override fun load(url: String, onLoad: (data: BufferGeometry__0) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit, onError: (err: Any) -> Unit)
    open fun preload(): DRACOLoader
    open fun dispose(): DRACOLoader
}