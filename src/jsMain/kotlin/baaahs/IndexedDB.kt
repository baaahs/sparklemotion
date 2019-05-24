package baaahs

import kotlinx.serialization.Serializable
import org.w3c.dom.DOMStringMap
import org.w3c.dom.ItemArrayLike
import org.w3c.dom.Window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

fun Window.indexedDB(): IDBFactory = js("window.indexedDB") as IDBFactory

abstract external class IDBFactory {
    fun open(name: String): IDBOpenDBRequest
    fun open(name: String, version: Long): IDBOpenDBRequest
    fun delete(name: String)
}

abstract external class IDBOpenDBRequest : IDBRequest {
    var onblocked: (event: Event) -> Unit
    var onupgradeneeded: (event: IDBVersionChangeEvent) -> Unit
}

abstract external class IDBRequest : EventTarget {
    var onsuccess: (event: Event) -> Unit
    var onerror: (event: Event) -> Unit
    val result: IDBDatabase
    val error: dynamic
    val source: dynamic
}

abstract external class IDBDatabase {
    val name: String
    val version: Long
    val objectStoreNames: ItemArrayLike<String>

    fun close()
    fun createObjectStore(name: String, options: dynamic = definedExternally): IDBObjectStore
    fun deleteObjectStore(name: String)
    fun transaction(storeName: String, mode: dynamic = definedExternally): IDBTransaction
    fun transaction(storeNames: Array<String>, mode: dynamic = definedExternally): IDBTransaction
}

abstract external class IDBObjectStore {
    val name: String
    val keyPath: Any;
    val indexNames: ItemArrayLike<String>
    val transaction: IDBTransaction
    val autoIncrement: Boolean;

    fun put(value: Any, key: Any? = definedExternally): IDBRequest
    fun add(value: Any, key: Any? = definedExternally): IDBRequest
    fun delete(query: Any): IDBRequest
    fun clear()
    fun get(key: Any): IDBRequest
    fun getKey(): IDBRequest
    fun getAll(query: Any? = definedExternally, count: Int? = definedExternally): IDBRequest
    fun getAllKeys(): IDBRequest
    fun count(): IDBRequest

    fun openCursor(): IDBRequest
    fun openKeyCursor(): IDBRequest

    fun index(): IDBRequest

    fun createIndex(): IDBRequest
    fun deleteIndex(): IDBRequest
}

abstract external class IDBTransaction : EventTarget {
    val mode: String
    val onabort: (event: Event) -> Unit
    val oncomplete: (event: Event) -> Unit
    val onerror: (event: Event) -> Unit

    fun objectStore(name: String): IDBObjectStore
}

abstract external class IDBVersionChangeEvent : Event {
    val oldVersion: Long
    val newVersion: Long
}