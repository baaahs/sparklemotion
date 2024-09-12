@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external open class FlakesTexture(width: Number = definedExternally, height: Number = definedExternally) : HTMLCanvasElement {
    override fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: Boolean)
    override fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any)
    override fun <K : Nothing?> addEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListener, options: Boolean)
    override fun addEventListener(type: String, listener: EventListener)
    override fun addEventListener(type: String, listener: EventListener, options: AddEventListenerOptions)
    override fun addEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun addEventListener(type: String, listener: EventListenerObject)
    override fun addEventListener(type: String, listener: EventListenerObject, options: AddEventListenerOptions)
    override fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: Boolean)
    override fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any)
    override fun <K : Nothing?> removeEventListener(type: K, listener: (self: HTMLCanvasElement, ev: Any) -> Any, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListener, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListener)
    override fun removeEventListener(type: String, listener: EventListener, options: EventListenerOptions)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: Boolean)
    override fun removeEventListener(type: String, listener: EventListenerObject)
    override fun removeEventListener(type: String, listener: EventListenerObject, options: EventListenerOptions)
}