package baaahs.show.mutable

import baaahs.gl.preview.ShaderBuilder
import baaahs.randomId
import baaahs.show.Shader
import baaahs.ui.Observable
import baaahs.ui.addObserver

class EditingShader(
    private val mutableShaderInstance: MutableShaderInstance,
    private val createShaderBuilder: (Shader) -> ShaderBuilder,
): Observable() {
    val id = randomId("EditingShader")
    var state = State.Changed

    val mutableShader: MutableShader get() = mutableShaderInstance.mutableShader
    val title: String get() = mutableShader.title

    var shaderBuilder: ShaderBuilder = createShaderBuilder(build())
        private set

    val gadgets get() = shaderBuilder.gadgets

    fun build(): Shader = mutableShader.build()

    private fun maybeNotifyStateChanging(newState: State) {
        if (state != newState) {
            state = newState
            notifyChanged()
        }
    }

    fun updateSrc(newSrc: String) {
        mutableShader.src = newSrc

        startBuilding()
    }

    private fun startBuilding() {
        val newShaderBuilder = createShaderBuilder(build())
        newShaderBuilder.addObserver {
            val newState = when (it.state) {
                ShaderBuilder.State.Success -> State.Success
                ShaderBuilder.State.Errors -> State.Errors
                else -> State.Building
            }

            maybeNotifyStateChanging(newState)
        }
        shaderBuilder = newShaderBuilder
        newShaderBuilder.startBuilding()
        state = State.Building
        notifyChanged()
    }

    enum class State {
        Changed,
        Building,
        Success,
        Errors
    }
}