package baaahs.glshaders

import baaahs.Logger
import baaahs.getTimeMillis
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslContext
import baaahs.glsl.Uniform
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE

class GlslProgram(private val gl: GlslContext, shaderSrc: String) {
    private val id = gl.runInContext { gl.check { createProgram() ?: throw IllegalStateException() } }

    private val vertexShader = gl.runInContext {
        gl.createVertexShader(
            """#version ${gl.glslVersion}
    
precision lowp float;

// xy = vertex position in normalized device coordinates ([-1,+1] range).
in vec2 Vertex;

const vec2 scale = vec2(0.5, 0.5);

void main()
{
    vec2 vTexCoords  = Vertex * scale + scale; // scale vertex attribute to [0,1] range
    gl_Position = vec4(Vertex, 0.0, 1.0);
}
"""
        )
    }

    val fragment = glslAnalyzer.analyze(shaderSrc)
    val bindings = fragment.globalVars.map { uniform ->
        Binding(uniform, defaultBindings["${uniform.type}:${uniform.name}"])
    }

    private val fragShader = gl.runInContext {
        gl.createFragmentShader(
            """#version ${gl.glslVersion}

#ifdef GL_ES
precision mediump float;
#endif

uniform float sm_beat;
out vec4 sm_fragColor;

#line 0 1
${shaderSrc.replace("gl_FragColor", "sm_fragColor")}
        """.trimIndent()
        )
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
        bindings.forEach { it.bind() }
    }

    fun release() {
//        gl.runInContext { gl.check { deleteProgram } }
    }

    inner class Binding(
        val glslUniform: ShaderFragment.GlslVar,
        providerFactory: (() -> Provider)?
    ) {
        private val uniformLocation by lazy {
            gl.runInContext { gl.check {
                getUniformLocation(id, glslUniform.name)?.let { Uniform(gl, it) } } }
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

    companion object {
        val logger = Logger("GlslProgram")

        private val glslAnalyzer = GlslAnalyzer()
        private val defaultBindings = mapOf<String, () -> Provider>(
            "float:time" to { TimeProvider() },
            "float:iTime" to { TimeProvider() },
            "vec2:resolution" to { ResolutionProvider() },
            "vec2:iResolution" to { ResolutionProvider() }
        )
    }
}
