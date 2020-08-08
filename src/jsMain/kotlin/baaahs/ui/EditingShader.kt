package baaahs.ui

import baaahs.*
import baaahs.gadgets.Slider
import baaahs.glshaders.*
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslError
import baaahs.glsl.GlslException
import baaahs.glsl.Shaders
import baaahs.model.ModelInfo
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderInstance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditingShader(
    val mutableShaderInstance: MutableShaderInstance,
    private val autoWirer: AutoWirer
): Observable() {
    var state = State.Changed

    val mutableShader: MutableShader get() = mutableShaderInstance.mutableShader
    val title: String get() = mutableShader.title

    var previewShaderBuilder: PreviewShaderBuilder = PreviewShaderBuilder(build(), autoWirer)
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
        val newPatchBuilder = PreviewShaderBuilder(build(), autoWirer)
        newPatchBuilder.addObserver {
            val newState = when (it.state) {
                PreviewShaderBuilder.State.Success -> State.Success
                PreviewShaderBuilder.State.Errors -> State.Errors
                else -> State.Building
            }

            maybeNotifyStateChanging(newState)
        }
        previewShaderBuilder = newPatchBuilder
        newPatchBuilder.startBuilding()
        notifyChanged()
    }

    enum class State {
        Changed,
        Building,
        Success,
        Errors
    }
}

class PreviewShaderBuilder(val shader: Shader, private val autoWirer: AutoWirer): Observable() {
    var state: State = State.Unbuilt
        private set

    var previewPatch: MutablePatch? = null
        private set
    var linkedPatch: LinkedPatch? = null
        private set
    var glslProgram: GlslProgram? = null
        private set

    var gadgets: List<GadgetData> = emptyList()
        private set
    var glslErrors: List<GlslError> = emptyList()
        private set


    fun startBuilding() {
        state = State.Linking
        notifyChanged()

        GlobalScope.launch {
            link()
        }
    }

    fun startCompile(gl: GlslContext) {
        state = State.Compiling
        notifyChanged()

        GlobalScope.launch {
            val showPlayer = PreviewShowPlayer(autoWirer.plugins, gl)
            compile(gl) { id, dataSource ->
                dataSource.createFeed(showPlayer, id)
            }
            gadgets = showPlayer.gadgets.map { (id, gadget) ->
                GadgetData(id, gadget, "/preview/gadgets/$id")
            }
        }
    }

    fun link() {
        val shaders: Array<Shader> = when (shader.type) {
            ShaderType.Projection -> arrayOf(shader, Shaders.smpteColorBars)
            ShaderType.Distortion -> arrayOf(shader, Shaders.smpteColorBars)
            ShaderType.Paint -> arrayOf(shader)
            ShaderType.Filter -> arrayOf(shader, Shaders.smpteColorBars)
        }

        try {
            previewPatch = autoWirer.autoWire(*shaders)
                .acceptSymbolicChannelLinks()
                .resolve()
            linkedPatch = previewPatch?.openForPreview(autoWirer, shader.type)
            state = State.Linked
        } catch (e: GlslException) {
            glslErrors = e.errors
            state = State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to analyze shader." }
            glslErrors = listOf(GlslError(e.message ?: e.toString()))
            state = State.Errors
        }
        notifyChanged()
    }

    fun compile(gl: GlslContext, resolver: Resolver) {
        try {
            glslProgram = linkedPatch?.compile(gl, resolver)
            state = State.Success
        }  catch (e: GlslException) {
            glslErrors = e.errors
            state = State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to compile patch." }
            glslErrors = listOf(GlslError(e.message ?: e.toString()))
            state = State.Errors
        }
        notifyChanged()
    }

    fun adjustGadgets() {
        val now = clock.now() / 2
        val count = gadgets.size

        val activeGadget = now.rem(count).toInt()
        val rem = now.rem(1f).toFloat()
        val degree = (if (rem >= .5f) (1f - rem) else rem) * 2

        var mask = 0x01
        gadgets.forEachIndexed() { index, gadgetData ->
            val isHigh = (index and mask) != 0

            val myDegree = if (activeGadget == index) {
                if (isHigh) 1f - degree else degree
            } else {
                if (isHigh) 1f else 0f
            }

            val gadget = gadgetData.gadget
            if (gadget is Slider) {
                val range = gadget.maxValue - gadget.minValue
                val scaled = range * myDegree + gadget.minValue
                gadget.value = scaled

            }

            mask = mask.shl(1)
        }
    }

    enum class State {
        Unbuilt,
        Linking,
        Linked,
        Compiling,
        Success,
        Errors
    }

    companion object {
        private val logger = Logger("ShaderEditor")
        private val clock = JsClock()
    }
}

private class PreviewShowPlayer(
    plugins: Plugins,
    override val glslContext: GlslContext
) : BaseShowPlayer(plugins, ModelInfo.Empty) {
    val gadgets: MutableMap<String, Gadget> = hashMapOf()

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
        gadgets[id] = gadget
    }

    override fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(id, "gadget") as T
    }
}
