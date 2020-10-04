package baaahs.show.mutable

import baaahs.gl.patch.AutoWirer
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.model.ModelInfo
import baaahs.randomId
import baaahs.show.Shader
import baaahs.ui.Observable
import baaahs.ui.addObserver

class EditingShader(
    val mutableShaderInstance: MutableShaderInstance,
    private val autoWirer: AutoWirer,
    private val modelInfo: ModelInfo
): Observable() {
    val id = randomId("EditingShader")
    var state = State.Changed

    val mutableShader: MutableShader get() = mutableShaderInstance.mutableShader
    val title: String get() = mutableShader.title

    var previewShaderBuilder: PreviewShaderBuilder =
        PreviewShaderBuilder(build(), autoWirer, modelInfo)
        private set

    val gadgets get() = previewShaderBuilder.gadgets

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
        val newPreviewShaderBuilder = PreviewShaderBuilder(build(), autoWirer, modelInfo)
        newPreviewShaderBuilder.addObserver {
            val newState = when (it.state) {
                PreviewShaderBuilder.State.Success -> State.Success
                PreviewShaderBuilder.State.Errors -> State.Errors
                else -> State.Building
            }

            maybeNotifyStateChanging(newState)
        }
        previewShaderBuilder = newPreviewShaderBuilder
        newPreviewShaderBuilder.startBuilding()
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