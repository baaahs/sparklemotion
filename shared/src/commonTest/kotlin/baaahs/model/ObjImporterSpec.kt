package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.importers.ObjImporter
import baaahs.only
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

@Suppress("unused")
class ObjImporterSpec : DescribeSpec({
    describe<ObjImporter> {
        val objData by value {
            """
                v 0 48 0
                v 0 120 0
                v 48 120 0
                v 48 48 0

                o Panel 1
                f 1 3 2
                f 1 4 3
                l 1 2
                l 2 3
                l 3 4
                l 4 1
            """.trimIndent()
        }
        val results by value { ObjImporter.import(objData, idPrefix = "") }

        it("imports simple OBJ data") {
            results.entities.size.shouldBe(1)
            val surface = results.entities.only()
            surface.name.shouldBe("Panel 1")
            (surface as Model.Surface).faces.map { it.vertices.toList() }.shouldContainExactly(
                listOf(
                    Vector3F(x = 0.0, y = 48.0, z = 0.0),
                    Vector3F(x = 48.0, y = 120.0, z = 0.0),
                    Vector3F(x = 0.0, y = 120.0, z = 0.0)
                ),
                listOf(
                    Vector3F(x = 0.0, y = 48.0, z = 0.0),
                    Vector3F(x = 48.0, y = 48.0, z = 0.0),
                    Vector3F(x = 48.0, y = 120.0, z = 0.0)
                )
            )
        }

        it("lists no errors") {
            results.errors.shouldBeEmpty()
        }

        context("when it contains an error") {
            override(objData) {
                """
                    v 1 2
                """.trimIndent()
            }

            it("creates no entities") {
                results.entities.shouldBeEmpty()
            }

            it("lists errors") {
                results.errors.shouldContainExactly(
                    Importer.Error("A vertex must have three coordinates: v 1 2", 1)
                )
            }
        }
    }
})