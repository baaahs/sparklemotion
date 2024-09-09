@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import kotlin.js.RegExp

open external class LoadingManager(onLoad: () -> Unit = definedExternally, onProgress: (url: String, loaded: Number, total: Number) -> Unit = definedExternally, onError: (url: String) -> Unit = definedExternally) {
    open var onStart: (url: String, loaded: Number, total: Number) -> Unit
    open var onLoad: () -> Unit
    open var onProgress: (url: String, loaded: Number, total: Number) -> Unit
    open var onError: (url: String) -> Unit
    open fun setURLModifier(callback: (url: String) -> String = definedExternally): LoadingManager /* this */
    open fun resolveURL(url: String): String
    open fun itemStart(url: String)
    open fun itemEnd(url: String)
    open fun itemError(url: String)
    open fun addHandler(regex: RegExp, loader: Loader): LoadingManager /* this */
    open fun removeHandler(regex: RegExp): LoadingManager /* this */
    open fun getHandler(file: String): Loader?
}