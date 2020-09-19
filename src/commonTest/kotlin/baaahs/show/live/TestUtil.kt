package baaahs.show.live

import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.app.ui.DropTarget
import baaahs.getBang
import baaahs.gl.patch.AutoWirer
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.*
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShow
import kotlinx.serialization.json.buildJsonObject

fun AutoWirer.wireUp(shader: Shader, ports: Map<String, MutablePort> = emptyMap()): MutablePatch {
    val unresolvedPatch = autoWire(shader)
    unresolvedPatch.editShader(shader).apply {
        ports.forEach { (portId, port) ->
            incomingLinksOptions.getBang(portId, "port").apply {
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

fun OpenShow.fakeRender(controlDisplay: ControlDisplay): String {
    val buf = StringBuilder()

    layouts.panelNames.forEach { panelName ->
        buf.append("$panelName:\n")
        controlDisplay.render(panelName) { panelBucket ->
            buf.append("  |${panelBucket.section.title}| ${panelBucket.controls.joinToString { it.control.id }}\n")
        }
    }

    return buf.trim().replace(Regex("\\s+\n"), "\n")
}

fun createLayouts(vararg panelNames: String): Layouts {
    return Layouts(panelNames.toList(), mapOf("default" to Layout(buildJsonObject { })))
}

fun MutableShow.addFixtureControls() {
    val autoWirer = AutoWirer(Plugins.safe())

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