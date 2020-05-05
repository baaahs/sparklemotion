package baaahs.glshaders

import baaahs.Logger
import baaahs.glshaders.GlslCode.ContentType
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE

class GlslProgram(
    internal val gl: GlslContext,
    private val patch: Patch
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

    private var nextTextureId = 0
    private val fragShader = gl.runInContext {
        gl.createFragmentShader("#version ${gl.glslVersion}\n\n${patch.toGlsl()}\n")
    }

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
    }

    val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private lateinit var bindings: List<Binding>

    fun bind(fn: (Patch.UniformPortRef) -> UniformProvider?) {
        if (::bindings.isInitialized) {
            throw IllegalStateException("program is already bound")
        }

        bindings = patch.uniformPorts.mapNotNull { uniformPort ->
            val uniformProvider = fn(uniformPort)
            if (uniformProvider != null) {
                Binding(uniformPort, uniformProvider)
            } else {
                logger.warn { "no UniformProvider bound for $uniformPort" }
                null
            }
        }
    }

    private inline fun <reified T> bindingsOf(): List<T> {
        return bindings
            .map { it.uniformProvider }
            .filterIsInstance<T>()
    }

    val arrangementListeners: List<GlslRenderer.ArrangementListener>
        get() = bindingsOf()

    val resolutionListeners: List<ResolutionListener>
        get() = bindingsOf()

    fun setResolution(x: Float, y: Float) {
        resolutionListeners.forEach { it.onResolution(x, y) }
    }

    fun prepareToDraw() {
        gl.runInContext {
            gl.check { useProgram(id) }

            bindings.forEach { it.setUniform() }
        }
    }

    val userInputs: List<Binding> get() =
        bindings.filter { it.uniformPort is InputPortRef }

    fun obtainTextureId(): Int {
        check(nextTextureId <= 31) { "too many textures" }
        return nextTextureId++
    }

    fun release() {
        bindings.forEach { it.release() }
//        TODO gl.runInContext { gl.check { deleteProgram } }
    }

    inner class Binding(
        internal val uniformPort: Patch.UniformPortRef,
        val uniformProvider: UniformProvider
    ) {
        init {
            val supportedTypes = uniformProvider.supportedTypes
            if (!supportedTypes.contains(uniformPort.type)) {
                throw CompiledShader.LinkException(
                    "can't set uniform ${uniformPort.type} ${uniformPort.name}: expected $supportedTypes)"
                )
            }
        }

        private val uniformLocation =
            gl.runInContext {
                gl.check {
                    val uniformLoc = getUniformLocation(id, uniformPort.varName)
                    if (uniformLoc == null) {
                        logger.warn { "no such uniform ${uniformPort.varName}" }
                    }
                    uniformLoc?.let { Uniform(gl, it) }
                }
            }

        fun setUniform() {
            try {
                uniformLocation?.let { uniformProvider.set(it) }
            } catch (e: Exception) {
                logger.error(e) { "failed to set ${uniformPort.name} from $uniformProvider" }
            }
        }

        fun release() = uniformProvider.release()
    }

    interface UniformProvider {
        val supportedTypes: List<String>

        fun set(uniform: Uniform)
        fun release() {}
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    object Resolution : StockUniformPortRef("vec2", "resolution", ContentType.Resolution.pluginId!!)

    object Time : StockUniformPortRef("float", "time", ContentType.Time.pluginId!!)

    object GlFragCoord : StockUniformPortRef("vec4", "gl_FragCoord", "baaahs.Core:none") {
        override val varName: String = name
        override val isImplicit = true
    }

    object UvCoordsTexture : StockUniformPortRef("sampler2D", "sm_uvCoordsTexture", ContentType.UvCoordinateTexture.pluginId!!)

    open class StockUniformPortRef(type: String, name: String, pluginId: String) : Patch.UniformPortRef(type, name, pluginId) {
        override val shaderId: String? = null
    }

    class InputPortRef(
        type: String,
        name: String,
        pluginId: String? = null,
        pluginConfig: Map<String, String> = emptyMap()
    ) : Patch.UniformPortRef(type, name, pluginId, pluginConfig)


    companion object {
        val logger = Logger("GlslProgram")
    }
}
