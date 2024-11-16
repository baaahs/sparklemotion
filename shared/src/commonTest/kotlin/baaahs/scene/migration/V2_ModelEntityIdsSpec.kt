package baaahs.scene.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.toBeSpecified
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Suppress("ClassName")
class V2_ModelEntityIdsSpec : DescribeSpec({
    describe<V2_ModelEntityIds> {
        val migration by value { V2_ModelEntityIds }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }

        context("migration of default grid direction and zigZag") {
            override(fromJson) {
                /**language=json*/
                """
                    {
                        "model": {
                            "title": "Scene",
                            "entities": [
                                {
                                    "type": "Grid",
                                    "title": "DJ Lightbox"
                                },
                                {
                                    "type": "Grid",
                                    "instance": 1
                                },
                                {
                                    "type": "Grid",
                                    "instance": 2
                                }
                            ]
                        }
                    }
                """.trimIndent()
            }

            it("fixes 'em") {
                toJsonObj.toString().shouldEqualJson(
                    /**language=json*/
                    """
                        {
                            "model": {
                                "title": "Scene",
                                "entities": {
                                    "djLightbox": {
                                        "type": "Grid",
                                        "title": "DJ Lightbox"
                                    },
                                    "grid": {
                                        "type": "Grid",
                                        "instance": 1
                                    },
                                    "grid2": {
                                        "type": "Grid",
                                        "instance": 2
                                    }
                                }
                            }
                        }
                    """.trimIndent()
                )
            }
        }
    }
})