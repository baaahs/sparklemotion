package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.show.migration.V1_UpdateDataSourceRefs
import baaahs.toBeSpecified
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.serialization.json.*

@Suppress("ClassName")
object V1_UpdateDataSourceRefsSpec : DescribeSpec({
    describe<V1_UpdateDataSourceRefs> {
        val migration by value { V1_UpdateDataSourceRefs }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val showJson by value { migration.migrate(fromJsonObj) }

        context("migration of dataSources") {
            override(fromJson) {
                /**language=json*/
                """
                {
                  "title": "Show",
                  "dataSources": {
                    "modelInfo": {
                      "type": "baaahs.plugin.CorePlugin.ModelInfoDataSource",
                      "structType": "ModelInfo"
                    },
                    "time": {
                      "type": "baaahs.plugin.CorePlugin.Time"
                    }
                  }
                }
                """.trimIndent()
            }

            it("fixes feed serial names") {
                val dataSourcesJson = showJson["dataSources"]!!.jsonObject
                dataSourcesJson["modelInfo"].type.shouldBe("baaahs.Core:ModelInfo")
                dataSourcesJson["time"].type.shouldBe("baaahs.Core:Time")
            }
        }
    }
})

val JsonElement?.type: String get() =
    this!!.jsonObject["type"]!!.jsonPrimitive.contentOrNull!!