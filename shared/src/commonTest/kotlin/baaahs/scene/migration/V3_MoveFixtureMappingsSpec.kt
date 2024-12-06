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
class V3_MoveFixtureMappingsSpec : DescribeSpec({
    describe<V3_MoveFixtureMappings> {
        val migration by value { V3_MoveFixtureMappings }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }

        context("migration to move fixture mappings from within controllers to top level and rename a controller config key") {
            override(fromJson) {
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
                                "defaultFixtureConfig": {
                                    "type": "PixelArray",
                                    "pixelFormat": "GRB8"
                                },
                                "fixtures": [
                                    {
                                        "entityId": "djLightbox",
                                        "fixtureConfig": {
                                            "type": "PixelArray",
                                            "pixelCount": 240
                                        }
                                    }
                                ]
                            },
                            "Brain:DEADBEEF": {
                                "type": "Brain",
                                "title": "DEADBEEF",
                                "fixtures": [
                                    {
                                        "entityId": "grid"
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

            }

            it("moves things about") {
                toJsonObj.toString().shouldEqualJson(
                    /**language=json*/
                    """
                        {
                            "model": {
                                "title": "Scene",
                                "entityIds": [
                                    "djLightbox",
                                    "grid",
                                    "grid2"
                                ]
                            },
                            "controllers": {
                                "SACN:main": {
                                    "type": "SACN",
                                    "title": "Main",
                                    "defaultFixtureOptions": {
                                        "type": "PixelArray",
                                        "pixelFormat": "GRB8"
                                    }
                                },
                                "Brain:DEADBEEF": {
                                    "type": "Brain",
                                    "title": "DEADBEEF"
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
                            },
                            "fixtureMappings": {
                                "SACN:main": [
                                    {
                                        "entityId": "djLightbox",
                                        "fixtureConfig": {
                                            "type": "PixelArray",
                                            "pixelCount": 240
                                        }
                                    }
                                ],
                                "Brain:DEADBEEF": [
                                    {
                                        "entityId": "grid"
                                    }
                                ]
                            }
                        }
                    """.trimIndent()
                )
            }
        }
    }
})