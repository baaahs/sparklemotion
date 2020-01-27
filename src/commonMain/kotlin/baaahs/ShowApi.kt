package baaahs

import baaahs.plugins.InputPlugin
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import kotlin.reflect.KClass

interface ShowApi {
    val allMovingHeads: List<MovingHead>
    val allSurfaces: List<Surface>
    val allUnusedSurfaces: List<Surface>
    val clock: Clock
    val currentBeat: Float

    fun <T : Gadget> getGadget(name: String, gadget: T): T

    fun <B : Shader.Buffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B

    fun getCompositorBuffer(
        surface: Surface,
        bufferA: Shader.Buffer,
        bufferB: Shader.Buffer,
        mode: CompositingMode = CompositingMode.NORMAL,
        fade: Float = 0.5f
    ): CompositorShader.Buffer

    fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer

    fun getBeatSource(): BeatSource

    fun <T : InputPlugin> getPlugin(pluginType: KClass<T>): T
}