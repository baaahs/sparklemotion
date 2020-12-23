package baaahs.show.mutable

import baaahs.app.ui.editor.LinkOption
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ChannelsInfo
import baaahs.gl.patch.ShaderInstanceOptions
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
    val mutableShaderInstance: MutableShaderInstance,
    private val autoWirer: AutoWirer,
    private val createShaderBuilder: (Shader) -> ShaderBuilder,
) : Observable() {
    val id = randomId("EditingShader")
    var state = State.Building

    val mutableShader: MutableShader get() = mutableShaderInstance.mutableShader
    val title: String get() = mutableShader.title

    var shaderBuilder: ShaderBuilder = createShaderBuilder(mutableShader.build())
        private set

    private var shaderInstanceOptions: ShaderInstanceOptions? = null
    private val linksSelectedByAHuman = HashMap(mutableShaderInstance.incomingLinks)

    val gadgets get() = shaderBuilder.gadgets
    val openShader get() = shaderBuilder.openShader
    val inputPorts get() = openShader?.inputPorts?.sortedBy { it.title }

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
        shaderInstanceOptions = null

        shaderBuilder.addObserver {
            if (it.state == ShaderBuilder.State.Success)
                maybeUpdateIncomingLinks()

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

    private fun maybeUpdateIncomingLinks() {
        openShader?.inputPorts?.forEach { inputPort ->
            if (!linksSelectedByAHuman.containsKey(inputPort.id)) {
                getShaderInstanceOptions()
                    ?.inputPortLinkOptions?.get(inputPort.id)?.firstOrNull()
                    ?.getMutablePort()
                    ?.let { mutableShaderInstance.incomingLinks[inputPort.id] = it }
            }
        }
    }

    fun getShaderInstanceOptions(): ShaderInstanceOptions? {
        val currentOpenShader = openShader
        if (shaderInstanceOptions == null && currentOpenShader != null) {
            val showBuilder = ShowBuilder()
            parentMutablePatch.build(showBuilder)

            val channelsInfo = ChannelsInfo(parentMutableShow, emptyList(), autoWirer)
            shaderInstanceOptions = ShaderInstanceOptions(
                currentOpenShader,
                ShaderChannel.Main,
                channelsInfo,
                currentLinks = mutableShaderInstance.incomingLinks,
                plugins = autoWirer.plugins
            )
        }
        return shaderInstanceOptions
    }

    fun linkOptionsFor(inputPort: InputPort): List<LinkOption>? = linkOptionsFor(inputPort.id)
    fun linkOptionsFor(portId: String): List<LinkOption>? {
        return getShaderInstanceOptions()
            ?.inputPortLinkOptions
            ?.get(portId)
    }

    fun changeInputPortLink(inputPort: InputPort, linkOption: LinkOption?) =
        changeInputPortLink(inputPort.id, linkOption)
    fun changeInputPortLink(portId: String, linkOption: LinkOption?) {
        val incomingLinks = mutableShaderInstance.incomingLinks
        if (linkOption == null) {
            incomingLinks.remove(portId)
        } else {
            val mutablePort = linkOption.getMutablePort()
            if (mutablePort != null) {
                incomingLinks[portId] = mutablePort
                linksSelectedByAHuman[portId] = mutablePort
            } else {
                TODO("what to do?")
            }
        }

        // Prune any unknown port mappings (e.g. if a uniform was removed).
        val knownInputPortIds = openShader!!.inputPorts.map { it.id }
        incomingLinks.keys.minus(knownInputPortIds).forEach { unknownKey ->
            logger.debug { "Removing unknown mapping for unknown port \"$unknownKey\"" }
            incomingLinks.remove(unknownKey)
        }
    }

    fun getInputPortLink(inputPort: InputPort) = getInputPortLink(inputPort.id)
    private fun getInputPortLink(portId: String): MutablePort? {
        return mutableShaderInstance.incomingLinks[portId]
    }

    fun isBuilding(): Boolean = state == State.Building

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