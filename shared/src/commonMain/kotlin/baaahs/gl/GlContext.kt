package baaahs.gl

import baaahs.gl.glsl.CompiledShader
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.ResourceAllocationException
import baaahs.ui.Observable
import baaahs.util.Logger
import baaahs.util.globalLaunch
import com.danielgergely.kgl.*
import kotlin.reflect.KClass

abstract class GlContext(
    val name: String,
    internal val kgl: Kgl,
    val glslVersion: String,
    var checkForErrors: Boolean = false,
    val state: State = State()
) {
    val id = nextId++
    protected open val allocatedContext: AllocatedContext
        get() = AllocatedContext(id, name, this::class)

    init {
        logger.debug { "Created GlContext \"$name\"." }
        globalLaunch {
            allocatedContexts.add(allocatedContext)
        }
    }

    abstract fun <T> runInContext(fn: () -> T): T
    abstract suspend fun <T> asyncRunInContext(fn: suspend () -> T): T

    abstract fun getGlInt(parameter: Int): Int

    val maxTextureUnit by lazy { getGlInt(GL_MAX_TEXTURE_IMAGE_UNITS) }
    val maxTextureSize by lazy { getGlInt(GL_MAX_TEXTURE_SIZE) }

    /**
     * When a GlContext shares a rendering canvas with others, `rasterOffet` indicates this
     * context's location in the shared canvas.
     */
    open val rasterOffset: RasterOffset = RasterOffset(0, 0)

    class Stats {
        var activeTexture = 0
        var bindTexture = 0
        var texImage2D = 0
    }
    val stats = Stats()

    class State {
        var viewport: List<Int> = emptyList()

        var activeProgram: GlslProgram? = null
        var activeFrameBuffer: FrameBuffer? = null
        var activeRenderBuffer: RenderBuffer? = null
    }

    fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        val newViewport = listOf(x, y, width, height)
        if (newViewport != state.viewport) {
            check { viewport(x, y, width, height) }
            state.viewport = newViewport
        }
    }

    fun createVertexShader(source: String): CompiledShader {
        return CompiledShader(this, GL_VERTEX_SHADER, source)
    }

    fun createFragmentShader(source: String): CompiledShader {
        return CompiledShader(this, GL_FRAGMENT_SHADER, source)
    }

    /** Starts building a program, but doesn't do any potentially blocking validation. */
    fun compile(vertexShader: CompiledShader, fragShader: CompiledShader): Program = runInContext {
        val program = noCheck {
            createProgram()
                ?: throw ResourceAllocationException("Failed to allocate a GL program.")
        }
        noCheck { attachShader(program, vertexShader.shaderId) }
        noCheck { attachShader(program, fragShader.shaderId) }
        noCheck { linkProgram(program) }
        program
    }

    fun useProgram(glslProgram: GlslProgram) {
        if (state.activeProgram !== glslProgram) {
            glslProgram.use()
            state.activeProgram = glslProgram
        }
    }

    fun createFrameBuffer(): FrameBuffer {
        return FrameBuffer(check { createFramebuffer() })
    }

    fun createRenderBuffer(): RenderBuffer {
        return RenderBuffer(check { createRenderbuffer() })
    }

    inner class FrameBuffer(private val framebuffer: Framebuffer) {
        private val curRenderBuffers = mutableMapOf<Int, RenderBuffer>()

        fun bind() {
            if (state.activeFrameBuffer != this) {
                check { bindFramebuffer(GL_FRAMEBUFFER, framebuffer) }
                state.activeFrameBuffer = this
            }
        }

        fun attach(renderBuffer: RenderBuffer, attachment: Int) {
            bind()

            if (curRenderBuffers[attachment] != renderBuffer) {
                check { framebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, renderBuffer.renderbuffer) }
                curRenderBuffers[attachment] = renderBuffer
            }
        }

        fun check() {
            if (checkForErrors) {
                bind()

                val status = check { checkFramebufferStatus(GL_FRAMEBUFFER) }
                if (status != GL_FRAMEBUFFER_COMPLETE) {
                    logger.warn { "$name: FrameBuffer huh? ${decodeGlConst(status) ?: status}" }
                }
            }
        }

        fun release() {
            if (state.activeFrameBuffer == this) {
                check { bindFramebuffer(GL_FRAMEBUFFER, null) }
            }

            check { deleteFramebuffer(framebuffer) }
            curRenderBuffers.values.forEach { it.release() }
        }

        // This attempts to work around readBuffer() not existing in Kgl (or on Android).
        // Temporarily attach the renderbuffer as the primary color buffer.
        fun <T> withRenderBufferAsAttachment0(renderBuffer: RenderBuffer, fn: () -> T): T {
            bind()

            val attachment = GL_COLOR_ATTACHMENT0
            val priorAttachment0 = curRenderBuffers[attachment]
            return if (priorAttachment0 != null && priorAttachment0 != renderBuffer) {
                check { framebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, renderBuffer.renderbuffer) }
                curRenderBuffers[attachment] = renderBuffer

                try {
                    fn()
                } finally {
                    check { framebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, priorAttachment0.renderbuffer) }
                    curRenderBuffers[attachment] = priorAttachment0
                }
            } else {
                fn()
            }
        }
    }

    inner class RenderBuffer(internal val renderbuffer: Renderbuffer) {
        var curInternalFormat = -1
        var curWidth = -1
        var curHeight = -1

        fun bind() {
            if (state.activeRenderBuffer != this) {
                check { bindRenderbuffer(GL_RENDERBUFFER, renderbuffer) }
                state.activeRenderBuffer = this
            }
        }

        fun storage(internalformat: Int, width: Int, height: Int) {
            bind()

            if (internalformat != curInternalFormat
                || width != curWidth
                || height != curHeight
            ) {
                check { bindRenderbuffer(GL_RENDERBUFFER, renderbuffer) }
                state.activeRenderBuffer = this
                check { renderbufferStorage(GL_RENDERBUFFER, internalformat, width, height) }

                curInternalFormat = internalformat
                curWidth = width
                curHeight = height
            }
        }

        fun readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, buffer: Buffer) {
            check { readPixels(x, y, width, height, format, type, buffer) }
        }

        fun release() {
            check { deleteRenderbuffer(renderbuffer) }
        }
    }

    fun bindActiveTexture(
        textureUnitNumber: Int = 0,
        target: Int = GL_TEXTURE_2D,
        texture: Texture?
    ) {
        check { activeTexture(GL_TEXTURE0 + textureUnitNumber) }
        check { bindTexture(target, texture) }
    }

    fun Texture.configure(minFilter: Int = GL_LINEAR, maxFilter: Int = GL_LINEAR) {
        bindActiveTexture(texture = this)

        check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter) }
        check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, maxFilter) }
        check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE) }
        check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE) }
    }

    fun Texture.upload(level: Int, internalFormat: Int, border: Int, resource: TextureResource) {
        bindActiveTexture(texture = this)

        stats.texImage2D++
        check { texImage2D(GL_TEXTURE_2D, level, internalFormat, border, resource) }
    }

    fun Texture.upload(
        level: Int,
        internalFormat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        buffer: Buffer
    ) {
        bindActiveTexture(texture = this)

        stats.texImage2D++
        check { texImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, border, format, type, buffer) }
    }

    open fun checkIfResultBufferCanContainFloats(required: Boolean = false): Boolean = true

    open fun checkIfResultBufferCanContainHalfFloats(required: Boolean = false): Boolean = true

    open fun checkForLinearFilteringOfFloatTextures(required: Boolean = false): Boolean = true

    open fun checkForParallelShaderCompile(required: Boolean = false): Boolean = true

    fun <T> noCheck(fn: Kgl.() -> T): T {
        return kgl.fn()
    }

    fun <T> check(fn: Kgl.() -> T): T {
        val result = kgl.fn()

        if (checkForErrors) checkForGlError()

        return result
    }

    private fun checkForGlError() {
        val error = kgl.getError()

        val code = decodeGlConst(error) ?: "unknown error $error"

        if (error != 0) {
            logger.error { "$name: OpenGL Error: $code" }
            throw RuntimeException("OpenGL Error: $code")
        }
    }

    private fun decodeGlConst(error: Int) =
        when (error) {
            GL_INVALID_ENUM -> "GL_INVALID_ENUM"
            GL_INVALID_VALUE -> "GL_INVALID_VALUE"
            GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
            GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
            GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
            //            GL_CONTEXT_LOST_WEBGL -> "GL_CONTEXT_LOST_WEBGL"
            GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
            else -> null
        }

    open fun release() {
        if (kgl is ReleasableKgl) {
            kgl.release()
        }

//        TODO("not implemented")
    }

    override fun toString() = "${this::class.simpleName}#$id"

    interface ReleasableKgl {
        fun release()
    }

    data class RasterOffset(val bottom: Int, val left: Int)

    class AllocatedContexts : Observable(), MutableList<AllocatedContext> by mutableListOf()

    data class AllocatedContext(
        val id: Int,
        val name: String,
        val type: KClass<out GlContext>,
        val parentId: Int? = null
    )

    companion object {
        val allocatedContexts = AllocatedContexts()

        private val logger = Logger<GlContext>()
        private var nextId = 0

        const val GL_RGBA8 = 0x8058

        const val GL_R32F = 0x822E
        const val GL_RG32F = 0x8230
        const val GL_RGB32F = 0x8815
        const val GL_RGBA32F = 0x8814

        const val GL_R16F = 0x822D
        const val GL_RG16F = 0x822F
        const val GL_RGB16F  = 0x881B
        const val GL_RGBA16F = 0x881A

        const val GL_COMPLETION_STATUS_KHR = 0x91B1
    }
}