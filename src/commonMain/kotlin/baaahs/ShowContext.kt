package baaahs

import baaahs.model.MovingHead
import baaahs.shaders.GlslShader
import baaahs.shaders.IGlslShader

interface ShowContext {
    val allSurfaces: List<Surface>
    val allUnusedSurfaces: List<Surface>
    val allMovingHeads: List<MovingHead>

    // Continuous from [0.0 ... 3.0] (0 is first beat in a measure, 3 is last)
    val currentBeat: Float

    fun getBeatSource(): BeatSource

    /**
     * Obtain a shader buffer which can be used to control the illumination of a surface.
     *
     * @param surface The surface we're shading.
     * @param shader The type of shader.
     * @return A shader buffer of the appropriate type.
     */
    fun getShaderBuffer(surface: Surface, shader: IGlslShader): GlslShader.Buffer

    fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer

    /**
     * Obtain a gadget that can be used to receive input from a user. The gadget will be displayed in the show's UI.
     *
     * @param name Symbolic name for this gadget; must be unique within the show.
     * @param gadget The gadget to display.
     */
    fun <T : Gadget> getGadget(name: String, gadget: T): T
}