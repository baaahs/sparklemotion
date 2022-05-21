package baaahs.gl.glsl

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.glsl.UniformImpl
import baaahs.show.DataSource
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

    private val feeds = gl.runInContext {
        linkedProgram.dataSourceLinks.mapNotNull { (dataSource, id) ->
            val engineFeed = engineFeedResolver.openFeed(id, dataSource)

            if (engineFeed != null) {
                val programFeed = engineFeed.bind(this)
                if (programFeed.isValid) {
                    programFeed
                } else {
                    logger.debug { "Invalid feed for $dataSource $id: $programFeed" }
                    programFeed.release()
                    null
                }
            } else {
                logger.warn { "No feed bound for $dataSource $id." }
                null
            }
        }
    }

    private val vertexShader_resolution by lazy { getUniform("vertexShader_resolution") }

    init {
        gl.runInContext {
            feeds.forEach { programFeed ->
                if (programFeed.updateMode == UpdateMode.ONCE)
                    programFeed.setOnProgram()
            }
        }
    }

    private val perFrameFeeds = feeds.mapNotNull { programFeed ->
        if (programFeed.updateMode == UpdateMode.PER_FRAME)
            programFeed
        else null
    }

    private val perFixtureFeeds = feeds.mapNotNull { programFeed ->
        if (programFeed.updateMode == UpdateMode.PER_FIXTURE)
            programFeed
        else null
    }

    override val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private inline fun <reified T> feedsOf(): List<T> = feeds.filterIsInstance<T>()

    override fun setResolution(x: Float, y: Float) {
        feedsOf<GlslProgram.ResolutionListener>().forEach { it.onResolution(x, y) }
    }

    override fun setPixDimens(width: Int, height: Int) {
        vertexShader_resolution!!.set(width.toFloat(), height.toFloat())
    }

    override fun aboutToRenderFrame() {
        perFrameFeeds.forEach { it.setOnProgram() }
    }

    override fun aboutToRenderFixture(renderTarget: RenderTarget) {
        perFixtureFeeds.forEach {
            it.setOnProgram(renderTarget)
        }
    }

    override fun release() {
        feeds.forEach { it.release() }

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
    }
}

fun interface FeedResolver {
    fun openFeed(id: String, dataSource: DataSource): Feed?
}

fun interface EngineFeedResolver {
    fun openFeed(id: String, dataSource: DataSource): EngineFeed?
}
