package baaahs.glshaders

import baaahs.Logger
import baaahs.getTimeMillis
import baaahs.glshaders.GlslCode.ContentType
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.Uniform
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE

class GlslProgram(private val gl: GlslContext, val patch: Patch) {
    private val id = gl.runInContext { gl.check { createProgram() ?: throw IllegalStateException() } }

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

    val bindings = patch.uniformInputs.map { uniformInput ->
        val providerFactory = if (uniformInput is StockUniformInput) {
            uniformInput.providerFactory
        } else {
            { noOpProvider }
        }
        Binding(uniformInput, providerFactory)
    }

    fun bind() {
        gl.runInContext { gl.check { useProgram(id) } }
        bindings.forEach { it.bind() }
    }

    fun release() {
//        TODO gl.runInContext { gl.check { deleteProgram } }
    }

    inner class Binding(
        val uniformInput: UniformInput,
        providerFactory: (() -> Provider)?
    ) {
        private val uniformLocation by lazy {
            gl.runInContext {
                gl.check {
                    getUniformLocation(id, uniformInput.varName)?.let { Uniform(gl, it) }
                }
            }
        }
        private val provider = providerFactory?.invoke()

        fun bind() {
            uniformLocation?.let { uniformLocation ->
                gl.runInContext { provider?.set(uniformLocation) }
            }
        }
    }

    interface Provider {
        fun set(uniform: Uniform)
    }

    class ResolutionProvider : Provider {
        override fun set(uniform: Uniform) {
            uniform.set(320f, 150f) // TODO: these need to match the canvas size
        }
    }

    class TimeProvider : Provider {
        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    class UvCoordProvider : Provider {
        override fun set(uniform: Uniform) {
            // No-op.
        }
    }

    interface Port {
        val shaderId: String?
    }

    object Resolution : StockUniformInput("vec2", "resolution", ::ResolutionProvider)
    object Time : StockUniformInput("float", "time", ::TimeProvider)
    object GlFragCoord : StockUniformInput("vec4", "gl_FragCoord", ::UvCoordProvider) {
        override val varName: String = name
        override val isImplicit = true
    }

    data class ShaderOut(override val shaderId: String) : Port

    object PixelColor : Port {
        override val shaderId: String? = null
    }

    open class StockUniformInput(
        type: String, name: String, val providerFactory: () -> Provider
    ) : UniformInput(type, name) {
        override val shaderId: String? = null
    }

    open class UniformInput(val type: String, val name: String) : Port {
        override val shaderId: String? = null

        open val varName: String get() = "in_$name"
        open val isImplicit = false

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UniformInput) return false

            if (type != other.type) return false
            if (name != other.name) return false
            if (shaderId != other.shaderId) return false
            if (isImplicit != other.isImplicit) return false

            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + (shaderId?.hashCode() ?: 0)
            result = 31 * result + isImplicit.hashCode()
            return result
        }

        override fun toString(): String {
            return "UniformInput(type='$type', name='$name', shaderId=$shaderId, isImplicit=$isImplicit)"
        }
    }

    class UserUniformInput(type: String, name: String) : UniformInput(type, name)

    data class ShaderPort(override val shaderId: String, val portName: String) : Port


    companion object {
        fun autoWire(shaders: Map<String, ShaderFragment>): Patch {
            val uvProjectorName =
                shaders.entries
                    .find { (_, shaderFragment) -> shaderFragment.shaderType == ShaderType.Projection }
                    ?.key

            val links = arrayListOf<Link>()
            shaders.forEach { (name, shaderFragment) ->
                shaderFragment.inputPorts.forEach { inputPort ->
                    val uniformInput =
                        if (inputPort.contentType == ContentType.UvCoordinate && uvProjectorName != null) {
                            { ShaderOut(uvProjectorName) }
                        } else {
                            defaultBindings[inputPort.contentType]
                        }
                            ?: { UserUniformInput(inputPort.type, inputPort.name) }

                    links.add(uniformInput() to ShaderPort(name, inputPort.name))
                }
            }
            return Patch(shaders, links)
        }

        val logger = Logger("GlslProgram")

        private val defaultBindings = mapOf<ContentType, () -> UniformInput>(
            ContentType.UvCoordinate to { GlFragCoord },
//            ContentType.XyCoordinate to { TODO() },
//            ContentType.XyzCoordinate to { TODO() },
//            ContentType.Color to { TODO() },
            ContentType.Time to { Time },
            ContentType.Resolution to { Resolution }
//            ContentType.Unknown to { TODO() }
        )
    }
}

typealias Link = Pair<GlslProgram.Port, GlslProgram.Port>
