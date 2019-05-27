package baaahs

import baaahs.db.Persistence
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import com.mongodb.reactivestreams.client.Success
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.reactivestreams.KMongo
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MongoDbFacade : Persistence.Impl {
    private val mongoClient = KMongo.createClient()

    companion object {
        val json = Json(JsonConfiguration.Stable)
    }

    override suspend fun open(
        name: String,
        version: Long,
        migrateFn: suspend (database: Persistence.Database, oldVersion: Long, newVersion: Long) -> Unit
    ): Persistence.Database {
        return DatabaseFacade(mongoClient.getDatabase(name))
    }

    override fun delete(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class DatabaseFacade(private val db: MongoDatabase) : Persistence.Database {
        override val name: String = db.name
        override val version: Long
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override suspend fun <T> createStore(name: String, serializer: KSerializer<T>): Persistence.Store<T> =
            suspendCoroutine { cont ->
                db.createCollection(name).subscribe(object : Subscriber<Success?> {
                    override fun onComplete() {
                        cont.resume(StoreFacade(name, db.getCollection(name) as MongoCollection<T>))
                    }

                    override fun onSubscribe(s: Subscription?) {
                    }

                    override fun onNext(t: Success?) {
                    }

                    override fun onError(t: Throwable?) {
                        cont.resumeWithException(t!!)
                    }
                })
            }

        override fun deleteStore(name: String) {
            db.getCollection(name).drop()
        }

        override suspend fun transaction(
            storeNames: Array<String>,
            mode: Persistence.TransactionMode
        ): Persistence.Transaction {
            return TransactionFacade()
        }

        override fun close() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        inner class StoreFacade<T>(
            override val name: String,
            private val collection: MongoCollection<T>
        ) : Persistence.Store<T> {

            override suspend fun getAll(): List<T> {
                return collection.find().toList()
            }

            override suspend fun put(value: T) {
                collection.insertOne(value)
            }
        }

        inner class TransactionFacade : Persistence.Transaction {
            override val mode: Persistence.TransactionMode
                get() = TODO("not implemented")

            override fun <T> objectStore(name: String, serializer: KSerializer<T>): Persistence.Store<T> {
                return StoreFacade<T>(name, db.getCollection(name) as MongoCollection<T>)
            }

        }
    }
}