package baaahs.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Storage(private val persistence: Persistence, private val dbName: String = "sparklemotion") {
    private val database = CoroutineScope(Dispatchers.Main).async {
        val lastMigration = migrations.last()
        persistence.open(dbName, lastMigration.number) { db, oldVersion, newVersion ->
            if (oldVersion.toDouble() > newVersion.toDouble()) {
                println("Deleting database $dbName")
                persistence.delete(dbName)
            }

            for (migration in migrations) {
                if (migration.number.toDouble() > oldVersion.toDouble()) {
                    println("Migrating to ${migration.number}...")
                    migration.migrate(db)
                }
            }
        }
    }


    val users = Store("User", User.serializer())
    val simulatorPixels = Store("SimulatorPixels", SimulatorPixels.serializer())

    inner class Store<T>(private val name: String, private val serializer: KSerializer<T>) {
        suspend fun <TT> transaction(function: suspend (store: Persistence.Store<T>) -> TT): TT {
            return function(database.await().transaction(name).objectStore(name, serializer))
        }
    }

    companion object {
        val migrations = listOf(
            Migration(1) { db -> db.createStore("User", User.serializer()) },
            Migration(2) { db -> db.createStore("SimulatorPixels", SimulatorPixels.serializer()) }
        )
    }
}

class Migration(val number: Long, val migrate: suspend (database: Persistence.Database) -> Unit)


@Serializable
data class User(val id: Int, val userName: String)

@Serializable
data class SimulatorPixels(val name: String, val surfaces: Map<String, List<Pixel>>) {
    @Serializable
    data class Pixel(
        val origin: Vector3,
        val distanceFromSurface: Float,
        val orientation: Vector3
    )
}

//@Serializable
//data class Vector2(val x: Float, val y: Float)

@Serializable
data class Vector3(val x: Float, val y: Float, val z: Float)

