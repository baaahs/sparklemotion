package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.importers.ObjImporter
import baaahs.only
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.size
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

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
            expect(results.entities).size.toEqual(1)
            val surface = results.entities.only()
            expect(surface.name).toEqual("Panel 1")
            expect((surface as Model.Surface).faces.map { it.vertices.toList() }).containsExactly(
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
            expect(results.errors).isEmpty()
        }

        context("when it contains an error") {
            override(objData) {
                """
                    v 1 2
                """.trimIndent()
            }

            it("creates no entities") {
                expect(results.entities).isEmpty()
            }

            it("lists errors") {
                expect(results.errors).containsExactly(
                    Importer.Error("A vertex must have three coordinates: v 1 2", 1)
                )
            }
        }
    }
})