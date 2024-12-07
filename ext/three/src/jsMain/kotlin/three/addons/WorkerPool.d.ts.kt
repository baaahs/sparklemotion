package three.addons

import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import kotlin.js.Promise

external interface `T$81` {
    var resolve: (e: Any) -> Unit
    var msg: Any
    var transfer: Array<dynamic /* ArrayBuffer | MessagePort | ImageBitmap | OffscreenCanvas */>
}

open external class WorkerPool(pool: Number = definedExternally) {
    open var pool: Number
    open var quene: Array<`T$81`>
    open var workers: Array<Worker>
    open var workersResolve: Array<(e: Any) -> Unit>
    open var workerStatus: Number
    open fun _initWorker(workerId: Number)
    open fun workerCreator(): Worker
    open fun _getIdleWorker()
    open fun _onMessage(workerId: Number, msg: Any)
    open fun setWorkerCreator(workerCreator: () -> Worker)
    open fun setWorkerLimit(pool: Number)
    open fun postMessage(msg: Any, transfer: Array<Any /* ArrayBuffer | MessagePort | ImageBitmap | OffscreenCanvas */> = definedExternally): Promise<MessageEvent>
    open fun dispose()
}