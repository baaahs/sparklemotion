package baaahs.db

import baaahs.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.list
import org.w3c.dom.events.Event
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class IndexedDbFacade(private val factory: IDBFactory) : Persistence.Impl {
    val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        val json = Json(JsonConfiguration.Stable)
    }

    override suspend fun open(
        name: String,
        version: Long,
        migrateFn: suspend (database: Persistence.Database, fromVersion: Long, toVersion: Long) -> Unit
    ): Persistence.Database =
        suspendCoroutine { cont ->
            val dbRequest = factory.open(name, version)
            fun target(event: Event) = event.target as IDBOpenDBRequest
            console.log("open db $name...")
            dbRequest.onupgradeneeded = { event ->
                console.log("db $name needs upgrade", event.oldVersion, event.newVersion)
                val databaseFacade = DatabaseFacade(target(event).result)
                doRunBlocking {
                    migrateFn(databaseFacade, event.oldVersion, event.newVersion)
                }
                // No cont.resume() here because onsuccess will still be called.
            }
            dbRequest.onsuccess = { event ->
                console.log("db $name opened")
                cont.resume(DatabaseFacade(target(event).result))
            }
            dbRequest.onerror = { event ->
                console.log("db $name error")
                cont.resumeWithException(target(event).error)
            }
        }

    override fun delete(name: String) {
        factory.delete(name)
    }

    class DatabaseFacade(private val db: IDBDatabase) : Persistence.Database {
        override val name: String = db.name
        override val version: Long = db.version

        override suspend fun <T> createStore(name: String, serializer: KSerializer<T>): Persistence.Store<T> =
            StoreFacade(db.createObjectStore(name, js("{'keyPath': 'id', 'autoIncrement': true}")), serializer)

        override fun deleteStore(name: String) = db.deleteObjectStore(name)

        override suspend fun transaction(
            storeNames: Array<String>,
            mode: Persistence.TransactionMode
        ): Persistence.Transaction = TransactionFacade(db.transaction(storeNames, transactionMode(mode)))

        private fun transactionMode(mode: Persistence.TransactionMode) =
            when (mode) {
                Persistence.TransactionMode.READ_ONLY -> "readonly"
                Persistence.TransactionMode.READ_WRITE -> "readwrite"
            }

        override fun close() = db.close()
    }

    class StoreFacade<T>(
        private val objectStore: IDBObjectStore,
        private val serializer: KSerializer<T>
    ) : Persistence.Store<T> {
        override val name: String get() = objectStore.name

        override suspend fun getAll(): List<T> = suspendCoroutine { cont ->
            val idbRequest = objectStore.getAll()
            fun target(event: Event) = (event.target as IDBRequest)

            idbRequest.onsuccess = { event ->
                val result = target(event).result as JsonElement
                val items = json.fromJson(serializer.list, result)
                cont.resume(items)
            }
            idbRequest.onerror = { event -> cont.resumeWithException(target(event).error) }
        }

        override suspend fun put(value: T) {
            objectStore.put(json.toJson(serializer, value))
        }
    }

    class TransactionFacade(private val transaction: IDBTransaction) : Persistence.Transaction {
        override val mode: Persistence.TransactionMode
            get() = when (transaction.mode) {
                "readonly" -> Persistence.TransactionMode.READ_ONLY
                "readwrite" -> Persistence.TransactionMode.READ_WRITE
                else -> throw IllegalStateException("unknown transaction mode ${transaction.mode}")
            }

        override fun <T> objectStore(name: String, serializer: KSerializer<T>): Persistence.Store<T> {
            return StoreFacade(transaction.objectStore(name), serializer)
        }
    }
}