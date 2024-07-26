@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
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