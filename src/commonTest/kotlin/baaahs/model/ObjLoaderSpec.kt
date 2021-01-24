package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object ObjLoaderSpec : Spek({
    describe<ObjLoader> {
        val objStr by value {
            """
                # OBJ model file (sort of)
                # Built by hand by @xian
                # File units = inches

                v 0 48.5 0
                v 0 120 0
                v 48.5 120 0
                v 48.5 48 0

                v 60 48 0
                v 60 120 0
                v 108 120 0

                o Panel 1
                f 1 3 2
                f 1 4 3
                l 1 2
                l 2 3
                l 3 4
                l 4 1

                o Panel 2
                f 5 7 6
                l 5 6
                l 6 7
                l 7 5
            """.trimIndent()
        }

        val objData by value { ObjLoader.load(objStr) }

        it("should load the objects") {
            val allVertices = listOf(
                Vector3F(0f, 48.5f, 0f),
                Vector3F(0f, 120f, 0f),
                Vector3F(48.5f, 120f, 0f),
                Vector3F(48.5f, 48f, 0f),
                Vector3F(60f, 48f, 0f),
                Vector3F(60f, 120f, 0f),
                Vector3F(108f, 120f, 0f)
            )

            expect(objData).toBe(
                ObjLoader.ObjData(
                    listOf(
                        ObjLoader.Obj(
                            "Panel 1",
                            faces = listOf(
                                Model.Face(allVertices, 0, 2, 1),
                                Model.Face(allVertices, 0, 3, 2),
                            ),
                            lines = listOf(
                                Model.Line(listOf(allVertices[0], allVertices[1])),
                                Model.Line(listOf(allVertices[1], allVertices[2])),
                                Model.Line(listOf(allVertices[2], allVertices[3])),
                                Model.Line(listOf(allVertices[3], allVertices[0])),
                            )
                        ),
                        ObjLoader.Obj(
                            "Panel 2",
                            faces = listOf(
                                Model.Face(allVertices, 4, 6, 5)
                            ),
                            lines = listOf(
                                Model.Line(listOf(allVertices[4], allVertices[5])),
                                Model.Line(listOf(allVertices[5], allVertices[6])),
                                Model.Line(listOf(allVertices[6], allVertices[4]))
                            )
                        ),
                        ),
                    allVertices
                )
            )
        }
    }
})