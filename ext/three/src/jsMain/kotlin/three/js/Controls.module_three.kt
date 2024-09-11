@file:JsModule("three")
@file:JsNonModule
package three.js

import org.w3c.dom.HTMLElement

open external class Controls/*<TEventMap : Any>*/(obj: Camera, domElement: HTMLElement?) : EventDispatcher/*<TEventMap>*/ {
    open var `object`: Camera
    open var domElement: HTMLElement?
    open var enabled: Boolean
    open fun connect()
    open fun disconnect()
    open fun dispose()
    open fun update(delta: Number)
}