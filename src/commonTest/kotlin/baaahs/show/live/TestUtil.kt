package baaahs.show.live

import baaahs.ShowState
import baaahs.app.ui.editor.PortLinkOption
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.gl.Toolchain
import baaahs.gl.autoWire
import baaahs.gl.shader.type.PaintShader
import baaahs.gl.shader.type.ProjectionShader
import baaahs.gl.shader.type.ShaderType
import baaahs.gl.testToolchain
import baaahs.plugin.core.datasource.SliderDataSource
import baaahs.show.*
import baaahs.show.mutable.*
import baaahs.ui.DragNDrop
import baaahs.ui.DropTarget

fun Toolchain.wireUp(shader: Shader, ports: Map<String, MutablePort> = emptyMap()): MutablePatch {
    val unresolvedPatch = autoWire(shader)
    unresolvedPatch.editShader(shader).apply {
        ports.forEach { (portId, port) ->
            linkOptionsFor(portId).apply {
                clear()
                add(PortLinkOption(port))
            }
        }
    }
    return unresolvedPatch.acceptSuggestedLinkOptions().confirm()
}

fun fakeShader(title: String, type: ShaderType = PaintShader) =
    type.newShaderFromTemplate().apply { this.title = title }.build()

class FakeEditHandler : EditHandler {
    val calls = arrayListOf<List<Any>>()
    lateinit var updatedShow: Show

    override fun onShowEdit(mutableShow: MutableShow, pushToUndoStack: Boolean) {
        calls.add(listOf(mutableShow, pushToUndoStack))
        updatedShow = mutableShow.getShow()
    }

    override fun onShowEdit(show: Show, pushToUndoStack: Boolean) {
        onShowEdit(show, ShowState(emptyMap()), pushToUndoStack)
    }

    override fun onShowEdit(show: Show, showState: ShowState, pushToUndoStack: Boolean) {
        calls.add(listOf(show, showState, pushToUndoStack))
        updatedShow = show
    }
}

class FakeDragNDrop : DragNDrop() {
    fun getDropTargets() = dropTargets.all()

    fun doMove(source: DropTarget, sourceIndex: Int, dest: DropTarget, destIndex: Int) {
        onMove(source, sourceIndex, dest, destIndex)
    }
}

fun OpenControl.fakeRender(): String {
    return when (this) {
        is OpenButtonGroupControl -> "$id[${buttons.joinToString(", ") { it.fakeRender() }}]"
        is OpenButtonControl -> if (isPressed) "*$id*" else id
        else -> id
    }
}

fun OpenShow.fakeRender(controlDisplay: ControlDisplay): String {
    val buf = StringBuilder()

    layouts.panels.values.forEach { panel ->
        buf.append("${panel.title}:\n")
        controlDisplay.render(panel) { panelBucket ->
            buf.append("  |${panelBucket.section.title}|")
            if (panelBucket.controls.isNotEmpty()) {
                buf.append(" ${panelBucket.controls.joinToString(", ") { it.control.fakeRender() }}")
            }
            buf.append("\n")
        }
    }

    return buf.trim().replace(Regex("\\s+\n"), "\n")
}

fun createLayouts(vararg panelNames: String): MutableLayouts {
    return MutableLayouts(
        Layouts(
            panelNames.associateWith { Panel(it) },
            mapOf("default" to Layout(null, emptyList()))
        )
    )
}

fun MutableShow.addFixtureControls() {
    val slider1 = SliderDataSource("slider1", 0f, 0f, 1f, 1f)
    val slider2 = SliderDataSource("slider2", 0f, 0f, 1f, 1f)

    addPatch(testToolchain.wireUp(fakeShader("Show Projection", ProjectionShader)))

    val panel = layouts.findOrCreatePanel("Panel 1")
    val panel1 = layouts.findOrCreatePanel("Panel 2")
    val panel2 = layouts.findOrCreatePanel("Panel 3")
    addButtonGroup(panel, "Scenes") {
        addButton("Scene 1") {
            addPatch(testToolchain.wireUp(fakeShader("Scene 1 Shader")))

            addButtonGroup(panel1, "Backdrops") {
                addButton("Backdrop 1.1") {
                    addPatch(testToolchain.wireUp(fakeShader("Backdrop 1.1 Shader")))
                }
                addButton("Backdrop 1.2") {
                    addPatch(testToolchain.wireUp(fakeShader("Backdrop 1.2 Shader")))
                    addControl(panel2, slider2.buildControl())
                }
            }
            addControl(panel2, slider1.buildControl())
        }

        addButton("Scene 2") {
            addPatch(testToolchain.wireUp(fakeShader("Scene 2 Shader")))

            addButtonGroup(panel1, "Backdrops") {
                addButton("Backdrop 2.1") {
                    addPatch(testToolchain.wireUp(fakeShader("Backdrop 2.1 Shader")))
                    addControl(panel2, slider2.buildControl())
                }
                addButton("Backdrop 2.2") {
                    addPatch(testToolchain.wireUp(fakeShader("Backdrop 2.2 Shader")))
                    addControl(panel2, slider1.buildControl())
                }
            }
        }
    }
}

fun ControlDisplay.renderBuckets(panel: Panel): List<ControlDisplay.PanelBuckets.PanelBucket> {
    val buckets = arrayListOf<ControlDisplay.PanelBuckets.PanelBucket>()
    render(panel) { panelBucket -> buckets.add(panelBucket) }
    return buckets
}