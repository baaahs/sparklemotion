package baaahs.show.migration.scene

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.show.migration.V6_FlattenPatches
import baaahs.toBeSpecified
import baaahs.toEqual
import baaahs.useBetterSpekReporter
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.spekframework.spek2.Spek

@Suppress("ClassName")
object V1_GridDirectionBackwardsSpec : Spek({
    useBetterSpekReporter()

    describe<V6_FlattenPatches> {
        val migration by value { V1_GridDirectionBackwards }
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
                            "title": "DJ Lightbox",
                            "rows": 36,
                            "columns": 9,
                            "rowGap": 2.5,
                            "columnGap": 2.5,
                            "direction": "ColumnsThenRows",
                            "zigZag": true
                        }
                    ]
                }
            }
                """.trimIndent()
            }

            it("fixes 'em") {
                expect(toJsonObj).toEqual(json.parseToJsonElement(
                    /**language=json*/
                    """
                        {
                            "model": {
                                "title": "Scene",
                                "entities": [
                                    {
                                        "type": "Grid",
                                        "title": "DJ Lightbox",
                                        "rows": 36,
                                        "columns": 9,
                                        "rowGap": 2.5,
                                        "columnGap": 2.5
                                    }
                                ]
                            }
                        }
                    """.trimIndent()
                ).jsonObject)
            }
        }

        context("migration of other grid direction and zigZag") {
            override(fromJson) {
                /**language=json*/
                """
            {
                "model": {
                    "title": "Scene",
                    "entities": [
                        {
                            "type": "Grid",
                            "title": "DJ Lightbox",
                            "rows": 36,
                            "columns": 9,
                            "rowGap": 2.5,
                            "columnGap": 2.5,
                            "direction": "RowsThenColumns"
                        }
                    ]
                }
            }
                """.trimIndent()
            }

            it("fixes 'em") {
                expect(toJsonObj).toEqual(json.parseToJsonElement(
                    /**language=json*/
                    """
                        {
                            "model": {
                                "title": "Scene",
                                "entities": [
                                    {
                                        "type": "Grid",
                                        "title": "DJ Lightbox",
                                        "rows": 36,
                                        "columns": 9,
                                        "rowGap": 2.5,
                                        "columnGap": 2.5,
                                        "direction": "ColumnsThenRows",
                                        "zigZag": false
                                    }
                                ]
                            }
                        }
                    """.trimIndent()
                ).jsonObject)
            }
        }
    }
})