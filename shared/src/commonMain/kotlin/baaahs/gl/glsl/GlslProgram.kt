package baaahs.gl.glsl

import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.render.RenderEngine
import baaahs.gl.render.RenderTarget
import baaahs.glsl.*
import baaahs.show.Feed
import baaahs.show.UpdateMode
import baaahs.util.Logger
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.Program

/**
 * A [GlslProgram] that is likely still being compiled by OpenGL.
 *
 * See https://registry.khronos.org/webgl/extensions/KHR_parallel_shader_compile/
 */
class GlslCompilingProgram(
    private val linkedProgram: LinkedProgram,
    private val vertexShader: CompiledShader,
    private val fragShader: CompiledShader,
    private val program: Program,
    private val renderEngine: RenderEngine,
    private val feedResolver: FeedResolver
) {
    fun bind(): GlslProgramImpl =
        GlslProgramImpl(renderEngine.gl, linkedProgram, vertexShader, fragShader, program) { id: String, feed: Feed ->
            val feed = feedResolver.openFeed(id, feed)
            feed?.let { renderEngine.cachedEngineFeed(it) }
        }.also { it.validate() }

    fun isReady(): Boolean {
        val gl = renderEngine.gl
        return if (gl.checkForParallelShaderCompile()) {
            gl.runInContext { gl.check { getProgramParameter(program, GlContext.GL_COMPLETION_STATUS_KHR) } == GL_TRUE }
        } else true
    }
}

interface GlslProgram {
    val title: String
    val fragShader: CompiledShader
    val vertexAttribLocation: Int

    fun setResolution(x: Float, y: Float)
    fun setPixDimens(width: Int, height: Int)
    fun aboutToRenderFrame()
    fun aboutToRenderFixture(renderTarget: RenderTarget)

    fun getUniform(name: String): GlslUniform?

    fun getIntUniform(name: String): IntUniform? = getUniform(name)?.let { IntUniform(it) }
    fun getInt2Uniform(name: String): Int2Uniform? = getUniform(name)?.let { Int2Uniform(it) }
    fun getInt3Uniform(name: String): Int3Uniform? = getUniform(name)?.let { Int3Uniform(it) }
    fun getInt4Uniform(name: String): Int4Uniform? = getUniform(name)?.let { Int4Uniform(it) }

    fun getFloatUniform(name: String): FloatUniform? = getUniform(name)?.let { FloatUniform(it) }
    fun getFloat2Uniform(name: String): Float2Uniform? = getUniform(name)?.let { Float2Uniform(it) }
    fun getFloat3Uniform(name: String): Float3Uniform? = getUniform(name)?.let { Float3Uniform(it) }
    fun getFloat4Uniform(name: String): Float4Uniform? = getUniform(name)?.let { Float4Uniform(it) }

    fun getMatrix4FUniform(name: String): Matrix4Uniform? = getUniform(name)?.let { Matrix4Uniform(it) }
    fun getEulerAngleUniform(name: String): EulerAngleUniform? = getUniform(name)?.let { EulerAngleUniform(it) }
    fun getTextureUniform(name: String): TextureUniform?

    fun <T> withProgram(fn: Kgl.() -> T): T
    fun validate()
    fun use()
    fun release()

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    companion object {
        fun create(
            gl: GlContext,
            linkedProgram: LinkedProgram,
            engineFeedResolver: EngineFeedResolver
        ): GlslProgram {
            val vertexShader = vertexShader(gl)
            val fragShader = gl.createFragmentShader(linkedProgram.toFullGlsl(gl.glslVersion))
            val program = gl.compile(vertexShader, fragShader)
            return GlslProgramImpl(gl, linkedProgram, vertexShader(gl), fragShader, program, engineFeedResolver)
        }

        fun vertexShader(gl: GlContext): CompiledShader =
            gl.createVertexShader("#version ${gl.glslVersion}\n${GlslProgramImpl.vertexShader}")
    }
}

class GlslProgramImpl(
    private val gl: GlContext,
    val linkedProgram: LinkedProgram,
    private val vertexShader: CompiledShader,
    override val fragShader: CompiledShader,
    val id: Program,
    engineFeedResolver: EngineFeedResolver
): GlslProgram {
    override val title: String get() = linkedProgram.rootNode.title

    private val textureUniforms = mutableMapOf<String, TextureUniform?>()

    internal val openFeeds = gl.runInContext {
        linkedProgram.feedLinks.mapNotNull { (feed, id) ->
            val engineFeed = engineFeedResolver.openFeed(id, feed)

            if (engineFeed != null) {
                val spy = if (enableUniformSpying) GlslProgramSpy(this) else null
                val programFeed = engineFeed.bind(spy ?: this)
                if (programFeed.isValid) {
                    OpenFeed(feed, id, programFeed, spy)
                } else {
                    logger.debug { "Invalid feed for $feed $id: $programFeed" }
                    programFeed.release()
                    null
                }
            } else {
                logger.warn { "No feed bound for $feed $id." }
                null
            }
        }
    }

    class GlslProgramSpy(val delegate: GlslProgram) : GlslProgram by delegate {
        val uniforms = mutableMapOf<String, UniformSpy?>()
        override fun getUniform(name: String): GlslUniform? =
            delegate.getUniform(name)?.let { UniformSpy(name, it) }
                .also { uniforms[name] = it }
    }

    class UniformSpy(override val name: String, val delegate: GlslUniform) : GlslUniform {
        var value: Any? = null

        override fun set(x: Int) { value = x; delegate.set(x) }
        override fun set(x: Int, y: Int) { value = listOf(x, y); delegate.set(x, y) }
        override fun set(x: Int, y: Int, z: Int) { value = listOf(x, y, z); delegate.set(x, y, z) }
        override fun set(x: Int, y: Int, z: Int, w: Int) { value = listOf(x, y, z, w); delegate.set(x, y, z, w) }
        override fun set(x: Float) { value = x; delegate.set(x) }
        override fun set(x: Float, y: Float) { value = Vector2F(x, y); delegate.set(x, y) }
        override fun set(x: Float, y: Float, z: Float) { value = Vector3F(x, y, z); delegate.set(x, y, z) }
        override fun set(x: Float, y: Float, z: Float, w: Float) { value = Vector4F(x, y, z, w); delegate.set(x, y, z, w) }
        override fun set(matrix: Matrix4F) { value = matrix; delegate.set(matrix) }
        override fun set(eulerAngle: EulerAngle) { value; delegate.set(eulerAngle) }
    }

    class OpenFeed(
        val feed: Feed,
        val id: String,
        val programFeedContext: ProgramFeedContext,
        val glslProgramSpy: GlslProgramSpy?
    ) {
        val updateMode get() = programFeedContext.updateMode

        fun release() = programFeedContext.release()
    }

    private val vertexShader_resolution by lazy { getFloat2Uniform("vertexShader_resolution") }

    init {
        gl.runInContext {
            openFeeds.forEach { feed ->
                if (feed.updateMode == UpdateMode.ONCE)
                    feed.programFeedContext.setOnProgram()
            }
        }
    }

    private val perFrameFeeds = openFeeds.filter { it.updateMode == UpdateMode.PER_FRAME }

    private val perFixtureFeeds = openFeeds.filter { it.updateMode == UpdateMode.PER_FIXTURE }

    override val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private inline fun <reified T> feedsOf(): List<T> =
        openFeeds.map { it.programFeedContext }.filterIsInstance<T>()

    override fun setResolution(x: Float, y: Float) {
        feedsOf<GlslProgram.ResolutionListener>().forEach { it.onResolution(x, y) }
    }

    override fun setPixDimens(width: Int, height: Int) {
        vertexShader_resolution!!.set(width.toFloat(), height.toFloat())
    }

    override fun aboutToRenderFrame() {
        perFrameFeeds.forEach {
            try {
                it.programFeedContext.setOnProgram()
            } catch (e: Exception) {
                logger.warn(e) { "Error in ${it.feed.title}'s setOnProgram." }
            }
        }

        // Update global texture unit bindings.
        textureUniforms.values.filterNotNull().onEachIndexed { index, textureUniform ->
            textureUniform.bindTextureUnitForRender(index)
        }
    }

    override fun aboutToRenderFixture(renderTarget: RenderTarget) {
        perFixtureFeeds.forEach {
            it.programFeedContext.setOnProgram(renderTarget)
        }
    }

    override fun release() {
        openFeeds.forEach { it.release() }

        gl.runInContext {
            gl.useProgram(this)
//            TODO: gl.check { detachShader(vertexShader) }
            vertexShader.release()
//            TODO: gl.check { detachShader(fragShader) }
            fragShader.release()

        }
//        TODO gl.runInContext { gl.check { deleteProgram(id) } }
    }

    override fun getUniform(name: String): GlslUniform? = gl.runInContext {
        gl.useProgram(this)
        val uniformLoc = gl.check { getUniformLocation(id, name) }
        if (uniformLoc == null) {
            logger.debug { "no such uniform $name" }
        }
        uniformLoc?.let { UniformImpl(this@GlslProgramImpl, name, it) }
    }

    override fun getTextureUniform(name: String): TextureUniform? =
        textureUniforms.getOrPut(name) { getUniform(name)?.let { TextureUniform(it, gl) } }

    override fun <T> withProgram(fn: Kgl.() -> T): T {
        gl.useProgram(this)
        return gl.check(fn)
    }

    override fun validate() = gl.runInContext {
        if (gl.check { getProgramParameter(id, GL_LINK_STATUS) } != GL_TRUE) {
            vertexShader.validate()
            fragShader.validate()

            val infoLog = gl.check { getProgramInfoLog(id) }
            throw CompilationException(infoLog ?: "Huh? Program error?")
        }
    }

    override fun use() {
        gl.check { useProgram(id) }
    }

    companion object {
        private val logger = Logger("GlslProgram")

        /**language=glsl*/
        val vertexShader = """
            precision highp float;

            // xy = vertex position in device pixel coordinates.
            in vec2 Vertex;
            
            uniform vec2 vertexShader_resolution;

            void main()
            {
                // scale vertex attribute to [-1,1] range
                gl_Position = vec4(Vertex / vertexShader_resolution * 2. - 1., 0.0, 1.0);
            }
        """.trimIndent()

        private val enableUniformSpying = false
    }
}

fun interface FeedResolver {
    fun openFeed(id: String, feed: Feed): FeedContext?
}

fun interface EngineFeedResolver {
    fun openFeed(id: String, feed: Feed): EngineFeedContext?
}
