@file:JsModule("three")
@file:JsNonModule
package three

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

external var DefaultLoadingManager: LoadingManager

open external class LoadingManager(onLoad: () -> Unit = definedExternally, onProgress: (url: String, loaded: Number, total: Number) -> Unit = definedExternally, onError: (url: String) -> Unit = definedExternally) {
    open var onStart: ((url: String, loaded: Number, total: Number) -> Unit)?
    open var onLoad: () -> Unit
    open var onProgress: (url: String, loaded: Number, total: Number) -> Unit
    open var onError: (url: String) -> Unit
    open fun setURLModifier(callback: (url: String) -> String = definedExternally): LoadingManager /* this */
    open fun resolveURL(url: String): String
    open fun itemStart(url: String)
    open fun itemEnd(url: String)
    open fun itemError(url: String)
    open fun addHandler(regex: RegExp, loader: Loader__0): LoadingManager /* this */
    open fun removeHandler(regex: RegExp): LoadingManager /* this */
    open fun getHandler(file: String): Loader__0?
}