package baaahs.migrator

import baaahs.describe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@Suppress("unused")
class DataMigratorSpec : DescribeSpec({
    describe<DataMigrator<*>> {
        it("should work with no migrations") {
            val migrator = DataMigrator(Foo.serializer()).Migrate()
            Json.Default.encodeToJsonElement(migrator, Foo("1234"))
                .shouldBe(buildJsonObject {
                    put("value", JsonPrimitive("1234"))
                    put("version", JsonPrimitive(0))
                })
        }
    }

})

@Serializable
private data class Foo(val value: String)