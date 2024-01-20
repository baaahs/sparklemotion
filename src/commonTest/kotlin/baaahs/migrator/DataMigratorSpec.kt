package baaahs.migrator

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.spekframework.spek2.Spek

@Suppress("unused")
object DataMigratorSpec : Spek({
    describe<DataMigrator<*>> {
        it("should work with no migrations") {
            val migrator = DataMigrator(Foo.serializer())
            expect(Json.Default.encodeToJsonElement(migrator, Foo("1234")))
                .toEqual(buildJsonObject {
                    put("value", JsonPrimitive("1234"))
                    put("version", JsonPrimitive(0))
                })
        }
    }

})

@Serializable
private data class Foo(val value: String)