package baaahs.glshaders

import baaahs.Logger
import baaahs.getTimeMillis
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import com.danielgergely.kgl.*

class GlslProgram(internal val gl: GlslContext, val patch: Patch) {
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

    val noOpProvider = object : Provider {
        override fun set(uniform: Uniform) {
            // TODO
        }
    }

    val bindings = patch.uniformPorts.map { uniformInput ->
        val providerFactory = if (uniformInput is StockUniformPort) {
            uniformInput.providerFactory
        } else {
            {
                logger.warn { "No provider binding found for ${uniformInput.name}" }
                noOpProvider
            }
        }
        Binding(uniformInput, providerFactory)
    }

    fun setResolution(x: Float, y: Float) {
        bindings.forEach { (it.provider as? ResolutionListener)?.onResolution(x, y) }
    }

    fun bind() {
        gl.runInContext { gl.check { useProgram(id) } }
        bindings.forEach { it.bind() }
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
        providerFactory: ((GlslProgram) -> Provider)
    ) {
        internal val provider = providerFactory.invoke(this@GlslProgram)

        internal val uniformLocation by lazy {
            gl.runInContext {
                gl.check {
                    getUniformLocation(id, uniformPort.varName)?.let { Uniform(gl, it) }
                }
            }
        }

        fun bind() {
            uniformLocation?.let { uniformLocation ->
                gl.runInContext { provider.set(uniformLocation) }
            }
        }

        fun release() = provider.release()
    }

    interface Provider {
        fun set(uniform: Uniform)
        fun release() {}
    }

    class ResolutionProvider : Provider, ResolutionListener {
        var x = 1f
        var y = 1f

        override fun set(uniform: Uniform) {
            uniform.set(x, y)
        }

        override fun onResolution(x: Float, y: Float) {
            this.x = x
            this.y = y
        }
    }

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    class TimeProvider : Provider {
        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    inner class UvCoordProvider : Provider, GlslRenderer.ArrangementListener {
        private val uvCoordTextureId = obtainTextureId()
        private var uvCoordTexture = gl.check { createTexture() }

        override fun onArrangementChange(arrangement: GlslRenderer.Arrangement) {
            if (arrangement.uvCoords.isEmpty()) return

            val pixWidth = arrangement.pixWidth
            val pixHeight = arrangement.pixHeight
            val floatBuffer = FloatBuffer(arrangement.uvCoords)

            gl.check { activeTexture(GL_TEXTURE0 + uvCoordTextureId) }
            gl.check { bindTexture(GL_TEXTURE_2D, uvCoordTexture) }
            gl.check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
            gl.check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
            gl.check {
                texImage2D(
                    GL_TEXTURE_2D, 0,
                    GL_R32F, pixWidth * 2, pixHeight, 0,
                    GL_RED,
                    GL_FLOAT, floatBuffer
                )
            }
        }

        override fun set(uniform: Uniform) {
            uniform.set(uvCoordTextureId)
        }

        override fun release() {
            gl.check { deleteTexture(uvCoordTexture) }
        }
    }

    object Resolution : StockUniformPort("vec2", "resolution", { p -> ResolutionProvider() })

    object Time : StockUniformPort("float", "time", { p -> TimeProvider() })

    object GlFragCoord : StockUniformPort("vec4", "gl_FragCoord", { p -> p.noOpProvider }) {
        override val varName: String = name
        override val isImplicit = true
    }

    object UvCoordsTexture : StockUniformPort("sampler2D", "sm_uvCoordsTexture", { p -> p.UvCoordProvider() })

    open class StockUniformPort(
        type: String, name: String, val providerFactory: (GlslProgram) -> Provider
    ) : Patch.UniformPort(type, name) {
        override val shaderId: String? = null
    }

    class UserUniformPort(type: String, name: String) : Patch.UniformPort(type, name)


    companion object {
        val logger = Logger("GlslProgram")
    }
}
