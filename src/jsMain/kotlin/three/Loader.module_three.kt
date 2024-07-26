@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.xhr.ProgressEvent
import kotlin.js.Promise

external interface `T$44` {
    @nativeGetter
    operator fun get(header: String): String?
    @nativeSetter
    operator fun set(header: String, value: String)
}

open external class Loader(manager: LoadingManager = definedExternally) {
    open var crossOrigin: String
    open var path: String
    open var resourcePath: String
    open var manager: LoadingManager
    open var requestHeader: `T$44`
    open fun loadAsync(url: String, onProgress: (event: ProgressEvent) -> Unit = definedExternally): Promise<Any>
    open fun setCrossOrigin(crossOrigin: String): Loader /* this */
    open fun setPath(path: String): Loader /* this */
    open fun setResourcePath(resourcePath: String): Loader /* this */
    open fun setRequestHeader(requestHeader: `T$44`): Loader /* this */
}