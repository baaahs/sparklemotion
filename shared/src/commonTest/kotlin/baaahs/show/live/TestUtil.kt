package baaahs.show.live

import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.Toolchain
import baaahs.gl.autoWire
import baaahs.gl.shader.type.PaintShader
import baaahs.gl.shader.type.ShaderType
import baaahs.show.Shader
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableDocument
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort

fun Toolchain.wireUp(shader: Shader, ports: Map<String, MutablePort> = emptyMap()): MutablePatch {
    val unresolvedPatch = autoWire(shader)
    unresolvedPatch.apply {
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

class FakeEditHandler : EditHandler<Show, ShowState> {
    val calls = arrayListOf<List<Any>>()
    lateinit var updatedShow: Show

    override fun onEdit(mutableDocument: MutableDocument<Show>, pushToUndoStack: Boolean) {
        calls.add(listOf(mutableDocument, pushToUndoStack))
        updatedShow = mutableDocument.build()
    }

    override fun onEdit(document: Show, pushToUndoStack: Boolean) {
        onEdit(document, ShowState(emptyMap()), pushToUndoStack)
    }

    override fun onEdit(document: Show, documentState: ShowState, pushToUndoStack: Boolean) {
        calls.add(listOf(document, documentState, pushToUndoStack))
        updatedShow = document
    }
}
