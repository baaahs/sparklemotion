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

external interface Map<K, V> {
    fun entries(): IterableIterator<dynamic /* JsTuple<K, V> */>
    fun keys(): IterableIterator<K>
    fun values(): IterableIterator<V>
    fun clear()
    fun delete(key: K): Boolean
    fun forEach(callbackfn: (value: V, key: K, map: Map<K, V>) -> Unit, thisArg: Any = definedExternally)
    fun get(key: K): V?
    fun has(key: K): Boolean
    fun set(key: K, value: V): Map<K, V> /* this */
    val size: Number
}

external interface MapConstructor {
    val prototype: Map<Any, Any>
}

external interface ReadonlyMap<K, V> {
    fun entries(): IterableIterator<dynamic /* JsTuple<K, V> */>
    fun keys(): IterableIterator<K>
    fun values(): IterableIterator<V>
    fun forEach(callbackfn: (value: V, key: K, map: ReadonlyMap<K, V>) -> Unit, thisArg: Any = definedExternally)
    fun get(key: K): V?
    fun has(key: K): Boolean
    val size: Number
}

external interface WeakMap<K : Any?, V> {
    fun delete(key: K): Boolean
    fun get(key: K): V?
    fun has(key: K): Boolean
    fun set(key: K, value: V): WeakMap<K, V> /* this */
}

external interface WeakMapConstructor {
    val prototype: WeakMap<Any?, Any>
}

external interface Set<T> {
    fun entries(): IterableIterator<dynamic /* JsTuple<T, T> */>
    fun keys(): IterableIterator<T>
    fun values(): IterableIterator<T>
    fun add(value: T): Set<T> /* this */
    fun clear()
    fun delete(value: T): Boolean
    fun forEach(callbackfn: (value: T, value2: T, set: Set<T>) -> Unit, thisArg: Any = definedExternally)
    fun has(value: T): Boolean
    val size: Number
}

external interface SetConstructor {
    val prototype: Set<Any>
}