package baaahs.db

import kotlinx.serialization.KSerializer

class Persistence(private val impl: Impl) {

    suspend fun open(
        name: String,
        version: Long,
        migrateFn: (database: Database, oldVersion: Long, newVersion: Long) -> Unit
    ) = impl.open(name, version, migrateFn)

    fun delete(name: String) = impl.delete(name)

    interface Database {
        val name: String
        val version: Long

        fun close()
        fun <T> createStore(name: String, serializer: KSerializer<T>): Store<T>
        fun deleteStore(name: String)

        suspend fun transaction(
            storeName: String,
            mode: TransactionMode = TransactionMode.READ_ONLY
        ): Transaction = transaction(arrayOf(storeName), mode)

        suspend fun transaction(
            storeNames: Array<String>,
            mode: TransactionMode = TransactionMode.READ_ONLY
        ): Transaction
    }

    interface Store<T> {
        val name: String

        suspend fun getAll(): Array<T>

        suspend fun put(value: T)
    }

    interface Transaction {
        val mode: TransactionMode

        fun <T> objectStore(name: String, serializer: KSerializer<T>): Store<T>
    }

    enum class TransactionMode {
        READ_ONLY,
        READ_WRITE,
    }

    interface Impl {
        suspend fun open(
            name: String,
            version: Long,
            migrateFn: (database: Database, oldVersion: Long, newVersion: Long) -> Unit
        ): Database

        fun delete(name: String)
    }
}