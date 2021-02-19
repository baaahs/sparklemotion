package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.show.migration.V2_RemoveShaderType
import baaahs.toBeSpecified
import baaahs.toEqual
import baaahs.useBetterSpekReporter
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek

@Suppress("ClassName")
object V2_RemoveShaderTypeSpec : Spek({
    useBetterSpekReporter()

    describe<V2_RemoveShaderType> {
        val migration by value { V2_RemoveShaderType }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }

        context("migration of layouts") {
            override(fromJson) {
                /**language=json*/
                """
                    {
                      "title": "Show",
                      "shaders": {
                        "shader1": {
                            "title": "1",
                            "type": "Anything",
                            "src": "// nothing"
                        }
                      }
                    }
                """.trimIndent()
            }

            it("drops Shader.type") {
                expect(
                    toJsonObj
                        .jsonObject["shaders"]!!
                        .jsonObject["shader1"]!!
                        .jsonObject
                ).toEqual(
                    buildJsonObject {
                        put("title", JsonPrimitive("1"))
                        put("src", JsonPrimitive("// nothing"))
                    }
                )
            }
        }
    }
})