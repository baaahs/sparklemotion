package baaahs.gl.preview

import baaahs.*
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslException
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.Resolver
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.glsl.Shaders
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePatch
import baaahs.ui.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PreviewShaderBuilder(val shader: Shader, private val autoWirer: AutoWirer): Observable() {
    var state: State =
        State.Unbuilt
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

    fun startCompile(gl: GlContext) {
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
        val screenCoordsProjection by lazy {
            Shader(
                "Screen Coords", ShaderType.Projection, """
                    uniform vec2 previewResolution;
                    
                    vec2 mainProjection(vec2 fragCoords) {
                      return fragCoords / previewResolution;
                    }
                """.trimIndent()
            )
        }
        val shaders: Array<Shader> = when (shader.type) {
            ShaderType.Projection -> arrayOf(shader,
                Shaders.smpteColorBars
            )
            ShaderType.Distortion -> arrayOf(screenCoordsProjection, shader,
                Shaders.smpteColorBars
            )
            ShaderType.Paint -> arrayOf(screenCoordsProjection, shader)
            ShaderType.Filter -> arrayOf(screenCoordsProjection, shader,
                Shaders.smpteColorBars
            )
        }

        try {
            val defaultPorts = mapOf(
                ContentType.UvCoordinate to MutableConstPort(
                    "gl_FragCoord"
                )
            )
            previewPatch = autoWirer.autoWire(*shaders, defaultPorts = defaultPorts)
                .acceptSymbolicChannelLinks()
                .resolve()
            linkedPatch = previewPatch?.openForPreview(autoWirer)
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

    fun compile(gl: GlContext, resolver: Resolver) {
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

    enum class State {
        Unbuilt,
        Linking,
        Linked,
        Compiling,
        Success,
        Errors
    }

    private class PreviewShowPlayer(
        plugins: Plugins,
        override val glContext: GlContext
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

    companion object {
        private val logger = Logger("ShaderEditor")
    }
}