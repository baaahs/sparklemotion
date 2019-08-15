package baaahs.glsl

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.getTimeMillis
import baaahs.shaders.GlslShader
import baaahs.shaders.GlslShader.AdjustableValue.Type.*
import baaahs.timeSync
import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

actual object GlslBase {
    actual val manager: GlslManager by lazy { JsGlslManager() }
}

class JsGlslManager : GlslManager {
    override fun createRenderer(program: String, adjustableValues: List<GlslShader.AdjustableValue>) =
        JsGlslRenderer(program, adjustableValues)
}

public external abstract class WebGL2RenderingContext : WebGLRenderingContext {
    companion object {
        val RED: Int = definedExternally
        val FRAMEBUFFER_INCOMPLETE_ATTACHMENT: Int = definedExternally
        val R32F: Int = definedExternally
        val RG32F: Int = definedExternally
        val DEPTH_COMPONENT32F: Int = definedExternally
    }
}


class JsGlslRenderer(
    fragShader: String,
    adjustableValues: List<GlslShader.AdjustableValue>
) : GlslRenderer(fragShader, adjustableValues) {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    var gl: WebGL2RenderingContext = canvas.getContext("webgl2")!! as WebGL2RenderingContext

    private val program: WebGLProgram?
    private val quad: Quad

    init {
        gl { gl.clearColor(0f, .5f, 0f, 1f) }

        program = createShaderProgram()
        quad = Quad()

        findUniforms()

        instance = createInstance(1, FloatArray(2), nextSurfaceOffset)
    }

    override fun getUniformLocation(name: String, optional: Boolean): Uniform {
        val loc = gl { gl.getUniformLocation(program, name) }
        if (loc == null && !optional)
            throw IllegalStateException("Couldn't find uniform $name")

        return Uniform(loc)
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

        private var quadVertexBuffer: WebGLBuffer? = null

        init {
            quadVertexBuffer = gl.createBuffer()
            gl { gl.bindBuffer(GL.ARRAY_BUFFER, quadVertexBuffer) }
            gl { gl.bufferData(GL.ARRAY_BUFFER, Float32Array(vertices), GL.STATIC_DRAW); }

            val vertexAttr = gl { gl.getAttribLocation(program, "Vertex") }
            gl { gl.vertexAttribPointer(vertexAttr, 2, GL.FLOAT, false, 0, 0) }
            gl { gl.enableVertexAttribArray(vertexAttr) }

            gl { gl.bindBuffer(GL.ARRAY_BUFFER, null) }
        }

        internal fun render() {
            gl { gl.bindBuffer(GL.ARRAY_BUFFER, quadVertexBuffer) }

            // Draw the triangles
            gl { gl.drawArrays(GL.TRIANGLES, 0, 6) }

            gl { gl.bindBuffer(GL.ARRAY_BUFFER, null) }
        }

        private fun release() {
            gl { gl.deleteBuffer(quadVertexBuffer) }
        }
    }

    override fun createSurfacePixels(surface: IdentifiedSurface, pixelOffset: Int): baaahs.glsl.SurfacePixels =
        SurfacePixels(surface, pixelOffset)

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

        gl { gl.uniform2f(resolutionLocation.location, 1f, 1f) }
        gl { gl.uniform1f(timeLocation.location, thisTime) }

        instance.bindUvCoordTexture(uvCoordTextureIndex, uvCoordsLocation!!)
        instance.bindUniforms()

        gl { gl.viewport(0, 0, pixelCount.bufWidth, pixelCount.bufHeight) }
        gl { gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT) }

        quad.render()

        gl { gl.finish() }

        val programLog = gl { gl.getProgramInfoLog(program) }
        if (programLog != null && programLog.isNotEmpty()) println("ProgramInfoLog: $programLog")
    }

    inner class SurfacePixels(
        surface: IdentifiedSurface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = instance.getPixel(pixel0Index + i)
    }

    private fun createShaderProgram(): WebGLProgram? {
        // Create a simple shader program
        val program = gl { gl.createProgram() }
        val vs = gl { gl.createShader(GL.VERTEX_SHADER) }
        gl {
            gl.shaderSource(
                vs,
                """#version 300 es

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
        compileShader(vs)

        gl { gl.attachShader(program, vs) }
        val fs = gl { gl.createShader(GL.FRAGMENT_SHADER) }

        val src = """#version 300 es

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D sm_uvCoords;
uniform float sm_uScale;
uniform float sm_vScale;

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
        gl { gl.shaderSource(fs, src) }

        compileShader(fs)

        gl { gl.attachShader(program, fs) }
        gl { gl.linkProgram(program) }
        if (gl.getProgramParameter(program, GL.LINK_STATUS) == false) {
            throw RuntimeException("ProgramInfoLog: ${gl.getProgramInfoLog(program)}")
        }

        gl { gl.useProgram(program) }
        return program
    }

    private fun compileShader(shader: WebGLShader?) {
        gl { gl.compileShader(shader) }
        if (gl.getShaderParameter(shader, GL.COMPILE_STATUS) == false) {
            window.alert(
                "Failed to compile shader: ${gl.getShaderInfoLog(shader)}\n" +
                        "Version: ${gl.getParameter(GL.VERSION)}\n" +
                        "GLSL Version: ${gl.getParameter(GL.SHADING_LANGUAGE_VERSION)}\n"
            )
            throw RuntimeException("Failed to compile shader: ${gl.getShaderInfoLog(shader)}")
        }
    }

    fun <T> gl(fn: () -> T): T {
        val result = fn.invoke()
        checkForGlError(gl)
        return result
    }

    override fun <T> withGlContext(fn: () -> T): T = fn()

    companion object {
        val GL = WebGLRenderingContext
        val GL2 = WebGL2RenderingContext

        fun checkForGlError(gl: WebGLRenderingContext) {
            while (true) {
                val error = gl.getError()
                val code = when (error) {
                    GL.INVALID_ENUM -> "GL_INVALID_ENUM"
                    GL.INVALID_VALUE -> "GL_INVALID_VALUE"
                    GL.INVALID_OPERATION -> "GL_INVALID_OPERATION"
                    GL.INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
                    GL2.FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
                    GL.CONTEXT_LOST_WEBGL -> "GL_CONTEXT_LOST_WEBGL"
                    GL.OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
                    else -> "unknown error $error"
                }
                if (error != 0) throw RuntimeException("OpenGL Error: $code") else return
            }
        }
    }

    inner class UnifyingAdjustableUniform(
        val adjustableValue: GlslShader.AdjustableValue, val surfaceCount: Int
    ) : AdjustibleUniform {
        val uniformLocation = gl { getUniformLocation(adjustableValue.varName, false) }
        var buffer: Any? = null

        override fun bind() {
            if (buffer != null) {
                val location = uniformLocation.location

                when (adjustableValue.valueType) {
                    INT -> gl { gl.uniform1i(location, buffer as Int) }
                    FLOAT -> gl { gl.uniform1f(location, buffer as Float) }
                    VEC3 -> {
                        val color = buffer as Color
                        gl { gl.uniform3f(location, color.redF, color.greenF, color.blueF) }
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
                INT -> surfaceCount
                FLOAT -> surfaceCount
                VEC3 -> surfaceCount * 3
            }

        val internalFormat: Int
            get() = when (adjustableValue.valueType) {
                INT -> GL.INT
                FLOAT -> GL2.R32F
                VEC3 -> GL.RGB
            }

        val buffer: ArrayBufferView = when (adjustableValue.valueType) {
            INT -> Uint32Array(elementCount)
            FLOAT -> Float32Array(elementCount)
            VEC3 -> Float32Array(elementCount)
        }

        var texture = gl { gl.createTexture() }
        val textureIndex = adjustableValueUniformIndices[adjustableValue.ordinal]
        val uniformLocation = gl { gl.getUniformLocation(program, adjustableValue.varName) }

        init {
            gl { gl.activeTexture(GL.TEXTURE0 + textureIndex) }
            gl { gl.bindTexture(GL.TEXTURE_2D, texture) }
            gl { gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST) }
            gl { gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST) }
            gl {
                gl.texImage2D(
                    GL.TEXTURE_2D, 0, GL2.R32F, elementCount, 1, 0,
                    GL2.RED, GL.FLOAT, null
                )
            }
            gl { gl.uniform1i(uvCoordsLocation.location, textureIndex) }
        }
    }

    inner class Instance(
        pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int
    ) : GlslRenderer.Instance(pixelCount, uvCoords, surfaceCount) {
        override val adjustableUniforms: List<AdjustibleUniform> =
            adjustableValues.map { adjustableValue -> UnifyingAdjustableUniform(adjustableValue, surfaceCount) }

        private var uvCoordTexture = gl { gl.createTexture() }
        private val frameBuffer = gl { gl.createFramebuffer() }
        private val renderBuffer = gl { gl.createRenderbuffer() }
        val pixelBuffer: Uint8Array = Uint8Array(pixelCount.bufSize * 4)

        private val uvCoordsFloat32 = Float32Array(uvCoords.toTypedArray())

        override fun bindFramebuffer() {
            gl { gl.bindFramebuffer(GL.FRAMEBUFFER, frameBuffer) }

            gl { gl.bindRenderbuffer(GL.RENDERBUFFER, renderBuffer) }
//            console.error("pixel count: $pixelCount (${pixelCount.bufWidth} x ${pixelCount.bufHeight} = ${pixelCount.bufSize})")
            gl { gl.renderbufferStorage(GL.RENDERBUFFER, GL.RGBA4, pixelCount.bufWidth, pixelCount.bufHeight) }
            gl { gl.framebufferRenderbuffer(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.RENDERBUFFER, renderBuffer) }

            val status = gl { gl.checkFramebufferStatus(GL.FRAMEBUFFER) }
            if (status != GL.FRAMEBUFFER_COMPLETE) {
                throw RuntimeException("FrameBuffer huh? $status")
            }
        }

        override fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: Uniform) {
            gl { gl.activeTexture(GL.TEXTURE0 + textureIndex) }
            gl { gl.bindTexture(GL.TEXTURE_2D, uvCoordTexture) }
            gl { gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST) }
            gl { gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST) }
            gl {
                gl.texImage2D(
                    GL.TEXTURE_2D, 0, GL2.R32F, pixelCount.bufWidth * 2, pixelCount.bufHeight, 0,
                    GL2.RED, GL.FLOAT, uvCoordsFloat32
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
                    GL.RGBA,
                    GL.UNSIGNED_BYTE,
                    pixelBuffer
                )
            }
        }

        override fun release() {
            println("Release $this with $pixelCount pixels and ${uvCoords.size} uvs")

            gl { gl.bindRenderbuffer(GL.RENDERBUFFER, null) }
            gl { gl.bindFramebuffer(GL.FRAMEBUFFER, null) }
            gl { gl.bindTexture(GL.TEXTURE_2D, null) }

            gl { gl.deleteFramebuffer(frameBuffer) }
            gl { gl.deleteRenderbuffer(renderBuffer) }
            gl { gl.deleteTexture(uvCoordTexture) }
        }
    }

    private val Uniform?.location: WebGLUniformLocation? get() = this?.locationInternal as WebGLUniformLocation?
}
