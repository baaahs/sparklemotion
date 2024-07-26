@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ErrorEvent
import org.w3c.dom.MimeType
import org.w3c.xhr.ProgressEvent

open external class FileLoader(manager: LoadingManager = definedExternally) : Loader {
    open var mimeType: MimeType?
    open var responseType: String?
    open var withCredentials: String?
    open fun load(url: String, onLoad: (response: dynamic /* String | ArrayBuffer */) -> Unit = definedExternally, onProgress: (request: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): Any
    open fun setMimeType(mimeType: MimeType): FileLoader
    open fun setResponseType(responseType: String): FileLoader
    open fun setWithCredentials(value: Boolean): FileLoader
}