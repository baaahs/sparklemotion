package baaahs.glshaders

import baaahs.Logger
import baaahs.glshaders.GlslCode.ContentType
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import baaahs.ports.InputPortRef
import baaahs.ports.OutputPortRef
import baaahs.ports.inputPortRef
import baaahs.show.DataSource
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE
import kotlinx.serialization.modules.SerializersModule

class GlslProgram(
    internal val gl: GlslContext,
    private val patch: Patch,
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
        gl.createFragmentShader("#version ${gl.glslVersion}\n\n${patch.toGlsl()}\n")
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
        return patch.inputPortRefs.mapNotNull { uniformPortRef ->
            if (uniformPortRef.isImplicit) return@mapNotNull null

            val dataSource = resolver.invoke(uniformPortRef)
            if (dataSource != null) {
                val binding = Binding(uniformPortRef, dataSource)
                if (binding.isValid) binding else {
                    logger.debug { "unused uniform for $uniformPortRef?" }
                    binding.release()
                    null
                }
            } else {
                logger.warn { "no UniformProvider bound for $uniformPortRef" }
                null
            }
        }
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
        private val inputPortRef: InputPortRef,
        val dataFeed: DataFeed
    ) {
        private val uniformLocation = getUniform(inputPortRef.varName)

        val isValid: Boolean get() = uniformLocation != null

        fun setUniform() {
            try {
                uniformLocation?.let { dataFeed.set(it) }
            } catch (e: Exception) {
                logger.error(e) { "failed to set ${inputPortRef.title} from $dataFeed" }
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

    interface DataFeed {
        fun set(uniform: Uniform)
        fun release() {}
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    companion object {
        private val logger = Logger("GlslProgram")

        val Resolution = inputPortRef(
            "resolution",
            "vec2",
            "resolution",
            ContentType.Resolution.pluginId
        )

        val Time = inputPortRef(
            "time",
            "float",
            "time",
            ContentType.Time.pluginId
        )

        val GlFragCoord = inputPortRef(
            "glFragCoord",
            "vec4",
            "gl_FragCoord",
            "baaahs.Core:none",
            varName = "gl_FragCoord",
            isImplicit = true
        )

        val UvCoordsTexture = inputPortRef(UvShader.uvCoordsTextureInputPort)

        val PixelColor = OutputPortRef("vec4", "gl_FragColor")
    }
}

typealias Resolver = (InputPortRef) -> GlslProgram.DataFeed?

val dataSourceProviderModule = SerializersModule {
    polymorphic(DataSource::class) {
        CorePlugin.NoOp::class with CorePlugin.NoOp.serializer()
        CorePlugin.Resolution::class with CorePlugin.Resolution.serializer()
        CorePlugin.Time::class with CorePlugin.Time.serializer()
        CorePlugin.UvCoord::class with CorePlugin.UvCoord.serializer()
        CorePlugin.SliderProvider::class with CorePlugin.SliderProvider.serializer()
        CorePlugin.ColorPickerProvider::class with CorePlugin.ColorPickerProvider.serializer()
        CorePlugin.ColorPickerProvider::class with CorePlugin.ColorPickerProvider.serializer()
        CorePlugin.RadioButtonStripProvider::class with CorePlugin.RadioButtonStripProvider.serializer()
        CorePlugin.Scenes::class with CorePlugin.Scenes.serializer()
        CorePlugin.Patches::class with CorePlugin.Patches.serializer()
        CorePlugin.XyPad::class with CorePlugin.XyPad.serializer()
    }
}
