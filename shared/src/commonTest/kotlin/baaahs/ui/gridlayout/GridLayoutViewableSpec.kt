package baaahs.ui.gridlayout

import baaahs.app.settings.Provider
import baaahs.app.ui.editor.Editor
import baaahs.describe
import baaahs.geom.Vector2I
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.show.GridLayout
import baaahs.show.IGridLayout
import baaahs.show.ImpossibleLayoutException
import baaahs.show.live.EmptyOpenContext
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.maps.shouldContainExactly

class GridLayoutViewableSpec : DescribeSpec({
    describe<GridLayout> {
        val layout by value {
            """
                ABB.
                DBBG
                .HI.
                ....
                
                # B:
                WX
                YZ
            """.trimIndent().toLayout()
        }
        val editor by value { SpyEditor() }
        val updatedLayout by value { Array<IGridLayout?>(1) { null } }
        val viewRoot by value {
            ViewRoot(
                layout.open(EmptyOpenContext),
                openShow = object : Provider<OpenShow>() {
                    override fun get(): OpenShow = TODO("not implemented")
                },
                editor = editor,
                onLayoutChange = { newLayout, stillDragging -> updatedLayout[0] = newLayout }
            )
        }
        val rootViewable by value { viewRoot.createViewable() }

        val allViews by value {
            buildMap { viewRoot.visit { put(it.id, it) } }
        }

        context("with no margin or gap") {
            it("lays out grid") {
                viewRoot.layout(Rect(100, 100, 400, 400))
                allViews.mapValues { (_, v) -> v.bounds }
                    .shouldContainExactly(
                        mapOf(
                            "##VIEWROOT##" to Rect(100, 100, 400, 400),
                            "A" to Rect(100, 100, 100, 100),
                            "B" to Rect(200, 100, 200, 200),
                            "D" to Rect(100, 200, 100, 100),
                            "G" to Rect(400, 200, 100, 100),
                            "H" to Rect(200, 300, 100, 100),
                            "I" to Rect(300, 300, 100, 100)
                        )
                    )
            }
        }

        context("with margin and gap") {
            beforeEach {
                viewRoot.gap = 10
                viewRoot.margins = 10
            }

            it("lays out grid") {
                viewRoot.layout(Rect(0, 0, 400, 400))
                allViews.mapValues { (_, v) -> v.bounds }
                    .shouldContainExactly(
                        mapOf(
                            "##VIEWROOT##" to Rect(0, 0, 400, 400),
                            "A" to Rect(10, 10, 88, 88),
                            "B" to Rect(108, 10, 185, 185),
                            "D" to Rect(10, 108, 88, 87),
                            "G" to Rect(303, 108, 87, 87),
                            "H" to Rect(108, 205, 87, 88),
                            "I" to Rect(205, 205, 88, 88)
                        )
                    )
            }
        }

        val find by value {
            { id: String -> viewRoot.findViewable(id) ?: error("Couldn't find $id.") }
        }
        val drag by value {
            { itemId: String, toLayoutId: String?, x: Int, y: Int ->
                val movingViewable = viewRoot.findViewable(itemId) ?: error("Couldn't find $itemId.")
                movingViewable.draggedBy(Vector2I(x, y))
//                viewRoot.moveElement(itemId, toLayoutId, Vector2I(x, y))
//                val view = find(id)
//                val fromLayout = view.parent
//                val toLayout = toLayoutId?.let { find(it) } ?: viewRoot.view
//                viewable
//                view.draggedBy(Vector2I(x, y))
//  TODO              viewRoot.gridLayout.stringify()
                updatedLayout[0]?.stringify() ?: "<no update>"
            }
        }

        context("rearranging") {
            beforeEach {
                viewRoot.layout(Rect(0, 0, 400, 400))
            }

            it("throws if we attempt to move an unknown item") {
                shouldThrow<ImpossibleLayoutException> { (drag("C", null, -1, -1)) }
            }

            it("can drag an item to an open adjacent cell") {
                drag("H", null, 25, 100)
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
            drag("A", null, 0, 2).shouldBe(
                """
                    .BC.
                    DEFG
                    AHI.
                """.trimIndent()
            )
        }

        it("moving A one space right swaps A and B") {
            drag("A", null, 1, 0).shouldBe(
                """
                    BAC.
                    DEFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving A two spaces right shifts C over") {
            drag("A", null, 2, 0).shouldBe(
                """
                    BCA.
                    DEFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving D one space right shifts E into its place") {
            drag("D", null, 1, 0).shouldBe(
                """
                    ABC.
                    EDFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving B one space down and left shifts E down") {
            drag("B", null, -1, 1).shouldBe(
                """
                    A.C.
                    BEFG
                    DHI.
                """.trimIndent()
            )
        }

        context("with ABCDEF in one row") {
            override(layout) { "ABCDEF".toLayout() }
            it("moving B two spaces over") {
                drag("B", null, 2, 0).shouldBe("ACDBEF")
            }
        }

        context("with .ABBC.") {
            override(layout) { ".ABBC.".toLayout() }

            it("moving A one space right should make no change") {
                drag("A", null, 1, 0).shouldBe(".ABBC.")
            }

            it("moving A two spaces right should swap A and B") {
                drag("A", null, 2, 0).shouldBe(".BBAC.")
            }

            it("moving C one space left should make no change") {
                drag("C", null, -1, 0).shouldBe(".ABBC.")
            }

            it("moving C two spaces left should swap B and C") {
                drag("C", null, -2, 0).shouldBe(".ACBB.")
            }
        }
    }
})

class SpyEditor : Editor<MutableIGridLayout> {
    override val title: String get() = "spy editor"
    override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) = TODO("edit not implemented")
    override fun delete(mutableShow: MutableShow) = TODO("delete not implemented")
}