@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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

external interface `T$89` {
    @nativeGetter
    operator fun get(header: String): String?
    @nativeSetter
    operator fun set(header: String, value: String)
}

external open class Loader<TData, TUrl>(manager: LoadingManager = definedExternally) {
    open var crossOrigin: String
    open var withCredentials: Boolean
    open var path: String
    open var resourcePath: String
    open var manager: LoadingManager
    open var requestHeader: `T$89`
    open fun load(url: TUrl, onLoad: (data: TData) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (err: Any) -> Unit = definedExternally)
    open fun loadAsync(url: TUrl, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally): Promise<TData>
    open fun setCrossOrigin(crossOrigin: String): Loader<TData, TUrl> /* this */
    open fun setWithCredentials(value: Boolean): Loader<TData, TUrl> /* this */
    open fun setPath(path: String): Loader<TData, TUrl> /* this */
    open fun setResourcePath(resourcePath: String): Loader<TData, TUrl> /* this */
    open fun setRequestHeader(requestHeader: `T$89`): Loader<TData, TUrl> /* this */

    companion object {
        var DEFAULT_MATERIAL_NAME: String
    }
}

external open class Loader__1<TData> : Loader<TData, String>

external open class Loader__0 : Loader<Any, String>