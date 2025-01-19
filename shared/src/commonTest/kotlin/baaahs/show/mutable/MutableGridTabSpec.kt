package baaahs.show.mutable

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.show.Control
import baaahs.show.GridTab
import baaahs.show.Show
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.ui.gridlayout.stringify
import baaahs.ui.gridlayout.toGridTab
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class MutableGridTabSpec : DescribeSpec({
    describe<MutableGridTab> {
        val initialLayout by value { "" }
        val initialGridTab by value { initialLayout.toGridTab("Tab") }
        val showBuilder by value { ShowBuilder() }
        val mutableGridTab by value { initialGridTab.editForSpec(showBuilder) }
        val rearrangedLayout by value { "" }
        val rearrangedGridTab by value { rearrangedLayout.toGridTab("Tab") }
        val stringifiedResult by value { (mutableGridTab.build(showBuilder) as GridTab).stringify() }

        beforeEach {
            mutableGridTab.applyChanges(rearrangedGridTab)
        }

        context("with no nested grids") {
            override(initialLayout) {
                """
                    abb.
                    dbbg
                    .hi.
                    ....
                """.trimIndent()
            }
            override(rearrangedLayout) {
                """
                    aa..
                    dbbg
                    .bb.
                    .hi.
                """.trimIndent()
            }

            it("modifies the MutableGridTab in place") {
                // This seems tautological but it's ... sort of not?
                stringifiedResult
                    .shouldBe(rearrangedLayout.lowercase())
            }
        }

        context("with nested grids") {
            override(initialLayout) {
                """
                    bb.a
                    bbdg
                    bb..
                    y.hi
                    
                    # b:
                    wx
                    .z
                """.trimIndent()
            }

            context("moving items within their containers") {
                override(rearrangedLayout) {
                    """
                    bb.a
                    bbdg
                    bb..
                    y.hi
                    
                    # b:
                    wx
                    zx
                """.trimIndent()
                }

                it("modifies the MutableGridTab in place") {
                    // This seems tautological but it's ... sort of not?
                    stringifiedResult
                        .shouldBe(rearrangedLayout.lowercase())
                }
            }

            context("moving items between containers") {
                override(rearrangedLayout) {
                    """
                    bb..
                    bbdg
                    bb.x
                    y.hi
                    
                    # b:
                    w.
                    az
                """.trimIndent()
                }

                it("modifies the MutableGridTab in place") {
                    // This seems tautological but it's ... sort of not?
                    println("stringifiedResult = ${stringifiedResult}")
                    stringifiedResult
                        .shouldBe(rearrangedLayout.lowercase())
                }
            }
        }
    }
})

class MutableDummyControl(
    override var asBuiltId: String?,
    override val title: String = asBuiltId ?: "Unnamed",
) : MutableControl {
    override fun buildControl(showBuilder: ShowBuilder): Control =
        DummyControl(asBuiltId, title)

    override fun previewOpen(): OpenControl = TODO("not implemented")

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> =
        TODO("not implemented")
}

data class DummyControl(
    val asBuiltId: String?,
    override val title: String
) : Control {
    override fun createMutable(mutableShow: MutableShow): MutableControl =
        MutableDummyControl(asBuiltId, title)

    override fun open(id: String, openContext: OpenContext): OpenControl =
        TODO("not implemented")
}

fun GridTab.editForSpec(showBuilder: ShowBuilder = ShowBuilder()): MutableGridTab {
    val mutableShow = Show.EmptyShow.edit().apply {
        visit {
            controls[it.controlId] = MutableDummyControl(it.controlId)
                .also { it.build(showBuilder) }
        }
    }
    return edit(emptyMap(), mutableShow).also {
        println("it = ${it}")
    }
}
