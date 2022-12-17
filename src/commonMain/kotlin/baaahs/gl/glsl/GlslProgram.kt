package baaahs.gl.glsl

import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.glsl.UniformImpl
import baaahs.show.Feed
import baaahs.show.UpdateMode
import baaahs.util.Logger
import com.danielgergely.kgl.Kgl

interface GlslProgram {
    val title: String
    val fragShader: CompiledShader
    val vertexAttribLocation: Int

    fun setResolution(x: Float, y: Float)
    fun setPixDimens(width: Int, height: Int)
    fun aboutToRenderFrame()
    fun aboutToRenderFixture(renderTarget: RenderTarget)
    fun getUniform(name: String): Uniform?
    fun <T> withProgram(fn: Kgl.() -> T): T
    fun use()
    fun release()

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }
}

class GlslProgramImpl(
    private val gl: GlContext,
    val linkedProgram: LinkedProgram,
    engineFeedResolver: EngineFeedResolver
): GlslProgram {
    override val title: String get() = linkedProgram.rootNode.title

    private val vertexShader =
        gl.createVertexShader(
            "#version ${gl.glslVersion}\n${GlslProgramImpl.vertexShader}"
        )

    override val fragShader =
        gl.createFragmentShader(linkedProgram.toFullGlsl(gl.glslVersion))

    val id = gl.compile(vertexShader, fragShader)

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
        override fun getUniform(name: String): Uniform? =
            delegate.getUniform(name)?.let { UniformSpy(name, it) }
                .also { uniforms.put(name, it) }
    }

    class UniformSpy(val name: String, val delegate: Uniform) : Uniform {
        var value: Any? = null

        override fun set(x: Int) { value = x; delegate.set(x) }
        override fun set(x: Int, y: Int) { value = listOf(x, y); delegate.set(x, y) }
        override fun set(x: Int, y: Int, z: Int) { value = listOf(x, y, z); delegate.set(x, y, z) }
        override fun set(x: Float) { value = x; delegate.set(x) }
        override fun set(x: Float, y: Float) { value = Vector2F(x, y); delegate.set(x, y) }
        override fun set(x: Float, y: Float, z: Float) { value = Vector3F(x, y, z); delegate.set(x, y, z) }
        override fun set(x: Float, y: Float, z: Float, w: Float) { value = Vector4F(x, y, z, w); delegate.set(x, y, z, w) }
        override fun set(matrix: Matrix4F) { value = matrix; delegate.set(matrix) }
        override fun set(vector2F: Vector2F) { value = vector2F; delegate.set(vector2F) }
        override fun set(vector3F: Vector3F) { value = vector3F; delegate.set(vector3F) }
        override fun set(vector4F: Vector4F) { value = vector4F; delegate.set(vector4F) }
        override fun set(eulerAngle: EulerAngle) { value; delegate.set(eulerAngle) }
        override fun set(textureUnit: GlContext.TextureUnit) { value = textureUnit; delegate.set(textureUnit) }
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

    private val vertexShader_resolution by lazy { getUniform("vertexShader_resolution") }

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
        perFrameFeeds.forEach { it.programFeedContext.setOnProgram() }
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

    override fun getUniform(name: String): Uniform? = gl.runInContext {
        gl.useProgram(this)
        val uniformLoc = gl.check { getUniformLocation(id, name) }
        if (uniformLoc == null) {
            logger.debug { "no such uniform $name" }
        }
        uniformLoc?.let { UniformImpl(this@GlslProgramImpl, it) }
    }

    override fun <T> withProgram(fn: Kgl.() -> T): T {
        gl.useProgram(this)
        return gl.check(fn)
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
