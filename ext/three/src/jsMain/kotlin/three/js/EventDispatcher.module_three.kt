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

//external interface BaseEvent<TEventType : String> {
//    val type: TEventType
//}
//
//external interface Event<TEventType : String, TTarget> {
//    val type: TEventType
//    val target: TTarget
//}
//
//typealias EventListener/*<TEventData, TEventType, TTarget>*/ = // (event: TEventData /* TEventData & Event<TEventType, TTarget> */) -> Unit
//            (event: Event /* TEventData & Event<TEventType, TTarget> */) -> Unit

open external class EventDispatcher<TEventMap : Any> {
//    open fun <T : Extract<Any, String>> addEventListener(type: T, listener: EventListener<Any, T, EventDispatcher<TEventMap> /* this */>)
//    open fun <T : Extract<Any, String>> hasEventListener(type: T, listener: EventListener<Any, T, EventDispatcher<TEventMap> /* this */>): Boolean
//    open fun <T : Extract<Any, String>> removeEventListener(type: T, listener: EventListener<Any, T, EventDispatcher<TEventMap> /* this */>)
//    open fun <T : Extract<Any, String>> dispatchEvent(event: BaseEvent<T> /* BaseEvent<T> & dynamic */)
}