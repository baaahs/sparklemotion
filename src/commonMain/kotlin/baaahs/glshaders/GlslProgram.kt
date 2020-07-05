package baaahs.glshaders

import baaahs.Logger
import baaahs.RefCounted
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.OutputPortRef
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE
import kotlinx.serialization.modules.SerializersModule

class GlslProgram(
    internal val gl: GlslContext,
    private val openPatch: OpenPatch,
    resolver: Resolver
) {
    internal val id = gl.runInContext { gl.check { createProgram() ?: throw IllegalStateException() } }

    private val vertexShader = gl.runInContext {
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
    }

    private val fragShader = gl.runInContext {
        gl.createFragmentShader("#version ${gl.glslVersion}\n\n${openPatch.toGlsl()}\n")
    }

    private val bindings: List<Binding>

    init {
        gl.runInContext {
            gl.check { attachShader(id, vertexShader.id) }
            gl.check { attachShader(id, fragShader.id) }
            gl.check { linkProgram(id) }
            if (gl.check { getProgramParameter(id, GL_LINK_STATUS) } != GL_TRUE) {
                val infoLog = gl.check { getProgramInfoLog(id) }
                throw CompiledShader.CompilationException(infoLog ?: "huh?")
            }
        }

        bindings = gl.runInContext { bind(resolver) }
    }

    val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private fun bind(resolver: Resolver): List<Binding> {
        return openPatch.bind(this, resolver)
    }

    private inline fun <reified T> bindingsOf(): List<T> {
        return bindings
            .map { it.dataFeed }
            .filterIsInstance<T>()
    }

    val arrangementListeners: List<GlslRenderer.ArrangementListener>
        get() = bindingsOf()

    val resolutionListeners: List<ResolutionListener>
        get() = bindingsOf()

    fun setResolution(x: Float, y: Float) {
        resolutionListeners.forEach { it.onResolution(x, y) }
    }

    fun updateUniforms() {
        gl.runInContext {
            gl.useProgram(this)

            bindings.forEach { it.setUniform() }
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

    inner class Binding(
        private val dataSource: DataSource,
        val dataFeed: DataFeed,
        val id: String
    ) {
        private val uniformLocation = getUniform(dataSource.getVarName(id))

        val isValid: Boolean get() = uniformLocation != null

        fun setUniform() {
            try {
                uniformLocation?.let { dataFeed.set(it) }
            } catch (e: Exception) {
                logger.error(e) { "failed to set ${dataSource.getType()} $id from $dataFeed" }
            }
        }

        fun release() = dataFeed.release()
    }

    enum class DataTypes {
        float,
        vec2,
        vec3,
        vec4
    }

    interface DataFeed : RefCounted {
        fun set(uniform: Uniform)
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    companion object {
        private val logger = Logger("GlslProgram")

        val PixelColor = OutputPortRef("sm_pixelColor")
    }
}

typealias Resolver = (DataSource) -> DataBinding?

class DataBinding(val id: String, val dataFeed: GlslProgram.DataFeed)

val dataSourceProviderModule = SerializersModule {
    polymorphic(DataSource::class) {
//        CorePlugin.NoOp::class with CorePlugin.NoOp.serializer()
        CorePlugin.Resolution::class with CorePlugin.Resolution.serializer()
        CorePlugin.Time::class with CorePlugin.Time.serializer()
        CorePlugin.UvCoordTexture::class with CorePlugin.UvCoordTexture.serializer()
        CorePlugin.SliderDataSource::class with CorePlugin.SliderDataSource.serializer()
        CorePlugin.ColorPickerProvider::class with CorePlugin.ColorPickerProvider.serializer()
        CorePlugin.ColorPickerProvider::class with CorePlugin.ColorPickerProvider.serializer()
        CorePlugin.RadioButtonStripProvider::class with CorePlugin.RadioButtonStripProvider.serializer()
        CorePlugin.Scenes::class with CorePlugin.Scenes.serializer()
        CorePlugin.Patches::class with CorePlugin.Patches.serializer()
        CorePlugin.XyPad::class with CorePlugin.XyPad.serializer()
    }
}
