package baaahs.ui.gridlayout

import io.kotest.core.spec.style.DescribeSpec
import baaahs.*
import io.kotest.matchers.shouldBe

class NodeSpec : DescribeSpec({
   describe<Node> {
       val grid = """
               AB
               C.
           """.trimIndent().toGridTab("Tab").createModel()

       describe("#attemptToPlace") {
           it("will place the item if there are no collisions") {
               val newNode = Node("D", 1, 1, 1, 1, null)
               val result = grid.rootNode.addNode(newNode).attemptToPlace(
                   newNode, 1, 1, arrayOf(Direction.West)
               )
               result.stringify().shouldBe("""
                   AB
                   CD
               """.trimIndent())
           }

           it("attempts to find a place for a new node") {
               val newNode = Node("D", 1, 0, 1, 1, null)
               val result = grid.rootNode.addNode(newNode).attemptToPlace(
                   newNode, 0, 1, arrayOf(Direction.West, Direction.East)
               )
               result.stringify().shouldBe("""
                   AB
                   DC
               """.trimIndent())
           }
       }
   }
})