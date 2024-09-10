package three.js

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

open external class FileLoader(manager: LoadingManager = definedExternally) : Loader__1<dynamic /* String | ArrayBuffer */> {
    override fun load(url: String, onLoad: (data: Any /* String | ArrayBuffer */) -> Unit, onProgress: (event: ProgressEvent) -> Unit, onError: (err: Any) -> Unit)
    open var mimeType: MimeType?
    open var responseType: String?
    open fun setMimeType(mimeType: MimeType): FileLoader
    open fun setResponseType(responseType: String): FileLoader
}