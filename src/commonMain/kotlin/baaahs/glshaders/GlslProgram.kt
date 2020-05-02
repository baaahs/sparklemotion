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

    val noOpProvider = object : UniformProvider {
        override fun set(uniform: Uniform) {
            // TODO
        }
    }

    private lateinit var bindings: List<Binding>

    fun bind(fn: (Patch.UniformPort) -> UniformProvider?) {
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
        bindings.filter { it.uniformPort is UserUniformPort }

    fun obtainTextureId(): Int {
        check(nextTextureId <= 31) { "too many textures" }
        return nextTextureId++
    }

    fun release() {
        bindings.forEach { it.release() }
//        TODO gl.runInContext { gl.check { deleteProgram } }
    }

    inner class Binding(
        internal val uniformPort: Patch.UniformPort,
        val uniformProvider: UniformProvider
    ) {
        internal val uniformLocation by lazy {
            gl.runInContext {
                gl.check {
                    getUniformLocation(id, uniformPort.varName)?.let { Uniform(gl, it) }
                }
            }
        }

        fun setUniform() {
            uniformLocation?.let { uniformLocation ->
                uniformProvider.set(uniformLocation)
            }
        }

        fun release() = uniformProvider.release()
    }

    interface UniformProvider {
        fun set(uniform: Uniform)
        fun release() {}
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    object Resolution : StockUniformPort("vec2", "resolution", ContentType.Resolution.pluginId!!)

    object Time : StockUniformPort("float", "time", ContentType.Time.pluginId!!)

    object GlFragCoord : StockUniformPort("vec4", "gl_FragCoord", "baaahs.Core:none") {
        override val varName: String = name
        override val isImplicit = true
    }

    object UvCoordsTexture : StockUniformPort("sampler2D", "sm_uvCoordsTexture", ContentType.UvCoordinateTexture.pluginId!!)

    open class StockUniformPort(type: String, name: String, pluginId: String) : Patch.UniformPort(type, name, pluginId) {
        override val shaderId: String? = null
    }

    class UserUniformPort(type: String, name: String, pluginId: String = "baaahs.Gadgets:whatever") : Patch.UniformPort(type, name, pluginId)


    companion object {
        val logger = Logger("GlslProgram")
    }
}
