package baaahs.gl.data

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.param.ParamBuffer
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.show.UpdateMode
import baaahs.util.RefCounted

interface FeedContext : RefCounted {
    fun bind(gl: GlContext): EngineFeed
    fun release() = Unit
}

/**
 * Context for a feed with respect to a [GlContext].
 *
 * This is a good spot for allocating any resources that can be shared across instances
 * of [GlslProgram] within the context.
 */
interface EngineFeed {
    fun bind(glslProgram: GlslProgram): ProgramFeed

    fun aboutToRenderFrame(renderTargets: List<RenderTarget>) = Unit

    fun release() = Unit
}

/**
 * Context for a feed with respect to a [GlslProgram].
 *
 * This is the spot for any resources that are specific to this program instance,
 * like uniform locations.
 */
interface ProgramFeed {
    val isValid: Boolean get() = true
    val updateMode: UpdateMode get() = UpdateMode.PER_FRAME

    val callSetEarly: Boolean get() = updateMode == UpdateMode.ONCE
    val callSetBeforeFrame: Boolean get() = updateMode == UpdateMode.PER_FRAME
    val callSetBeforeFixture: Boolean get() = updateMode == UpdateMode.PER_FIXTURE

    fun setOnProgram() = Unit

    fun setOnProgram(renderTarget: RenderTarget) = Unit

    /**
     * Only release any resources specifically allocated by this Binding, not by
     * its parent [FeedContext].
     */
    fun release() {}
}

interface PerPixelEngineFeed : EngineFeed {
    val updateMode: UpdateMode get() = UpdateMode.ONCE
    val buffer: ParamBuffer

    override fun aboutToRenderFrame(renderTargets: List<RenderTarget>) {
        if (updateMode == UpdateMode.PER_FRAME || updateMode == UpdateMode.PER_FIXTURE) {
            renderTargets.forEach { setOnBuffer(it) }
            buffer.uploadToTexture()
        }
    }

    fun resize(width: Int, height: Int, beforeUploading: () -> Unit) {
        buffer.resizeBuffer(width, height)
        beforeUploading()
        buffer.uploadToTexture()
    }

    fun setOnBuffer(renderTarget: RenderTarget)

    override fun bind(glslProgram: GlslProgram): PerPixelProgramFeed

    override fun release() {
        buffer.release()
    }
}

abstract class PerPixelProgramFeed(
    override val updateMode: UpdateMode
) : ProgramFeed {
    abstract val buffer: ParamBuffer
    abstract val uniform: Uniform

    override val callSetEarly: Boolean get() = true
    override val callSetBeforeFrame: Boolean get() =
        updateMode == UpdateMode.PER_FRAME || updateMode == UpdateMode.PER_FIXTURE
    override val callSetBeforeFixture: Boolean get() = false

    final override fun setOnProgram() {
        buffer.setTexture(uniform)
    }

    final override fun setOnProgram(renderTarget: RenderTarget) {}
}