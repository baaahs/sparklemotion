package baaahs.plugins

import kotlinx.serialization.json.JsonObject

interface InputPlugin {
    val name: String

    fun createDataSource(config: JsonObject): DataSource<*>

    interface DataSource<T> {
        val name: String

        fun getValue(): T
    }
}