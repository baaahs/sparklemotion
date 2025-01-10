package baaahs.ui.gridlayout

import baaahs.app.ui.editor.Editor
import baaahs.describe
import baaahs.geom.Vector2I
import baaahs.getBang
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.show.ImpossibleLayoutException
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe

class GridManagerSpec : DescribeSpec({
    describe<GridManager> {
        val tab by value {
            """
                ABB.
                DBBG
                .HI.
                ....

                # B:
                WX
                YZ
            """.trimIndent().toGridTab("Tab")
        }
        val editor by value { SpyEditor() }
        val updatedGrid by value { Array<GridModel?>(1) { null } }
        val originalGridModel by value { tab.createModel() }
        val gridManager by value {
            TestGridManager(
                originalGridModel,
                onChange = { newGridModel -> updatedGrid[0] = newGridModel }
            ).also {
                it.margin = 0
                it.gap = 0
                it.editable(true)
            }
        }

        val allViews by value {
            gridManager.nodeWrappers
        }

        beforeEach {
            // Make each cell 100x100px.
            val gridRootLayout = originalGridModel.rootNode.layout!!
            gridManager.onResize(
                gridRootLayout.columns * 100,
                gridRootLayout.rows * 100,
            )
        }

        context("with no margin or gap") {
            it("lays out grid") {
                gridManager.onResize(400, 400)
                allViews.mapValues { (_, v) -> v.effectiveBounds }
                    .shouldContainExactly(
                        mapOf(
                            "_ROOT_" to Rect(0, 0, 400, 400),
                            "A" to Rect(0, 0, 100, 100),
                            "B" to Rect(100, 0, 200, 200),
                            "D" to Rect(0, 100, 100, 100),
                            "G" to Rect(300, 100, 100, 100),
                            "H" to Rect(100, 200, 100, 100),
                            "I" to Rect(200, 200, 100, 100),
                            "W" to Rect(100, 0, 100, 100),
                            "X" to Rect(200, 0, 100, 100),
                            "Y" to Rect(100, 100, 100, 100),
                            "Z" to Rect(200, 100, 100, 100)
                        )
                    )
            }
        }

//        context("with margin and gap") {
//            beforeEach {
//                gridManager.gap = 10
//                gridManager.margins = 10
//            }
//
//            it("lays out grid") {
//                gridManager.layout(Rect(0, 0, 400, 400))
//                allViews.mapValues { (_, v) -> v.bounds }
//                    .shouldContainExactly(
//                        mapOf(
//                            "##VIEWROOT##" to Rect(0, 0, 400, 400),
//                            "A" to Rect(10, 10, 88, 88),
//                            "B" to Rect(108, 10, 185, 185),
//                            "D" to Rect(10, 108, 88, 87),
//                            "G" to Rect(303, 108, 87, 87),
//                            "H" to Rect(108, 205, 87, 88),
//                            "I" to Rect(205, 205, 88, 88)
//                        )
//                    )
//            }
//        }

        val find by value {
            { id: String -> gridManager.nodeWrappers.getBang(id, "node wrapper") }
        }
        val drag by value {
            { itemId: String, toLayoutId: String?, x: Int, y: Int ->
                val movingViewable = find(itemId)
                val pointerDownPoint = movingViewable.layoutBounds!!.center
                movingViewable.onPointerDown(pointerDownPoint)
                val offset = Vector2I(x, y)
                movingViewable.onPointerMove(pointerDownPoint + offset)
                movingViewable.onPointerUp(pointerDownPoint + offset)
//                movingViewable.draggedBy(Vector2I(x, y))
//                viewRoot.moveElement(itemId, toLayoutId, Vector2I(x, y))
//                val view = find(id)
//                val fromLayout = view.parent
//                val toLayout = toLayoutId?.let { find(it) } ?: viewRoot.view
//                viewable
//                view.draggedBy(Vector2I(x, y))
//  TODO              viewRoot.gridLayout.stringify()
                updatedGrid[0]?.rootNode?.stringify() ?: "<no update>"
            }
        }

        context("rearranging") {
            it("throws if we attempt to move an unknown item") {
                shouldThrow<ImpossibleLayoutException> { (drag("C", null, -1, -1)) }
            }

            it("can drag an item to an open adjacent cell") {
                drag("H", null, 0, 100)
                    .shouldBe(
                        """
                            ABB.
                            DBBG
                            ..I.
                            .H..

                            # B:
                            WX
                            YZ
                        """.trimIndent()
                )
            }

//            it("moving C to E's spot fails") {
//                val c = find("C")
//                c.draggedBy(c.bounds.left + 1)
//                shouldThrow<ImpossibleLayoutException> { (move("C", -1, -1)) }
//            }
        }

        it("will move an item to an open spot leaving items in between undisturbed") {
            drag("A", null, 0, 200).shouldBe(
                """
                    .BB.
                    DBBG
                    AHI.
                    ....
    
                    # B:
                    WX
                    YZ
                """.trimIndent()
            )
        }

        it("moving H one space right swaps H and I") {
            drag("H", null, 100, 0).shouldBe(
                """
                    ABB.
                    DBBG
                    .IH.
                    ....
    
                    # B:
                    WX
                    YZ
                """.trimIndent()
            )
        }

        it("moving A two spaces right shifts C over") {
            drag("A", null, 200, 0).shouldBe(
                """
                    BBA.
                    BBDG
                    .HI.
                    ....
    
                    # B:
                    WX
                    YZ
                """.trimIndent()
            )
        }

        it("moving D one space right shifts E into its place") {
            drag("D", null, 100, 0).shouldBe(
                """
                    ABC.
                    EDFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving B one space down and left shifts E down") {
            drag("B", null, -100, 100).shouldBe(
                """
                    A.C.
                    BEFG
                    DHI.
                """.trimIndent()
            )
        }

        it("moving item between grids") {
            drag("X", null, 100, 0).shouldBe(
                """
                    ABBX
                    DBBG
                    .HI.
                    ....
    
                    # B:
                    W.
                    YZ
                """.trimIndent()
            )
        }

        context("dragging between grids") {
            override(tab) { """
                ABCC.
                ABCC.
                AB...
                
                # C:
                ..
                ..
            """.trimIndent().toGridTab("Tab") }

            it("resizes dragged node to fit a smaller container") {
                drag("B", null, 100, 0).shouldBe("""
                    A.CC.
                    A.CC.
                    A....
                    
                    # C:
                    B.
                    B.
                """.trimIndent())
            }
        }

        context("with multicolumn/multirow nodes") {
            override(tab) { """
                AABB.
                AABB.
                .....
            """.trimIndent().toGridTab("Tab") }

            it("pushes neighbors") {
                drag("A", null, 100, 0).shouldBe("""
                    .AABB
                    .AABB
                    .....
                """.trimIndent())
            }

            it("swaps with neighbors") {
                drag("A", null, 200, 0).shouldBe("""
                    BBAA.
                    BBAA.
                    .....
                """.trimIndent())
            }
        }

        context("with ABCDEF in one row") {
            override(tab) { "ABCDEF".toGridTab("Tab") }
            it("moving B two spaces over") {
                drag("B", null, 200, 0).shouldBe("ACDBEF")
            }
        }

        context("with .ABBC.") {
            override(tab) { ".ABBC.".toGridTab("Tab") }

            it("moving A one space right should make no change") {
                drag("A", null, 100, 0).shouldBe(".ABBC.")
            }

            it("moving A two spaces right should swap A and B") {
                drag("A", null, 200, 0).shouldBe(".BBAC.")
            }

            it("moving C one space left should make no change") {
                drag("C", null, -100, 0).shouldBe(".ABBC.")
            }

            it("moving C two spaces left should swap B and C") {
                drag("C", null, -200, 0).shouldBe(".ACBB.")
            }
        }
    }
})

class SpyEditor : Editor<MutableIGridLayout> {
    override val title: String get() = "spy editor"
    override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) = TODO("edit not implemented")
    override fun delete(mutableShow: MutableShow) = TODO("delete not implemented")
}