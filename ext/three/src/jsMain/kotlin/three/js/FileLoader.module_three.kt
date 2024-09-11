@file:JsModule("three")
@file:JsNonModule
package three.js

import org.w3c.dom.MimeType
import org.w3c.xhr.ProgressEvent

open external class FileLoader(manager: LoadingManager = definedExternally) : Loader__1<dynamic /* String | ArrayBuffer */> {
    override fun load(url: String, onLoad: (data: Any /* String | ArrayBuffer */) -> Unit, onProgress: (event: ProgressEvent) -> Unit, onError: (err: Any) -> Unit)
    open var mimeType: MimeType?
    open var responseType: String?
    open fun setMimeType(mimeType: MimeType): FileLoader
    open fun setResponseType(responseType: String): FileLoader
}