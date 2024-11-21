package baaahs.scene.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.toBeSpecified
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Suppress("ClassName")
class V2_ModelEntityIdsSpec : DescribeSpec({
    describe<V2_ModelEntityIds> {
        val migration by value { V2_ModelEntityIds }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }

        context("migration to give entities unique ids in a dictionary") {
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
                        },
                        "controllers": {
                            "SACN:main": {
                                "type": "SACN",
                                "title": "Main",
                                "fixtures": [
                                    {
                                        "entityId": "DJ Lightbox",
                                        "fixtureConfig": {
                                            "type": "PixelArray",
                                            "pixelCount": 240
                                        }
                                    }
                                ]
                            }
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
                                "entityIds": ["djLightbox", "grid", "grid2"]
                            },
                            "controllers": {
                                "SACN:main": {
                                    "type": "SACN",
                                    "title": "Main",
                                    "fixtures": [
                                        {
                                            "entityId": "djLightbox",
                                            "fixtureConfig": {
                                                "type": "PixelArray",
                                                "pixelCount": 240
                                            }
                                        }
                                    ]
                                }
                            },
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
                    """.trimIndent()
                )
            }
        }
    }
})