package baaahs.gl.glsl

import baaahs.Logger
import baaahs.RefCounted
import baaahs.gl.GlContext
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.ModelRenderer
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.OutputPortRef
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE

class GlslProgram(
    internal val gl: GlContext,
    private val linkedPatch: LinkedPatch,
    resolver: Resolver
) {
    internal val id = gl.runInContext { gl.check { createProgram() ?: throw IllegalStateException() } }

    private val vertexShader =
        gl.createVertexShader(
            """
            #version ${gl.glslVersion}
                
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
        )

    internal val fragShader =
        gl.createFragmentShader(linkedPatch.toFullGlsl(gl.glslVersion))

    private val bindings: List<Binding>

    init {
        gl.runInContext {
            gl.check { attachShader(id, vertexShader.shaderId) }
            gl.check { attachShader(id, fragShader.shaderId) }
            gl.check { linkProgram(id) }
            if (gl.check { getProgramParameter(id, GL_LINK_STATUS) } != GL_TRUE) {
                vertexShader.validate()
                fragShader.validate()

                val infoLog = gl.check { getProgramInfoLog(id) }
                throw CompilationException(infoLog ?: "Huh? Program error?")
            }
        }

        bindings = gl.runInContext { bind(resolver) }
    }

    val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private fun bind(resolver: Resolver): List<Binding> {
        return linkedPatch.bind(this, resolver)
    }

    private inline fun <reified T> bindingsOf(): List<T> {
        return bindings
            .map { it.dataFeed }
            .filterIsInstance<T>()
    }

    val arrangementListeners: List<ModelRenderer.ArrangementListener>
        get() = bindingsOf()

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

    interface Binding {
        val dataFeed: DataFeed
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

    companion object {
        private val logger = Logger("GlslProgram")

        val PixelColor = OutputPortRef("sm_result")
    }
}

typealias Resolver = (String, DataSource) -> GlslProgram.DataFeed?
