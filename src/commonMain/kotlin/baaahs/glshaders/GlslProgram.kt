package baaahs.glshaders

import baaahs.Logger
import baaahs.getTimeMillis
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.Uniform
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE

class GlslProgram(private val gl: GlslContext, val shaderSrc: String) {
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

//    val fragment = glslAnalyzer.analyze(shaderSrc)
//    val bindings = fragment.globalVars.map { uniform ->
//        Binding(uniform, defaultBindings["${uniform.type}:${uniform.name}"])
//    }

    private val fragShader = gl.runInContext {
        gl.createFragmentShader("#version ${gl.glslVersion}\n\n$shaderSrc\n")
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

    fun bind() {
        gl.runInContext { gl.check { useProgram(id) } }
//        bindings.forEach { it.bind() }
    }

    fun release() {
//        gl.runInContext { gl.check { deleteProgram } }
    }

    inner class Binding(
        val glslUniform: GlslCode.GlslVar,
        providerFactory: (() -> Provider)?
    ) {
        private val uniformLocation by lazy {
            gl.runInContext {
                gl.check {
                    getUniformLocation(id, glslUniform.name)?.let { Uniform(gl, it) }
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

    class TimeProvider : Provider {
        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    class ResolutionProvider : Provider {
        override fun set(uniform: Uniform) {
            uniform.set(320f, 150f) // TODO: these need to match the canvas size
        }
    }

    interface Port {
        val shaderId: String?
    }

    object Resolution : StockUniformInput("vec2", "resolution", ::ResolutionProvider)
    object Time : StockUniformInput("float", "time", ::TimeProvider)
    object UvCoord : StockUniformInput("vec4", "gl_FragCoord", ::TODO) {
        override val uniformName: String = name
        override val isImplicit = true
    }

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

        open val uniformName: String get() = "in_$name"
        open val isImplicit = false
    }

    class ShaderPort(override val shaderId: String, val portName: String) : Port

    data class Patch(
        val shaders: Map<String, ShaderFragment>,
        val links: List<Pair<Port, Port>>
    )

    companion object {
        val logger = Logger("GlslProgram")

        private val glslAnalyzer = GlslAnalyzer()
        private val defaultBindings = mapOf<String, () -> Provider>(
            "float:time" to { TimeProvider() },
            "float:iTime" to { TimeProvider() },
            "vec2:resolution" to { ResolutionProvider() },
            "vec2:iResolution" to { ResolutionProvider() }
        )

        fun toGlsl(patch: Patch): String {
            val fragments = patch.shaders

            val buf = StringBuilder()
            buf.append("#ifdef GL_ES\n")
            buf.append("precision mediump float;\n")
            buf.append("#endif\n")
            buf.append("\n")
            buf.append("// SparkleMotion generated GLSL\n")
            buf.append("\n")

            val fromById = hashMapOf<String?, ArrayList<Pair<Port, Port>>>()
            val toById = hashMapOf<String?, ArrayList<Pair<Port, Port>>>()
            patch.links.forEach { link ->
                val (from, to) = link
                fromById.getOrPut(from.shaderId) { arrayListOf() }.add(link)
                toById.getOrPut(to.shaderId) { arrayListOf() }.add(link)
            }

            val fromGlobal: List<Pair<Port, Port>> = fromById[null] ?: emptyList()
            val toGlobal: List<Pair<Port, Port>> = toById[null] ?: emptyList()

            fromGlobal.forEach { (from, _) ->
                (from as? UniformInput)?.let {
                    if (!it.isImplicit)
                        buf.append("uniform ${from.type} ${from.uniformName};\n")
                }
            }
            buf.append("\n")

            fragments.entries.forEachIndexed { i, (shaderId, shaderFragment) ->
                val namespace = "p$i"
                buf.append("// Shader ID: $shaderId; namespace: $namespace")
                val portMap = hashMapOf<String, String>()
                toById[shaderId]?.forEach { (from, to) ->
                    when {
                        to is ShaderPort && from is UniformInput ->
                            portMap[to.portName] = from.uniformName
                    }
                }
                buf.append(shaderFragment.toGlsl(namespace, portMap))
            }

            buf.append("\n\n#line 10001\n")
            buf.append("void main() {\n")
            fragments.values.forEachIndexed { i, shaderFragment ->
                val namespace = "p$i"
                buf.append(shaderFragment.invocationGlsl(namespace))
            }
            buf.append("}\n")
            return buf.toString()
        }
    }
}
