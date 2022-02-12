package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.model.importers.ObjImporter
import baaahs.only
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.size
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
class ObjImporterSpec : Spek({
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
        val loader by value { ObjImporter.import(objData) }

        it("imports simple OBJ data") {
            expect(loader.entities).size.toEqual(1)
            val surface = loader.entities.only()
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
            expect(loader.errors).isEmpty()
        }

        context("when it contains an error") {
            override(objData) {
                """
                    v 1 2
                """.trimIndent()
            }

            it("creates no entities") {
                expect(loader.entities).isEmpty()
            }

            it("lists errors") {
                expect(loader.errors).containsExactly(
                    Importer.Error("A vertex must have three coordinates: v 1 2", 1)
                )
            }
        }
    }
})