//package baaahs.ui.gridlayout
//
//import baaahs.kotest.value
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.matchers.maps.shouldContainExactly
//import io.kotest.matchers.shouldBe
//
//fun LayoutModel<String, String>.allBoxes(): Map<String, Rect?> =
//    mapOf("root" to box) + items.associate { it.id to it.box }
//
//class GridRootSpec : DescribeSpec({
//    describe<LayoutModel<*, *>> {
//        val layoutModel by value { LayoutModel<String, String>(4, 4) { id -> id } }
//
//        describe("before layout has been called") {
//            it("should have no boxes") {
//                layoutModel.box shouldBe null
//            }
//        }
//
//        context("with some items") {
//            beforeEach {
//                layoutModel.addItem("A", 0, 0)
//                layoutModel.addItem("B", 1, 0)
//                layoutModel.addGrid("C", 2, 0, 2, 2)
//                layoutModel.addItem("D", 3, 3)
//            }
//
//            describe("before layout has been called") {
//                it("should have no boxes") {
//                    layoutModel.allBoxes() shouldContainExactly mapOf(
//                        "root" to null,
//                        "A" to null,
//                        "B" to null,
//                        "C" to null,
//                        "D" to null,
//                    )
//                }
//            }
//
//            describe("when layout has been called") {
//                beforeEach {
//                    layoutModel.layout(Rect(0, 0, 100, 100))
//                }
//
//                it("should have appropriately-sized boxes") {
//                    layoutModel.allBoxes() shouldContainExactly mapOf(
//                        "root" to Rect(0, 0, 100, 100),
//                        "A" to Rect(0, 0, 25, 25),
//                        "B" to Rect(25, 0, 25, 25),
//                        "C" to Rect(50, 0, 50, 50),
//                        "D" to Rect(75, 75, 25, 25)
//                    )
//                }
//            }
//        }
//    }
//})