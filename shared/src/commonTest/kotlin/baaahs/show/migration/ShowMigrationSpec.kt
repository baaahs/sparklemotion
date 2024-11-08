package baaahs.show.migration

import baaahs.describe
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.show.Show
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ShowMigrationSpec : DescribeSpec({
    describe<ShowMigrator> {
        val json by value { Json { serializersModule = testPlugins().serialModule } }

        context("when writing") {
            val toJson by value { json.encodeToJsonElement(ShowMigrator.Migrate(), Show("test")) }
            it("includes version") {
                expect(toJson.jsonObject["version"]?.jsonPrimitive?.intOrNull)
                    .toBe(AllShowMigrations.last().toVersion)
            }
        }
    }
})