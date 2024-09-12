@file:JsModule("three")
@file:JsNonModule
package three

import org.w3c.dom.HTMLImageElement

open external class ImageLoader<TData>(manager: LoadingManager = definedExternally) : Loader<TData, HTMLImageElement> {
//    open fun load(url: String, onLoad: (data: HTMLImageElement) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (err: Any) -> Unit = definedExternally): HTMLImageElement
}