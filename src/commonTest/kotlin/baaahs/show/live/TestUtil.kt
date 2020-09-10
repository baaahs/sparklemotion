package baaahs.show.live

import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.app.ui.DropTarget
import baaahs.getBang
import baaahs.gl.patch.AutoWirer
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.Show
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShow

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

    override fun onShowEdit(mutableShow: MutableShow, pushToUndoStack: Boolean) {
        calls.add(listOf(mutableShow, pushToUndoStack))
    }

    override fun onShowEdit(show: Show, showState: ShowState, pushToUndoStack: Boolean) {
        calls.add(listOf(show, showState, pushToUndoStack))
    }
}

class FakeDragNDrop : DragNDrop() {
    fun getDropTargets() = dropTargets.all()
}

fun OpenShow.fakeRender(controlDisplay: ControlDisplay): String {
    val buf = StringBuilder()

    layouts.panelNames.forEach { panelName ->
        buf.append("$panelName:\n")
        controlDisplay.render(panelName) { dropTargetId, section, controls ->
            buf.append("  |${section.title}| ${controls.joinToString { it.control.id }}\n")
        }
    }

    return buf.trim().toString()
}
