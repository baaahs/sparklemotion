package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.show.GridItem
import baaahs.show.GridLayout
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class GridHelperSpec : DescribeSpec({
    describe("test helpers") {
        it("convert from string to layout") {
            """
                AA.B
                AA..
                .CC.
                
                # A:
                XY
                XZ
            """.trimIndent().toLayout()
                .shouldBe(
                    GridLayout(
                        4, 3, matchParent = false,
                        listOf(
                            GridItem("A", 0, 0, 2, 2, layout =
                                GridLayout(2, 2, matchParent = false,
                                    listOf(
                                        GridItem("X", 0, 0, 1, 2),
                                        GridItem("Y", 1, 0, 1, 1),
                                        GridItem("Z", 1, 1, 1, 1),
                                    )
                                ),
                            ),
                            GridItem("B", 3, 0, 1, 1),
                            GridItem("C", 1, 2, 2, 1),
                        )
                    )
                )
        }

        it("convert from layout to string") {
            GridLayout(
                4, 3, matchParent = false,
                listOf(
                    GridItem("A", 0, 0, 2, 2, layout =
                        GridLayout(2, 2, matchParent = false,
                            listOf(
                                GridItem("X", 0, 0, 1, 2),
                                GridItem("Y", 1, 0, 1, 1),
                                GridItem("Z", 1, 1, 1, 1),
                            )
                        )
                    ),
                    GridItem("B", 3, 0, 1, 1),
                    GridItem("C", 1, 2, 2, 1)
                )
            ).stringify().shouldBe(
                """
                    AA.B
                    AA..
                    .CC.
                    
                    # A:
                    XY
                    XZ
                """.trimIndent()
            )
        }
    }
})