@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

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

open external class EventDispatcher {
    open fun addEventListener(type: String, listener: (event: Event) -> Unit)
    open fun hasEventListener(type: String, listener: (event: Event) -> Unit): Boolean
    open fun removeEventListener(type: String, listener: (event: Event) -> Unit)
    open fun dispatchEvent(event: Event)
}