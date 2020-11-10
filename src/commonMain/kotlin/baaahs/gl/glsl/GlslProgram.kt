package baaahs.gl.glsl

import baaahs.gl.GlContext
import baaahs.gl.data.Feed
import baaahs.gl.patch.LinkedPatch
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.OutputPortRef
import baaahs.util.Logger
import com.danielgergely.kgl.Kgl

class GlslProgram(
    private val gl: GlContext,
    private val linkedPatch: LinkedPatch,
    feedResolver: FeedResolver
) {
    private val vertexShader =
        gl.createVertexShader(
            "#version ${gl.glslVersion}\n${GlslProgram.vertexShader}"
        )

    internal val fragShader =
        gl.createFragmentShader(linkedPatch.toFullGlsl(gl.glslVersion))

    val id = gl.compile(vertexShader, fragShader)

    private val bindings = gl.runInContext {
        linkedPatch.dataSourceLinks.mapNotNull { (dataSource, id) ->
            if (dataSource.isImplicit()) return@mapNotNull null
            val feed = feedResolver.openFeed(id, dataSource)

            if (feed != null) {
                val binding = feed.bind(this)
                if (binding.isValid) binding else {
                    logger.debug { "unused uniform for $dataSource?" }
                    binding.release()
                    null
                }
            } else {
                logger.warn { "no UniformProvider bound for $dataSource" }
                null
            }
        }
    }

    val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private inline fun <reified T> bindingsOf(): List<T> {
        return bindings
            .mapNotNull { it.feed }
            .filterIsInstance<T>()
    }

    val resolutionListeners: List<ResolutionListener>
        get() = bindingsOf()

    fun setResolution(x: Float, y: Float) {
        resolutionListeners.forEach { it.onResolution(x, y) }
    }

    fun updateUniforms() {
        gl.runInContext {
            gl.useProgram(this)

            bindings.forEach { it.setOnProgram() }
        }
    }

    fun release() {
        bindings.forEach { it.release() }
//        TODO gl.runInContext { gl.check { deleteProgram } }
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
