package baaahs.gl.glsl

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.OutputPortRef
import baaahs.show.UpdateMode
import baaahs.util.Logger
import com.danielgergely.kgl.Kgl

class GlslProgram(
    private val gl: GlContext,
    private val linkedPatch: LinkedPatch,
    engineFeedResolver: EngineFeedResolver
) {
    val title: String get() = linkedPatch.rootNode.title

    private val vertexShader =
        gl.createVertexShader(
            "#version ${gl.glslVersion}\n${GlslProgram.vertexShader}"
        )

    internal val fragShader =
        gl.createFragmentShader(linkedPatch.toFullGlsl(gl.glslVersion))

    val id = gl.compile(vertexShader, fragShader)

    private val feeds = gl.runInContext {
        linkedPatch.dataSourceLinks.mapNotNull { (dataSource, id) ->
            if (dataSource.isImplicit()) return@mapNotNull null
            val engineFeed = engineFeedResolver.openFeed(id, dataSource)

            if (engineFeed != null) {
                val programFeed = engineFeed.bind(this)
                if (programFeed.isValid) {
                    programFeed
                } else {
                    logger.debug { "unused uniform for $dataSource?" }
                    programFeed.release()
                    null
                }
            } else {
                logger.warn { "no UniformProvider bound for $dataSource" }
                null
            }
        }
    }

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

    val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private inline fun <reified T> feedsOf(): List<T> = feeds.filterIsInstance<T>()

    fun setResolution(x: Float, y: Float) {
        feedsOf<ResolutionListener>().forEach { it.onResolution(x, y) }
    }

    fun aboutToRenderFrame() {
        perFrameFeeds.forEach { it.setOnProgram() }
    }

    fun aboutToRenderFixture(renderTarget: RenderTarget) {
        perFixtureFeeds.forEach {
            it.setOnProgram(renderTarget)
        }
    }

    fun release() {
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

    fun getUniform(name: String): Uniform? = gl.runInContext {
        gl.useProgram(this)
        val uniformLoc = gl.check { getUniformLocation(id, name) }
        if (uniformLoc == null) {
            logger.debug { "no such uniform $name" }
        }
        uniformLoc?.let { Uniform(this@GlslProgram, it) }
    }

    fun <T> withProgram(fn: Kgl.() -> T): T {
        gl.useProgram(this)
        return gl.check(fn)
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    companion object {
        private val logger = Logger("GlslProgram")

        val PixelColor = OutputPortRef("sm_result")

        val vertexShader = """
            precision lowp float;

            // xy = vertex position in normalized device coordinates ([-1,+1] range).
            in vec2 Vertex;

            const vec2 scale = vec2(0.5, 0.5);

            void main()
            {
                vec2 vTexCoords  = Vertex * scale + scale; // scale vertex attribute to [0,1] range
                gl_Position = vec4(Vertex, 0.0, 1.0);
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
