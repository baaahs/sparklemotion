package baaahs.glsl

import baaahs.Color
import baaahs.Surface
import baaahs.getTimeMillis
import baaahs.shaders.GlslShader
import baaahs.timeSync
import de.fabmax.kool.TextureProps
import de.fabmax.kool.createContext
import de.fabmax.kool.defaultProps
import de.fabmax.kool.gl.*
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.createFloat32Buffer
import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

class JsGlslRenderer(
    fragShader: String,
    adjustableValues: List<GlslShader.AdjustableValue>
) : GlslRenderer(createContext(), fragShader, adjustableValues) {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
//    var gl: WebGL2RenderingContext = canvas.getContext("webgl2")!! as WebGL2RenderingContext

    private val program: ProgramResource
    private val quad: Quad

    init {
        gl { glClearColor(0f, .5f, 0f, 1f) }

        program = createShaderProgram()
        quad = Quad()

        findUniforms()

        instance = createInstance(1, FloatArray(2), nextSurfaceOffset)
    }

    inner class Quad {
        private val vertices = arrayOf(
            // First triangle:
            1.0f, 1.0f,
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            // Second triangle:
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f
        )

        private var quadVertexBuffer: BufferResource = BufferResource.create(GL_ARRAY_BUFFER, ctx)

        init {
            val verticesBuffer = createFloat32Buffer(vertices.size).put(vertices.toFloatArray())
            quadVertexBuffer.setData(verticesBuffer, GL_STATIC_DRAW, ctx)

            val vertexAttr = gl { glGetAttribLocation(program, "Vertex") }
            gl { glVertexAttribPointer(vertexAttr, 2,
                WebGLRenderingContext.FLOAT, false, 0, 0) }
            gl { glEnableVertexAttribArray(vertexAttr) }

            gl { glBindBuffer(WebGLRenderingContext.ARRAY_BUFFER, null) }
        }

        internal fun render() {
            gl { glBindBuffer(WebGLRenderingContext.ARRAY_BUFFER, quadVertexBuffer) }

            // Draw the triangles
            gl { gl.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 6) }

            gl { glBindBuffer(WebGLRenderingContext.ARRAY_BUFFER, null) }
        }

        private fun release() {
            gl { glDeleteBuffer(quadVertexBuffer) }
        }
    }

    override fun createSurfacePixels(surface: Surface, pixelOffset: Int): baaahs.glsl.SurfacePixels =
        SurfacePixels(surface, pixelOffset)

    inner class SurfacePixels(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = instance.getPixel(pixel0Index + i)
    }

    override fun createSurfaceMonoPixel(surface: Surface, pixelOffset: Int): baaahs.glsl.SurfacePixels =
        SurfaceMonoPixel(surface, pixelOffset)

    inner class SurfaceMonoPixel(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = instance.getPixel(pixel0Index)
    }

    override fun createInstance(pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int): GlslRenderer.Instance =
        Instance(pixelCount, uvCoords, surfaceCount)

    override fun draw() {
        withGlContext {
            val addSurfacesMs = timeSync { incorporateNewSurfaces() }

            val bindFbMs = timeSync { instance.bindFramebuffer() }
            val renderMs = timeSync { render() }

            val readPxMs = timeSync {
                instance.copyToPixelBuffer()
            }

            gl { gl.finish() }
//            gl { glfwSwapBuffers(window) }

//            println("Render of $pixelCount took: " +
//                    "addSurface=${addSurfacesMs}ms " +
//                    "bindFbMs=${bindFbMs}ms " +
//                    "renderMs=${renderMs}ms " +
//                    "readPxMs=${readPxMs}ms " +
//                    "$this")
        }
    }

    private fun render() {
        val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f

        gl { glUniform2f(resolutionLocation.location, 1f, 1f) }
        gl { glUniform1f(timeLocation.location, thisTime) }

        instance.bindUvCoordTexture(0, uvCoordsLocation!!)
        instance.bindUniforms()

        gl { glViewport(0, 0, pixelCount.bufWidth, pixelCount.bufHeight) }
        gl { glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        quad.render()

        gl { gl.finish() }

        val programLog = program.getInfoLog(ctx)
        if (programLog.isNotEmpty()) println("ProgramInfoLog: $programLog")
    }

    private fun createShaderProgram(): ProgramResource {
        // Create a simple shader program
        val program = ProgramResource.create(ctx)

        val vertexShader = ShaderResource.createVertexShader(ctx)
        vertexShader.shaderSource("""#version 300 es

precision lowp float;

// xy = vertex position in normalized device coordinates ([-1,+1] range).
in vec2 Vertex;

const vec2 scale = vec2(0.5, 0.5);

void main()
{
    vec2 vTexCoords  = Vertex * scale + scale; // scale vertex attribute to [0,1] range
    gl_Position = vec4(Vertex, 0.0, 1.0);
}
""", ctx)
        compileShader(vertexShader)
        program.attachShader(vertexShader, ctx)

        val fragmentShader = ShaderResource.createFragmentShader(ctx)
        val src = """#version 300 es

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D sm_uvCoords;
uniform float sm_uScale;
uniform float sm_vScale;
uniform float sm_startOfMeasure;
uniform float sm_beat;

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
        fragmentShader.shaderSource(src, ctx)
        compileShader(fragmentShader)
        program.attachShader(fragmentShader, ctx)

        if (!program.link(ctx)) {
            val infoLog = program.getInfoLog(ctx)
            throw RuntimeException("ProgramInfoLog: $infoLog")
        }

        gl { glUseProgram(program) }

        return program
    }

    private fun compileShader(shader: ShaderResource) {
        if (!shader.compile(ctx)) {
            val infoLog = shader.getInfoLog(ctx)
            window.alert(
                "Failed to compile shader: $infoLog\n" +
                        "Version: ${gl.getParameter(WebGLRenderingContext.VERSION)}\n" +
                        "GLSL Version: ${gl.getParameter(WebGLRenderingContext.SHADING_LANGUAGE_VERSION)}\n"
            )
            throw RuntimeException("Failed to compile shader: ${infoLog}")
        }
    }

    fun <T> gl(fn: () -> T): T {
        val result = fn.invoke()
        checkForGlError()
        return result
    }

    override fun <T> withGlContext(fn: () -> T): T = fn()

    companion object {
        val GL = WebGLRenderingContext
        val GL2 = WebGL2RenderingContext

        fun checkForGlError() {
            while (true) {
                val error = glGetError()
                val code = when (error) {
                    WebGLRenderingContext.INVALID_ENUM -> "GL_INVALID_ENUM"
                    WebGLRenderingContext.INVALID_VALUE -> "GL_INVALID_VALUE"
                    WebGLRenderingContext.INVALID_OPERATION -> "GL_INVALID_OPERATION"
                    WebGLRenderingContext.INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
                    WebGL2RenderingContext.FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
                    WebGLRenderingContext.CONTEXT_LOST_WEBGL -> "GL_CONTEXT_LOST_WEBGL"
                    WebGLRenderingContext.OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
                    else -> "unknown error $error"
                }
                if (error != 0) throw RuntimeException("OpenGL Error: $code") else return
            }
        }
    }

    inner class UnifyingAdjustableUniform(
        val adjustableValue: GlslShader.AdjustableValue, val surfaceCount: Int
    ) : AdjustibleUniform {
        val uniformLocation = gl { getUniformLocation(adjustableValue.varName) }
        var buffer: Any? = null

        override fun bind() {
            if (buffer != null) {
                val location = uniformLocation.location

                when (adjustableValue.valueType) {
                    GlslShader.AdjustableValue.Type.INT -> gl { glUniform1i(location, buffer as Int) }
                    GlslShader.AdjustableValue.Type.FLOAT -> gl { glUniform1f(location, buffer as Float) }
                    GlslShader.AdjustableValue.Type.VEC3 -> {
                        val color = buffer as Color
                        gl { glUniform3f(location, color.redF, color.greenF, color.blueF) }
                    }
                }
            }
        }

        // last one wins!
        override fun setValue(surfaceOrdinal: Int, value: Any?) {
            buffer = value
        }
    }

    inner class AwesomerAdjustableUniform(val adjustableValue: GlslShader.AdjustableValue, val surfaceCount: Int) {
        // TODO: we should save these in an array, one for each surface, but let's keep it simple for now.
        val elementCount: Int
            get() = when (adjustableValue.valueType) {
                GlslShader.AdjustableValue.Type.INT -> surfaceCount
                GlslShader.AdjustableValue.Type.FLOAT -> surfaceCount
                GlslShader.AdjustableValue.Type.VEC3 -> surfaceCount * 3
            }

        val internalFormat: Int
            get() = when (adjustableValue.valueType) {
                GlslShader.AdjustableValue.Type.INT -> WebGLRenderingContext.INT
                GlslShader.AdjustableValue.Type.FLOAT -> WebGL2RenderingContext.R32F
                GlslShader.AdjustableValue.Type.VEC3 -> WebGLRenderingContext.RGB
            }

        val buffer: ArrayBufferView = when (adjustableValue.valueType) {
            GlslShader.AdjustableValue.Type.INT -> Uint32Array(elementCount)
            GlslShader.AdjustableValue.Type.FLOAT -> Float32Array(elementCount)
            GlslShader.AdjustableValue.Type.VEC3 -> Float32Array(elementCount)
        }

        val textureIndex = adjustableValueUniformIndices[adjustableValue.ordinal]
        var texture = TextureResource.create(GL_TEXTURE0 + textureIndex,
            TextureProps("", GL_NEAREST, GL_NEAREST), ctx)
        val uniformLocation = gl { glGetUniformLocation(program, adjustableValue.varName) }

        init {
            gl { glActiveTexture(texture.target) }
            gl { glBindTexture(WebGLRenderingContext.TEXTURE_2D, texture) }
            gl { glTexParameteri(
                WebGLRenderingContext.TEXTURE_2D,
                WebGLRenderingContext.TEXTURE_MIN_FILTER,
                WebGLRenderingContext.NEAREST
            ) }
            gl { glTexParameteri(
                WebGLRenderingContext.TEXTURE_2D,
                WebGLRenderingContext.TEXTURE_MAG_FILTER,
                WebGLRenderingContext.NEAREST
            ) }
            gl {
                glTexImage2D(
                    WebGLRenderingContext.TEXTURE_2D, 0,
                    WebGL2RenderingContext.R32F, elementCount, 1, 0,
                    WebGL2RenderingContext.RED,
                    WebGLRenderingContext.FLOAT, null
                )
            }
            gl { glUniform1i(uvCoordsLocation.location, textureIndex) }
        }
    }

    inner class Instance(
        pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int
    ) : GlslRenderer.Instance(pixelCount, uvCoords, surfaceCount) {
        override val adjustableUniforms: Map<Int, AdjustibleUniform> =
            adjustableValues.associate { adjustableValue ->
                adjustableValue.ordinal to UnifyingAdjustableUniform(adjustableValue, surfaceCount)
            }

        private var uvCoordTexture = TextureResource.create(GL_TEXTURE0 + uvCoordTextureIndex,
            TextureProps("", GL_NEAREST, GL_NEAREST), ctx)
        private val frameBuffer = gl { FramebufferResource.create(pixelCount.bufWidth, pixelCount.bufHeight, ctx) }
        private val renderBuffer = gl { RenderbufferResource.create(ctx) }
        val pixelBuffer: Uint8Array = Uint8Array(pixelCount.bufSize * 4)

        private val uvCoordsFloat32 = Float32Array(uvCoords.toTypedArray())

        override fun bindFramebuffer() {
            gl { glBindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, frameBuffer) }

            gl { glBindRenderbuffer(WebGLRenderingContext.RENDERBUFFER, renderBuffer) }
//            console.error("pixel count: $pixelCount (${pixelCount.bufWidth} x ${pixelCount.bufHeight} = ${pixelCount.bufSize})")
            gl { glRenderbufferStorage(
                WebGLRenderingContext.RENDERBUFFER,
                WebGLRenderingContext.RGBA4, pixelCount.bufWidth, pixelCount.bufHeight) }
            gl { glFramebufferRenderbuffer(
                WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.COLOR_ATTACHMENT0,
                WebGLRenderingContext.RENDERBUFFER, renderBuffer) }

            val status = gl { gl.checkFramebufferStatus(WebGLRenderingContext.FRAMEBUFFER) }
            if (status != WebGLRenderingContext.FRAMEBUFFER_COMPLETE) {
                throw RuntimeException("FrameBuffer huh? $status")
            }
        }

        override fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: Uniform) {
            gl { glActiveTexture(uvCoordTexture.target) }
            gl { glBindTexture(WebGLRenderingContext.TEXTURE_2D, uvCoordTexture) }
            gl { glTexParameteri(
                WebGLRenderingContext.TEXTURE_2D,
                WebGLRenderingContext.TEXTURE_MIN_FILTER,
                WebGLRenderingContext.NEAREST
            ) }
            gl { glTexParameteri(
                WebGLRenderingContext.TEXTURE_2D,
                WebGLRenderingContext.TEXTURE_MAG_FILTER,
                WebGLRenderingContext.NEAREST
            ) }
            gl {
                gl.texImage2D(
                    WebGLRenderingContext.TEXTURE_2D, 0,
                    WebGL2RenderingContext.R32F, pixelCount.bufWidth * 2, pixelCount.bufHeight, 0,
                    WebGL2RenderingContext.RED,
                    WebGLRenderingContext.FLOAT, uvCoordsFloat32
                )
            }
            gl { gl.uniform1i(uvCoordsLocation.location, textureIndex) }
        }

        override fun getPixel(pixelIndex: Int): Color {
            val offset = pixelIndex * 4
            return Color(
                red = pixelBuffer[offset],
                green = pixelBuffer[offset + 1],
                blue = pixelBuffer[offset + 2],
                alpha = pixelBuffer[offset + 3]
            )
        }

        override fun copyToPixelBuffer() {
            gl {
                gl.readPixels(
                    0,
                    0,
                    pixelCount.bufWidth,
                    pixelCount.bufHeight,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.UNSIGNED_BYTE,
                    pixelBuffer
                )
            }
        }

        override fun release() {
            println("Release $this with $pixelCount pixels and ${uvCoords.size} uvs")

            gl { glBindRenderbuffer(WebGLRenderingContext.RENDERBUFFER, null) }
            gl { glBindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, null) }
            gl { glBindTexture(WebGLRenderingContext.TEXTURE_2D, null) }

            gl { glDeleteFramebuffer(frameBuffer) }
            gl { glDeleteRenderbuffer(renderBuffer) }
            gl { glDeleteTexture(uvCoordTexture) }
        }
    }

    private val Uniform?.location: WebGLUniformLocation? get() = this?.locationInternal as WebGLUniformLocation?
}

external abstract class WebGL2RenderingContext : WebGLRenderingContext {
    companion object {
        val RED: Int = definedExternally
        val FRAMEBUFFER_INCOMPLETE_ATTACHMENT: Int = definedExternally
        val R32F: Int = definedExternally
        val RG32F: Int = definedExternally
        val DEPTH_COMPONENT32F: Int = definedExternally
    }
}
