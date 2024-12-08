@file:JsModule("three")
@file:JsNonModule
package three

import org.w3c.xhr.ProgressEvent
import kotlin.js.Promise

external interface `T$89`

open external class Loader<TData, TUrl>(manager: LoadingManager = definedExternally) {
    open var crossOrigin: String
    open var withCredentials: Boolean
    open var path: String
    open var resourcePath: String
    open var manager: LoadingManager
    open var requestHeader: `T$89`
    open fun load(url: TUrl, onLoad: (data: TData) -> Unit, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (err: Any) -> Unit = definedExternally): Any?
    open fun loadAsync(url: TUrl, onProgress: (event: ProgressEvent) -> Unit = definedExternally): Promise<TData>
    open fun setCrossOrigin(crossOrigin: String): Loader<TData, TUrl> /* this */
    open fun setWithCredentials(value: Boolean): Loader<TData, TUrl> /* this */
    open fun setPath(path: String): Loader<TData, TUrl> /* this */
    open fun setResourcePath(resourcePath: String): Loader<TData, TUrl> /* this */
    open fun setRequestHeader(requestHeader: `T$89`): Loader<TData, TUrl> /* this */

    companion object {
        var DEFAULT_MATERIAL_NAME: String
    }
}