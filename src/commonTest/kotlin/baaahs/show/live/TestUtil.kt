package baaahs.show.live

import baaahs.ShowState
import baaahs.gl.patch.AutoWirer
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.*
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShow
import baaahs.ui.DragNDrop
import baaahs.ui.DropTarget
import kotlinx.serialization.json.buildJsonObject

fun AutoWirer.wireUp(shader: Shader, ports: Map<String, MutablePort> = emptyMap()): MutablePatch {
    val unresolvedPatch = autoWire(shader)
    unresolvedPatch.editShader(shader).apply {
        ports.forEach { (portId, port) ->
            linkOptionsFor(portId).apply {
                clear()
                add(port)
            }
        }
    }
    return unresolvedPatch.acceptSymbolicChannelLinks().resolve()
}

fun fakeShader(title: String, type: ShaderType = ShaderType.Paint) =
    Shader(title, type, type.template)

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

    layouts.panelNames.forEach { panelName ->
        buf.append("$panelName:\n")
        controlDisplay.render(panelName) { panelBucket ->
            buf.append("  |${panelBucket.section.title}|")
            if (panelBucket.controls.isNotEmpty()) {
                buf.append(" ${panelBucket.controls.joinToString(", ") { it.control.fakeRender() }}")
            }
            buf.append("\n")
        }
    }

    return buf.trim().replace(Regex("\\s+\n"), "\n")
}

fun createLayouts(vararg panelNames: String): Layouts {
    return Layouts(panelNames.toList(), mapOf("default" to Layout(buildJsonObject { })))
}

fun MutableShow.addFixtureControls() {
    val slider1 = CorePlugin.SliderDataSource("slider1", 0f, 0f, 1f, 1f)
    val slider2 = CorePlugin.SliderDataSource("slider2", 0f, 0f, 1f, 1f)

    addPatch(autoWirer.wireUp(fakeShader("Show Projection", ShaderType.Projection)))

    addButtonGroup("Panel 1", "Scenes") {
        addButton("Scene 1") {
            addPatch(autoWirer.wireUp(fakeShader("Scene 1 Shader")))

            addButtonGroup("Panel 2", "Backdrops") {
                addButton("Backdrop 1.1") {
                    addPatch(autoWirer.wireUp(fakeShader("Backdrop 1.1 Shader")))
                }
                addButton("Backdrop 1.2") {
                    addPatch(autoWirer.wireUp(fakeShader("Backdrop 1.2 Shader")))
                    addControl("Panel 3", slider2.buildControl())
                }
            }
            addControl("Panel 3", slider1.buildControl())
        }

        addButton("Scene 2") {
            addPatch(autoWirer.wireUp(fakeShader("Scene 2 Shader")))

            addButtonGroup("Panel 2", "Backdrops") {
                addButton("Backdrop 2.1") {
                    addPatch(autoWirer.wireUp(fakeShader("Backdrop 2.1 Shader")))
                    addControl("Panel 3", slider2.buildControl())
                }
                addButton("Backdrop 2.2") {
                    addPatch(autoWirer.wireUp(fakeShader("Backdrop 2.2 Shader")))
                    addControl("Panel 3", slider1.buildControl())
                }
            }
        }
    }
}

fun ControlDisplay.renderBuckets(panelName: String): List<ControlDisplay.PanelBuckets.PanelBucket> {
    val buckets = arrayListOf<ControlDisplay.PanelBuckets.PanelBucket>()
    render(panelName) { panelBucket -> buckets.add(panelBucket) }
    return buckets
}

val autoWirer = AutoWirer(Plugins.safe())
