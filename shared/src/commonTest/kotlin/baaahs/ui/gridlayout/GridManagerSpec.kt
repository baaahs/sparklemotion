package baaahs.ui.gridlayout

import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
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
        val gridChanges by value { ArrayList<GridModel>() }
        val originalGridModel by value { tab.createModel() }
        val gridManager by value {
            TestGridManager(
                originalGridModel,
                onChange = { newGridModel -> gridChanges.add(newGridModel) }
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
            gridManager.onNodeResize(
                gridRootLayout.columns * 100,
                gridRootLayout.rows * 100,
            )
        }

        context("with no margin or gap") {
            it("lays out grid") {
                gridManager.onNodeResize(400, 400)
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

        val gesture by value {Gesture(gridManager, gridChanges) }

        val pointerDownOn by value {
            { itemId: String ->
                gesture.onNode(itemId)
                    .pointerDown()
            }
        }

        context("dragging the root node") {
            it("is not allowed") {
                gesture.onRootNode()
                    .pointerDown()
                gridManager.draggingState shouldBe null
            }
        }

        context("tapping an item without moving it") {
            it("should not move anything") {
                pointerDownOn("X")
                    .dropThere()
                    .changes.shouldBeEmpty()
            }
        }

        context("rearranging") {
            it("throws if we attempt to move an unknown item") {
                shouldThrow<IllegalStateException> {
                    pointerDownOn("C")
                        .dragAndDropAt(-1, -1)
                }
            }

            context("if we drag but don't actually rearrange any items") {
                it("shouldn't trigger change") {
                    pointerDownOn("A")
                        .dragAndDropAt(1, 1) // Not enough distance to move anything.
                        .changes.shouldBeEmpty()
                }
            }

            it("can drag an item to an open adjacent cell") {
                pointerDownOn("H")
                    .dragAndDropAt(0, 100)
                    .onlyChange.shouldBe(
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
            pointerDownOn("A")
                .dragAndDropAt(0, 200)
                .onlyChange.shouldBe(
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
            pointerDownOn("H")
                .dragAndDropAt(100, 0)
                .onlyChange.shouldBe(
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

        it("moving B one space down and left moves others around") {
            pointerDownOn("B")
                .dragAndDropAt(-100, 100)
                .onlyChange.shouldBe(
                    """
                    A...
                    BB.G
                    BBI.
                    DH..

                    # B:
                    WX
                    YZ
                    """.trimIndent()
                )
        }

        it("moving item from root to inner grid") {
            pointerDownOn("X")
                .dragAndDropAt(100, 0)
                .onlyChange.shouldBe(
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
                AAA
                BBB
                CC.
                CCX
                ..Y
                
                # A:
                ...
                
                # B:
                ...
                
                # C:
                ..
                .Z
            """.trimIndent().toGridTab("Tab") }

            it("drags an item (X) from root to a subgrid (A)") {
                pointerDownOn("X")
                    .dragAndDropAt(-100, -300)
                    .onlyChange.shouldBe(
                        """
                        AAA
                        BBB
                        CC.
                        CC.
                        ..Y
                        
                        # A:
                        .X.
                        
                        # B:
                        ...
                        
                        # C:
                        ..
                        .Z
                        """.trimIndent()
                    )
            }

            it("drags an item (Z) from a subgrid (C) to root (and bump X up too)") {
                pointerDownOn("Z")
                    .dragAndDropAt(100, 0)
                    .onlyChange.shouldBe(
                        """
                        AAA
                        BBB
                        CCX
                        CCZ
                        ..Y
                        
                        # A:
                        ...
                        
                        # B:
                        ...
                        
                        # C:
                        ..
                        ..
                        """.trimIndent()
                    )
            }

            it("drags an item (Z) from a subgrid (C) to another subgrid (B)") {
                pointerDownOn("Z")
                    .dragAndDropAt(0, -200)
                    .onlyChange.shouldBe(
                        """
                        AAA
                        BBB
                        CC.
                        CCX
                        ..Y
                        
                        # A:
                        ...
                        
                        # B:
                        .Z.
                        
                        # C:
                        ..
                        ..
                        """.trimIndent()
                    )
            }

            // Not implemented (yet?).
            xit("resizes dragged node to fit a smaller container") {
                pointerDownOn("B")
                    .dragAndDropAt(100, 0)
                    .onlyChange.shouldBe(
                        """
                        A.CC.
                        A.CC.
                        A....
                        
                        # C:
                        B.
                        B.
                        """.trimIndent()
                    )
            }
        }

        context("with multicolumn/multirow nodes") {
            override(tab) { """
                AABB.
                AABB.
                CCC..
            """.trimIndent().toGridTab("Tab") }

            it("pushes neighbors") {
                pointerDownOn("A")
                    .dragAndDropAt(100, 0)
                    .onlyChange.shouldBe(
                        """
                        .AABB
                        .AABB
                        CCC..
                        """.trimIndent()
                    )
            }

            it("swaps with neighbors") {
                pointerDownOn("A")
                    .dragAndDropAt(200, 0)
                    .onlyChange.shouldBe(
                        """
                        BBAA.
                        BBAA.
                        CCC..
                        """.trimIndent()
                    )
            }

            it("handles differently-sized block sensibly") {
                pointerDownOn("C")
                    .dragAndDropAt(100, -200)
                    .onlyChange.shouldBe(
                        """
                        .CCC.
                        AABB.
                        AABB.
                        """.trimIndent()
                )
            }
        }

        context("with ABCDEF in one row") {
            override(tab) { "ABCDEF".toGridTab("Tab") }
            it("moving B two spaces over") {
                pointerDownOn("B")
                    .dragAndDropAt(200, 0)
                    .onlyChange.shouldBe("ACDBEF")
            }
        }

        context("with .ABBC.") {
            override(tab) { ".ABBC.".toGridTab("Tab") }

            // Not as implemented.
            xit("moving A one space right should make no change") {
                pointerDownOn("A")
                    .dragAndDropAt(100, 0)
                    .onlyChange.shouldBe(".ABBC.")
            }

            it("moving A two spaces right should swap A and B") {
                pointerDownOn("A")
                    .dragAndDropAt(200, 0)
                    .onlyChange.shouldBe(".BBAC.")
            }

            // Not as implemented.
            xit("moving C one space left should make no change") {
                pointerDownOn("C")
                    .dragAndDropAt(-100, 0)
                    .onlyChange.shouldBe(".ABBC.")
            }

            it("moving C two spaces left should swap B and C") {
                pointerDownOn("C")
                    .dragAndDropAt(-200, 0)
                    .onlyChange.shouldBe(".ACBB.")
            }
        }

        context("with a layout like the default show") {
            override(tab) { """
                CCCCCCCCZZVVV
                CCCCCCCCZZVVV
                CCCCCCCCZZVVV
                SSSSSSSSEEBBB
                SSSSSSSSEEBBB
                SSSSSSSSEEBBB
                SSSSSSSSEEGGG
                SSSSSSSSEEGGG
                SSSSSSSSEEGGG
                SSSSSSSSEEGGG
            """.trimIndent().toGridTab("Tab") }

            it("moving global controls up a few spaces should push beatlink down") {
                println("moving global controls up a few spaces should push beatlink down")
                pointerDownOn("G")
                    .dragAndDropAt(0, -300)
                    .onlyChange.shouldBe(
                        """
                        CCCCCCCCZZVVV
                        CCCCCCCCZZVVV
                        CCCCCCCCZZVVV
                        SSSSSSSSEEGGG
                        SSSSSSSSEEGGG
                        SSSSSSSSEEGGG
                        SSSSSSSSEEGGG
                        SSSSSSSSEEBBB
                        SSSSSSSSEEBBB
                        SSSSSSSSEEBBB
                        """.trimIndent()
                    )
            }
        }

        context("resizing") {
            it("resizes only when dragged past the middle of a cell") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(-49, -49)
                    .dropThere()
                    .changes.shouldBeEmpty()

            }

            context("resizing a node smaller") {
                beforeEach {
                    gesture.onNode("B")
                        .pointerDownOnBottomRightResizeHandle()
                        .dragBy(-60, -60)
                }

                it("will make the node smaller") {
                    gesture.dropThere()
                        .onlyChange.shouldBe(
                            """
                        AB..
                        D..G
                        .HI.
                        ....
            
                        # B:
                        WX
                        YZ
                        """.trimIndent()
                        )
                }

                it("moves the placeholder to preview the new size") {
                    println("moves the placeholder to preview the new size: $gridManager ${gridManager.placeholder} -> ${gridManager.placeholder.bounds}")
                    gridManager.placeholder
                        .bounds shouldBe
                            Rect(100, 0, 100, 100)
                }

                it("scales the DOM node to match during the gesture") {
                    gesture
                        .effectiveBounds shouldBe
                            Rect(100, 0, 140, 140)
                }

                it("won't scale the DOM node smaller than .75x.75 of a 1x1 grid cell") {
                    gesture
                        .dragBy(-100, -100) // Cumulatively -160, -160.
                        .effectiveBounds shouldBe
                            Rect(100, 0, 75, 75)
                }
            }

            it("won't resize to smaller than 1x1") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(-160, -160)
                    .dropThere()
                    .onlyChange.shouldBe(
                        """
                        AB..
                        D..G
                        .HI.
                        ....
            
                        # B:
                        WX
                        YZ
                        """.trimIndent()
                    )
            }

            it("resizes a node bigger vertically") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(0, 60)
                    .dropThere()
                    .onlyChange.shouldBe(
                        """
                        ABB.
                        DBBG
                        .BB.
                        .HI.
            
                        # B:
                        WX
                        YZ
                        """.trimIndent()
                    )
            }

            it("resizes a node bigger horizontally") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(0, 60)
                    .dropThere()
                    .onlyChange.shouldBe(
                        """
                        ABB.
                        DBBG
                        .BB.
                        .HI.
            
                        # B:
                        WX
                        YZ
                        """.trimIndent()
                    )
            }

            it("resizes a node bigger bidirectionally") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(60, 60)
                    .dropThere()
                    .onlyChange.shouldBe(
                        """
                        ABBB
                        DBBB
                        .BBB
                        .HIG
            
                        # B:
                        WX
                        YZ
                        """.trimIndent()
                    )
            }

            it("refuses to resizes a node if it would push other nodes out of bounds") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(0, 160)
                    .dropThere()
                    .changes.shouldBeEmpty()
            }

            it("refuses to resizes a node bigger than will fit in the grid") {
                pointerDownOn("B")
                    .pointerDownOnBottomRightResizeHandle()
                    .dragBy(160, 160)
                    .dropThere()
                    .changes.shouldBeEmpty()
            }

            context("with multiple drags") {
                it("will resize") {
                    pointerDownOn("B")
                        .pointerDownOnBottomRightResizeHandle()
                        .dragBy(0, 60)
                        .dragBy(60, 0)
                        .dragBy(1, 1)
                        .dropThere()
                        .onlyChange.shouldBe(
                            """
                            ABBB
                            DBBB
                            .BBB
                            .HIG
                
                            # B:
                            WX
                            YZ
                            """.trimIndent()
                        )
                }

                it("won't resize a node past where it would push other nodes out of bounds") {
                    pointerDownOn("B")
                        .pointerDownOnBottomRightResizeHandle()
                        .dragBy(0, 60)
                        .dragBy(60, 0)
                        .dragBy(1, 100)
                        .dropThere()
                        .onlyChange.shouldBe(
                            """
                            ABBB
                            DBBB
                            .BBB
                            .HIG
                
                            # B:
                            WX
                            YZ
                            """.trimIndent()
                        )
                }
            }
        }
    }
})
