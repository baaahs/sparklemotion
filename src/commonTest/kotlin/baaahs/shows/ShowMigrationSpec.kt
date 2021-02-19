package baaahs.shows

import baaahs.describe
import baaahs.gl.testPlugins
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.show.migration.AllMigrations
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.spekframework.spek2.Spek

object ShowMigrationSpec : Spek({
    describe<ShowMigrator> {
        val json by value { Json { serializersModule = testPlugins().serialModule } }

        context("when writing") {
            val toJson by value { json.encodeToJsonElement(ShowMigrator, Show("test")) }
            it("includes version") {
                expect(toJson.jsonObject["version"]?.jsonPrimitive?.intOrNull)
                    .toBe(AllMigrations.last().toVersion)
            }
        }
    }
})