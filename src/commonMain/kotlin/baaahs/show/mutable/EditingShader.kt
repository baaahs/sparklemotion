package baaahs.show.mutable

import baaahs.app.ui.editor.DataSourceOption
import baaahs.app.ui.editor.ShaderChannelOption
import baaahs.app.ui.editor.ShaderOption
import baaahs.app.ui.editor.SourcePortOption
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.shader.InputPort
import baaahs.randomId
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.ui.Observable
import baaahs.ui.addObserver
import baaahs.util.Logger

class EditingShader(
    private val parentMutableShow: MutableShow,
    private val parentMutablePatch: MutablePatch,
    private val mutableShaderInstance: MutableShaderInstance,
    private val createShaderBuilder: (Shader) -> ShaderBuilder,
): Observable() {
    val id = randomId("EditingShader")
    var state = State.Building

    val mutableShader: MutableShader get() = mutableShaderInstance.mutableShader
    val title: String get() = mutableShader.title

    var shaderBuilder: ShaderBuilder = createShaderBuilder(mutableShader.build())
        private set

    val gadgets get() = shaderBuilder.gadgets
    val openShader get() = shaderBuilder.openShader
    val inputPorts get() =
        (openShader?.inputPorts?.sortedBy { it.title } ?: emptyList())
            .associateWith { mutableShaderInstance.incomingLinks[it.id] }

    init {
        startBuilding()
    }

    private fun maybeNotifyStateChanging(newState: State) {
        if (state != newState) {
            state = newState
            notifyChanged()
        }
    }

    fun updateSrc(newSrc: String) {
        if (mutableShader.src != newSrc) {
            mutableShader.src = newSrc

            shaderBuilder = createShaderBuilder(mutableShader.build())
            startBuilding()
        }
    }

    private fun startBuilding() {
        shaderBuilder.addObserver {
            val newState = when (it.state) {
                ShaderBuilder.State.Success -> State.Success
                ShaderBuilder.State.Errors -> State.Errors
                else -> State.Building
            }

            maybeNotifyStateChanging(newState)
        }
        shaderBuilder.startBuilding()
        state = State.Building
        notifyChanged()
    }

    fun suggestShaderChannels(): List<ShaderChannel> {
        return (parentMutableShow.shaderChannels + mutableShaderInstance.shaderChannel).toList()
            .sortedBy { it.id }
    }

    fun suggestSourcePortOptions(): List<SourcePortOption> {
        val shaderChannelOptions = suggestShaderChannels()
            .map { ShaderChannelOption(it) }

        val siblingShaderInstances = parentMutablePatch.mutableShaderInstances

        val shaderOptions = siblingShaderInstances
            .minus(mutableShaderInstance)
            .sortedWith(MutableShaderInstance.defaultOrder)
            .map { instance -> ShaderOption(instance) }

        val dataSourceOptions =
            parentMutableShow.dataSources.values
                .sortedBy { it.dataSource.dataSourceName }
                .map { DataSourceOption(it.dataSource) }

        return shaderChannelOptions + shaderOptions + dataSourceOptions
    }

    fun changeInputPort(inputPort: InputPort, sourcePortOption: SourcePortOption?) {
        val incomingLinks = mutableShaderInstance.incomingLinks
        if (sourcePortOption == null) {
            incomingLinks.remove(inputPort.id)
        } else {
            incomingLinks[inputPort.id] = sourcePortOption.portEditor
        }

        // Prune any unknown port mappings (e.g. if a uniform was removed).
        val knownInputPortIds = openShader!!.inputPorts.map { it.id }
        incomingLinks.keys.minus(knownInputPortIds).forEach { unknownKey ->
            logger.debug { "Removing unknown mapping for unknown port \"$unknownKey\"" }
            incomingLinks.remove(unknownKey)
        }
    }

    //    if (it.state == EditingShader.State.Success) {
//        val shader = it.shaderBuilder.shader
//        val wiringGuess = appContext.autoWirer.autoWire(shader)
//            .acceptSymbolicChannelLinks()
//            .takeFirstIfAmbiguous()
//            .resolve()
//        // TODO Improve on this.
//        val editingIncomingLinks = props.mutableShaderInstance.incomingLinks
//        val guessIncomingLinks = wiringGuess.mutableShaderInstances.first().incomingLinks
//
//        editingIncomingLinks.clear()
//        editingIncomingLinks.putAll(guessIncomingLinks)
//    }

    companion object {
        private val logger = Logger<EditingShader>()
    }

    enum class State {
        Building,
        Success,
        Errors
    }
}