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

external interface Event {
    var type: String
    var target: Any?
        get() = definedExternally
        set(value) = definedExternally
    @nativeGetter
    operator fun get(attachment: String): Any?
    @nativeSetter
    operator fun set(attachment: String, value: Any)
}

external interface `T$7` {
    var type: String
    @nativeGetter
    operator fun get(attachment: String): Any?
    @nativeSetter
    operator fun set(attachment: String, value: Any)
}

open external class EventDispatcher {
    open fun addEventListener(type: String, listener: (event: Event) -> Unit)
    open fun hasEventListener(type: String, listener: (event: Event) -> Unit): Boolean
    open fun removeEventListener(type: String, listener: (event: Event) -> Unit)
    open fun dispatchEvent(event: `T$7`)
}