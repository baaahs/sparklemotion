package three.addons

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
import three.*
import kotlin.js.*

external interface KTX2LoaderWorkerConfig {
    var astcSupported: Boolean
    var etc1Supported: Boolean
    var etc2Supported: Boolean
    var dxtSupported: Boolean
    var bptcSupported: Boolean
    var pvrtcSupported: Boolean
}

open external class KTX2Loader(manager: LoadingManager = definedExternally) : Loader__1<CompressedTexture> {
    open var transcoderPath: String
    open var transcoderBinary: ArrayBuffer?
    open var transcoderPending: Promise<Unit>?
    open var workerPool: WorkerPool
    open var workerSourceURL: String
    open var workerConfig: KTX2LoaderWorkerConfig
    open fun setTranscoderPath(path: String): KTX2Loader /* this */
    open fun setWorkerLimit(limit: Number): KTX2Loader /* this */
    open fun detectSupportAsync(renderer: Renderer): Promise<KTX2Loader /* this */>
    open fun detectSupport(renderer: WebGLRenderer): KTX2Loader /* this */
//    open fun detectSupport(renderer: WebGPURenderer): KTX2Loader /* this */
    open fun init(): Promise<Unit>
    open fun parse(buffer: ArrayBuffer, onLoad: (texture: CompressedTexture) -> Unit = definedExternally, onError: (err: Any) -> Unit = definedExternally)
    open fun dispose(): KTX2Loader /* this */
}