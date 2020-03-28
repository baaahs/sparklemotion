package baaahs.glsl

import baaahs.shaders.GlslShader
import com.danielgergely.kgl.GL_LINK_STATUS
import com.danielgergely.kgl.GL_TRUE
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Program(
    private val gl: GlslContext,
    val fragShader: String,
    private val glslVersion: String,
    plugins: List<GlslPlugin> = GlslBase.plugins
) {
    private val id = gl.check { createProgram() ?: throw IllegalStateException() }

    val params: List<GlslShader.Param>
    val plugins = plugins.map { gl.check { it.forProgram(this, this@Program) } }

    private val gadgetPattern = Regex(
        "\\s*//\\s*SPARKLEMOTION GADGET:\\s*([^\\s]+)\\s+(\\{.*})\\s*\n" +
                "\\s*uniform\\s+([^\\s]+)\\s+([^\\s]+);"
    )
    private val json = Json(JsonConfiguration.Stable.copy(isLenient = true))

    private var nextTextureId = 0

    fun obtainTextureId(): Int {
        check(nextTextureId <= 31) { "too many textures!" }
        return nextTextureId++
    }

    init {
        attachVertexShader()
        val src = buildFragmentShader()
        println(src)
        params = findParams(src)
        val fragmentShader = gl.createFragmentShader(src)
        attachShader(fragmentShader)

        if (!link()) {
            val infoLog = getInfoLog()
            throw RuntimeException("ProgramInfoLog: $infoLog")
        }

        this.plugins.forEach { it.afterCompile() }
    }

    fun findParams(glslFragmentShader: String): List<GlslShader.Param> {
        return gadgetPattern.findAll(glslFragmentShader).map { matchResult ->
            println("matches: ${matchResult.groupValues}")
            val (gadgetType, configJson, valueTypeName, varName) = matchResult.destructured
            val configData = json.parseJson(configJson)
            val valueType = when (valueTypeName) {
                "int" -> GlslShader.Param.Type.INT
                "float" -> GlslShader.Param.Type.FLOAT
                "vec3" -> GlslShader.Param.Type.VEC3
                else -> throw IllegalArgumentException("unsupported type $valueTypeName")
            }
            GlslShader.Param(varName, gadgetType, valueType, configData.jsonObject)
        }.toList()
    }

    fun getInfoLog(): String? = gl.check { getProgramInfoLog(id) }
    fun attachShader(compiledShader: CompiledShader) = gl.check { attachShader(id, compiledShader.id) }
    fun link(): Boolean {
        gl.check { linkProgram(id) }
        return gl.check { getProgramParameter(id, GL_LINK_STATUS) } == GL_TRUE
    }

    fun bind() = gl.check { useProgram(id) }

    fun getUniform(name: String): Uniform? = gl.check { getUniformLocation(id, name)?.let { Uniform(this, it) } }

    private fun attachVertexShader() {
        val vertexShaderSource = """#version $glslVersion
    
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
        val vertexShader = gl.createVertexShader(vertexShaderSource)
        attachShader(vertexShader)
    }

    fun getVertexAttribLocation(): Int {
        return gl.check { getAttribLocation(id, "Vertex") }
    }

    private fun buildFragmentShader(): String {
        return """#version $glslVersion
    
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D sm_uvCoords;

// SPARKLEMOTION GADGET: Slider { "name": "u scale", "minValue": 0, "maxValue": 3 }
uniform float sm_uScale;

// SPARKLEMOTION GADGET: Slider { "name": "v scale", "minValue": 0, "maxValue": 3 }
uniform float sm_vScale;

// SPARKLEMOTION GADGET: StartOfMeasure { "name": "startOfMeasure" }
uniform float sm_startOfMeasure;

// SPARKLEMOTION GADGET: Beat { "name": "beat" }
uniform float sm_beat;

// SPARKLEMOTION GADGET: Slider { "name": "Brightness", "minValue": 0, "maxValue": 1 }
uniform float sm_brightness;

// SPARKLEMOTION GADGET: Slider { "name": "Saturation", "minValue": 0, "maxValue": 1 }
uniform float sm_saturation;

${plugins.map { plugin -> plugin.glslPreamble }.joinToString("\n")}

out vec4 sm_fragColor;

${fragShader
        .replace(
            Regex("void main\\s*\\(\\s*(void\\s*)?\\)"),
            "void sm_main(vec2 sm_pixelCoord)"
        )
        .replace("gl_FragCoord", "sm_pixelCoord")
        .replace("gl_FragColor", "sm_fragColor")
    }

// Coming in, `gl_FragCoord` is a vec2 where `x` and `y` correspond to positions in `sm_uvCoords`.
// We look up the `u` and `v` coordinates (which should be floats `[0..1]` in the mapping space) and
// pass them to the shader's original `main()` method.
void main(void) {
    int uvX = int(gl_FragCoord.x);
    int uvY = int(gl_FragCoord.y);
    
    vec2 pixelCoord = vec2(
        texelFetch(sm_uvCoords, ivec2(uvX * 2, uvY), 0).r * sm_uScale,    // u
        texelFetch(sm_uvCoords, ivec2(uvX * 2 + 1, uvY), 0).r * sm_vScale // v
    );

    sm_main(pixelCoord);
}
"""
    }
}
