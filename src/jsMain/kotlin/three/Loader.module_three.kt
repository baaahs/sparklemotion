@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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