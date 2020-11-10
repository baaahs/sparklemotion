package baaahs.gl.glsl

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.GlContext
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
            val dataFeed = feedResolver.openFeed(id, dataSource)

            if (dataFeed != null) {
                val binding = dataFeed.bind(this)
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
            .mapNotNull { it.dataFeed }
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

    interface Binding {
        val dataFeed: DataFeed?
        val isValid: Boolean

        fun setOnProgram()

        /**
         * Only release any resources specifically allocated by this Binding, not by
         * its parent [DataFeed].
         */
        fun release() {}
    }

    class SingleUniformBinding(
        glslProgram: GlslProgram,
        dataSource: DataSource,
        val id: String,
        override val dataFeed: DataFeed,
        val setUniform: (Uniform) -> Unit
    ) : Binding {
        private val type: Any = dataSource.getType()
        private val varName = dataSource.getVarName(id)
        private val uniformLocation = glslProgram.getUniform(varName)

        override val isValid: Boolean get() = uniformLocation != null

        override fun setOnProgram() {
            try {
                uniformLocation?.let { setUniform(it) }
            } catch (e: Exception) {
                logger.error(e) { "failed to set uniform $type $varName for $id" }
            }
        }
    }

    interface DataFeed : RefCounted {
        fun bind(glslProgram: GlslProgram): Binding
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    class NoOpDataFeed : DataFeed, RefCounted by RefCounter() {
        override fun bind(glslProgram: GlslProgram): Binding {
            return object : Binding {
                override val dataFeed: DataFeed
                    get() = this@NoOpDataFeed
                override val isValid: Boolean
                    get() = true

                override fun setOnProgram() {
                    // No-op.
                }
            }
        }
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
    fun openFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed?
}
