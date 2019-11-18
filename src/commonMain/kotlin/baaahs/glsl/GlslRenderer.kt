package baaahs.glsl

import baaahs.Color
import baaahs.Surface
import baaahs.getTimeMillis
import baaahs.shaders.GlslShader
import baaahs.timeSync
import com.danielgergely.kgl.*
import kotlin.math.max
import kotlin.math.min

open class GlslRenderer(
    val gl: Kgl,
    private val contextSwitcher: ContextSwitcher,
    val fragShader: String,
    val adjustableValues: List<GlslShader.AdjustableValue>,
    private val glslVersion: String,
    plugins: List<GlslPlugin>
) {
    private val surfacesToAdd: MutableList<GlslSurface> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextSurfaceOffset: Int = 0

    val glslSurfaces: MutableList<GlslSurface> = mutableListOf()

    private var nextTextureId = 0
    private val uvCoordTextureId = getTextureId()
    private val rendererPlugins = plugins.map { it.forRenderer(this) }

    var arrangement: Arrangement

    val program: Program = gl { createShaderProgram() }
    private val uvCoordsUniform: Uniform? = gl { Uniform.find(program, "sm_uvCoords") }
    private val resolutionUniform: Uniform? = gl { Uniform.find(program, "resolution") }
    private val timeUniform: Uniform? = gl { Uniform.find(program, "time") }

    private val quad: Quad = gl { Quad(gl, program) }

    val stats = Stats()

    init {
        gl { gl.clearColor(0f, .5f, 0f, 1f) }

        arrangement = createArrangement(0, FloatArray(0), glslSurfaces)
    }

    fun getTextureId(): Int {
        check(nextTextureId <= 31) { "too many textures!" }
        return nextTextureId++
    }

    private fun createShaderProgram(): Program {
        // Create a simple shader program
        val program = Program.create(gl)

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
        val vertexShader = Shader.createVertexShader(gl, vertexShaderSource)
        program.attachShader(vertexShader)

        val src = """#version $glslVersion

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D sm_uvCoords;
uniform float sm_uScale;
uniform float sm_vScale;
uniform float sm_startOfMeasure;
uniform float sm_beat;
${rendererPlugins.map { plugin -> plugin.glslPreamble }.joinToString("\n")}

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

        println(src)
        val fragmentShader = Shader.createFragmentShader(gl, src)
        program.attachShader(fragmentShader)

        if (!program.link()) {
            val infoLog = program.getInfoLog()
            throw RuntimeException("ProgramInfoLog: $infoLog")
        }

        rendererPlugins.forEach { it.afterCompile(program) }

        return program
    }

    fun addSurface(surface: Surface, uvTranslator: UvTranslator): GlslSurface? {
        val glslSurface = GlslSurface(SurfacePixels(surface, nextPixelOffset), Uniforms(), uvTranslator)
        nextPixelOffset += surface.pixelCount

        surfacesToAdd.add(glslSurface)
        return glslSurface
    }

    inner class SurfacePixels(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = arrangement.getPixel(pixel0Index + i)
    }

    private fun createArrangement(pixelCount: Int, uvCoords: FloatArray, surfaceCount: List<GlslSurface>): Arrangement =
        Arrangement(pixelCount, uvCoords, surfaceCount.toList())

    fun draw() {
        withGlContext {
            program.bind()
            stats.addSurfacesMs += timeSync { incorporateNewSurfaces() }
            stats.bindFbMs += timeSync { arrangement.bindFramebuffer() }
            stats.renderMs += timeSync { render() }
            stats.readPxMs += timeSync { arrangement.copyToPixelBuffer() }
        }

        stats.frameCount++
    }

    private fun render() {
        val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f

        resolutionUniform?.set(1f, 1f)
        timeUniform?.set(thisTime)

        arrangement.bindUvCoordTexture(uvCoordsUniform!!)
        arrangement.bindUniforms()

        rendererPlugins.forEach { it.beforeRender() }

        gl.viewport(0, 0, pixelCount.bufWidth, pixelCount.bufHeight)
        gl.clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        quad.render()

        gl.finish()

        val programLog = program.getInfoLog() ?: ""
        if (programLog.isNotEmpty()) println("ProgramInfoLog: $programLog")
    }

    protected fun incorporateNewSurfaces() {
        if (surfacesToAdd.isNotEmpty()) {
            val oldUvCoords = arrangement.uvCoords
            val newPixelCount = nextPixelOffset

            arrangement.release()

            val newUvCoords = FloatArray(newPixelCount.bufSize * 2)
            oldUvCoords.copyInto(newUvCoords)

            surfacesToAdd.forEach {
                val surface = it.pixels.surface
                val pixelLocations = LinearSurfacePixelStrategy.forSurface(surface)
                val uvTranslator = it.uvTranslator.forPixels(pixelLocations)

                for (i in 0 until uvTranslator.pixelCount) {
                    val uvOffset = (it.pixels.pixel0Index + i) * 2
                    val (u, v) = uvTranslator.getUV(i)
                    newUvCoords[uvOffset] = u     // u
                    newUvCoords[uvOffset + 1] = v // v
                }
            }

            glslSurfaces.addAll(surfacesToAdd)
            surfacesToAdd.clear()

            arrangement = createArrangement(newPixelCount, newUvCoords, glslSurfaces)
            arrangement.bindUvCoordTexture(uvCoordsUniform!!)

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")
        }
    }

    inner class Arrangement(val pixelCount: Int, val uvCoords: FloatArray, val surfaces: List<GlslSurface>) {
        val adjustableUniforms: List<AdjustableUniform> =
            adjustableValues.map { adjustableValue ->
                UnifyingAdjustableUniform(program, adjustableValue, surfaces.size)
            }

        private var uvCoordTexture = gl { gl.createTexture() }
        private val frameBuffer = gl { gl.createFramebuffer() }
        private val renderBuffer = gl { gl.createRenderbuffer() }
        private val pixelBuffer = ByteBuffer(pixelCount.bufSize * 4)
        private val uvCoordsFloatBuffer = FloatBuffer(uvCoords)

        fun bindFramebuffer() {
            gl.checkForGlError()
            gl { gl.bindFramebuffer(GL_FRAMEBUFFER, frameBuffer) }

            gl { gl.bindRenderbuffer(GL_RENDERBUFFER, renderBuffer) }
//            console.error("pixel count: $pixelCount (${pixelCount.bufWidth} x ${pixelCount.bufHeight} = ${pixelCount.bufSize})")
            gl { gl.renderbufferStorage(GL_RENDERBUFFER, GL_RGBA4, pixelCount.bufWidth, pixelCount.bufHeight) }
            gl { gl.framebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderBuffer) }

            val status = gl { gl.checkFramebufferStatus(GL_FRAMEBUFFER) }
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                println(RuntimeException("FrameBuffer huh? $status").message)
            }
        }

        fun bindUvCoordTexture(uvCoordsLocation: Uniform) {
            gl { gl.activeTexture(GL_TEXTURE0 + uvCoordTextureId) }
            gl { gl.bindTexture(GL_TEXTURE_2D, uvCoordTexture) }
            gl { gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
            gl { gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
            gl {
                gl.texImage2D(
                    GL_TEXTURE_2D, 0,
                    GL_R32F, pixelCount.bufWidth * 2, pixelCount.bufHeight, 0,
                    GL_RED,
                    GL_FLOAT, uvCoordsFloatBuffer
                )
            }
            uvCoordsLocation.set(uvCoordTextureId)
        }

        fun getPixel(pixelIndex: Int): Color {
            val offset = pixelIndex * 4
            return Color(
                red = pixelBuffer[offset],
                green = pixelBuffer[offset + 1],
                blue = pixelBuffer[offset + 2],
                alpha = pixelBuffer[offset + 3]
            )
        }

        fun copyToPixelBuffer() {
            gl.readPixels(0, 0, pixelCount.bufWidth, pixelCount.bufHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer)
        }

        fun release() {
            println("Release $this with $pixelCount pixels and ${uvCoords.size} uvs")

            gl { gl.bindRenderbuffer(GL_RENDERBUFFER, null) }
            gl { gl.bindFramebuffer(GL_FRAMEBUFFER, null) }
            gl { gl.bindTexture(GL_TEXTURE_2D, null) }

            gl { gl.deleteFramebuffer(frameBuffer) }
            gl { gl.deleteRenderbuffer(renderBuffer) }
            gl { gl.deleteTexture(uvCoordTexture) }
        }

        fun bindUniforms() {
            adjustableValues.forEachIndexed { adjustableIndex, adjustable ->
                surfaces.forEachIndexed { surfaceIndex, surface ->
                    val value = surface.uniforms.values?.get(adjustableIndex)
                    value?.let {
                        val adjustableUniform = adjustableUniforms[adjustableIndex]
                        adjustableUniform.setValue(surfaceIndex, value)
                    }
                }
            }

            adjustableUniforms.forEach { it.bind() }
        }
    }

    val Int.bufWidth: Int get() = max(1, min(this, 1024))
    val Int.bufHeight: Int get() = this / 1024 + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Uniforms {
        var values: Array<Any?>? = null

        fun updateFrom(values: Array<Any?>) {
            this.values = values
        }
    }

    inline fun <T> gl(fn: () -> T): T {
        val result = fn.invoke()
        gl.checkForGlError()
        return result
    }

    private fun <T> withGlContext(fn: () -> T): T {
        return contextSwitcher.inContext { fn() }
    }

    interface ContextSwitcher {
        fun <T> inContext(fn: () -> T): T
    }

    class Stats {
        var addSurfacesMs = 0; internal set
        var bindFbMs = 0; internal set
        var renderMs = 0; internal set
        var readPxMs = 0; internal set
        var frameCount = 0; internal set

        fun dump() {
            println(
                "Render of $frameCount frames took: " +
                        "addSurface=${addSurfacesMs}ms " +
                        "bindFbMs=${bindFbMs}ms " +
                        "renderMs=${renderMs}ms " +
                        "readPxMs=${readPxMs}ms " +
                        "$this"
            )
        }

        fun reset() {
            addSurfacesMs = 0
            bindFbMs = 0
            renderMs = 0
            readPxMs = 0
            frameCount = 0
        }
    }
}
