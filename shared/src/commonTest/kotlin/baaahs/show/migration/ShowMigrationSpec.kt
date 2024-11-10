package baaahs.show.migration

import baaahs.describe
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.show.Show
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ShowMigrationSpec : DescribeSpec({
    describe<ShowMigrator> {
        val json by value { Json { serializersModule = testPlugins().serialModule } }

        context("when writing") {
            val toJson by value { json.encodeToJsonElement(ShowMigrator.Migrate(), Show("test")) }
            it("includes version") {
                toJson.jsonObject["version"]?.jsonPrimitive?.intOrNull.shouldBe(AllShowMigrations.last().toVersion)
            }
        }
    }
})