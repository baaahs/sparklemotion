package baaahs.glsl

import baaahs.*
import baaahs.shaders.GlslShader
import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.gl.*
import de.fabmax.kool.shading.Uniform1f
import de.fabmax.kool.shading.Uniform2f
import de.fabmax.kool.shading.UniformTexture2D
import de.fabmax.kool.util.createFloat32Buffer
import kotlin.math.max
import kotlin.math.min

class GlslRenderer(
    val ctx: KoolContext,
    val fragShader: String,
    val adjustableValues: List<GlslShader.AdjustableValue>
) {
    private val surfacesToAdd: MutableList<GlslSurface> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextSurfaceOffset: Int = 0

    val glslSurfaces: MutableList<GlslSurface> = mutableListOf()

    val uvCoordTextureIndex = 0
    var surfaceOrdinalTextureIndex = 1
    var nextTextureIndex = 2
    val adjustableValueUniformIndices = adjustableValues.map { nextTextureIndex++ }

    // uniforms
    internal var uvCoordsLocation: UniformTexture2D? = null
    internal var resolutionLocation: Uniform2f? = null
    internal var timeLocation: Uniform1f? = null

    lateinit var instance: Instance

    val program: ProgramResource

    private val quad: Quad

    init {
        program = createShaderProgram()

        gl { glClearColor(0f, .5f, 0f, 1f) }

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
                GL_FLOAT, false, 0, 0) }
            gl { glEnableVertexAttribArray(vertexAttr) }

            gl { glBindBuffer(GL_ARRAY_BUFFER, null) }
        }

        internal fun render() {
            gl { glBindBuffer(GL_ARRAY_BUFFER, quadVertexBuffer) }

            // Draw the triangles
            gl { glDrawArrays(GL_TRIANGLES, 0, 6) }

            gl { glBindBuffer(GL_ARRAY_BUFFER, null) }
        }

        private fun release() {
            gl { glDeleteBuffer(quadVertexBuffer) }
        }
    }

    fun createShaderProgram(): ProgramResource {
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

    fun <T> gl(fn: () -> T): T {
        val result = fn.invoke()
        checkForGlError()
        return result
    }

    companion object {

        fun checkForGlError() {
            while (true) {
                val error = glGetError()
                val code = when (error) {
                    GL_INVALID_ENUM -> "GL_INVALID_ENUM"
                    GL_INVALID_VALUE -> "GL_INVALID_VALUE"
                    GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
                    GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
                    GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
//                    GL_CONTEXT_LOST_WEBGL -> "GL_CONTEXT_LOST_WEBGL"
                    GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
                    else -> "unknown error $error"
                }
                if (error != 0) throw RuntimeException("OpenGL Error: $code") else return
            }
        }
    }


    private fun compileShader(shader: ShaderResource) {
        if (!shader.compile(ctx)) {
            val infoLog = shader.getInfoLog(ctx)
            println(
                "Failed to compile shader: $infoLog\n" +
                        "Version: dunno\n" +
                        "GLSL Version: dunno\n"
            )
//            window.alert(
//                "Failed to compile shader: $infoLog\n" +
//                        "Version: ${gl.getParameter(GL_VERSION)}\n" +
//                        "GLSL Version: ${gl.getParameter(GL_SHADING_LANGUAGE_VERSION)}\n"
//            )
            throw RuntimeException("Failed to compile shader: ${infoLog}")
        }
    }



    fun findUniforms() {
        uvCoordsLocation = UniformTexture2D("sm_uvCoords")
        resolutionLocation = Uniform2f("resolution")
        timeLocation = Uniform1f("time")
    }

    fun addSurface(surface: Surface, uvTranslator: UvTranslator): GlslSurface? {
        val glslSurface: GlslSurface
        if (surface is IdentifiedSurface) {
            if (surface.pixelLocations != null) {
                glslSurface = GlslSurface(
                    createSurfacePixels(surface, nextPixelOffset),
                    Uniforms(nextSurfaceOffset++),
                    uvTranslator
                )
                nextPixelOffset += surface.pixelCount
            } else {
                glslSurface = GlslSurface(
                    createSurfaceMonoPixel(surface, nextPixelOffset),
                    Uniforms(nextSurfaceOffset++),
                    uvTranslator
                )
                nextPixelOffset += 1
            }
        } else {
            glslSurface = GlslSurface(
                createSurfaceMonoPixel(surface, nextPixelOffset),
                Uniforms(nextSurfaceOffset++),
                uvTranslator
            )
            nextPixelOffset += 1
        }

        surfacesToAdd.add(glslSurface)
        return glslSurface
    }
    fun createSurfacePixels(surface: Surface, pixelOffset: Int): baaahs.glsl.SurfacePixels =
        SurfacePixels(surface, pixelOffset)

    inner class SurfacePixels(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = instance.getPixel(pixel0Index + i)
    }

    fun createSurfaceMonoPixel(surface: Surface, pixelOffset: Int): baaahs.glsl.SurfacePixels =
        SurfaceMonoPixel(surface, pixelOffset)

    inner class SurfaceMonoPixel(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = instance.getPixel(pixel0Index)
    }

    fun createInstance(pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int): GlslRenderer.Instance =
        Instance(pixelCount, uvCoords, surfaceCount)

    fun draw() {
        withGlContext {
            val addSurfacesMs = timeSync { incorporateNewSurfaces() }

            val bindFbMs = timeSync { instance.bindFramebuffer() }
            val renderMs = timeSync { render() }

            val readPxMs = timeSync {
                instance.copyToPixelBuffer()
            }

            gl { glFinish() }
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

        gl { glUniform2f(resolutionLocation?.location, 1f, 1f) }
        gl { glUniform1f(timeLocation?.location, thisTime) }

        instance.bindUvCoordTexture(0, uvCoordsLocation!!)
        instance.bindUniforms()

        gl { glViewport(0, 0, pixelCount.bufWidth, pixelCount.bufHeight) }
        gl { glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        quad.render()

        gl { glFinish() }

        val programLog = program.getInfoLog(ctx)
        if (programLog.isNotEmpty()) println("ProgramInfoLog: $programLog")
    }

    fun <T> withGlContext(fn: () -> T): T = fn()

    protected fun incorporateNewSurfaces() {
        if (surfacesToAdd.isNotEmpty()) {
            val oldUvCoords = instance.uvCoords
            val newPixelCount = nextPixelOffset

            withGlContext {
                instance.release()
            }

            val newUvCoords = FloatArray(newPixelCount.bufSize * 2)
            oldUvCoords.copyInto(newUvCoords)

            surfacesToAdd.forEach {
                val surface = it.pixels.surface
                val uvTranslator = it.uvTranslator.forSurface(surface)

                for (i in 0 until uvTranslator.pixelCount) {
                    val uvOffset = (it.pixels.pixel0Index + i) * 2
                    val (u, v) = uvTranslator.getUV(i)
                    newUvCoords[uvOffset] = u     // u
                    newUvCoords[uvOffset + 1] = v // v
                }
            }

            withGlContext {
                instance = createInstance(newPixelCount, newUvCoords, nextSurfaceOffset)
                instance.bindUvCoordTexture(uvCoordTextureIndex, uvCoordsLocation!!)
            }

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")

            glslSurfaces.addAll(surfacesToAdd)
            surfacesToAdd.clear()
        }
    }

    interface AdjustibleUniform {
        fun bind()
        fun setValue(surfaceOrdinal: Int, value: Any?)
    }

    inner class Instance(val pixelCount: Int, val uvCoords: FloatArray, val surfaceCount: Int) {
        val adjustableUniforms: Map<Int, AdjustibleUniform> =
            adjustableValues.associate { adjustableValue ->
                adjustableValue.ordinal to UnifyingAdjustableUniform(adjustableValue, surfaceCount)
            }
        private var uvCoordTexture = TextureResource.create(GL_TEXTURE0 + uvCoordTextureIndex,
            TextureProps("", GL_NEAREST, GL_NEAREST), ctx)
        private val frameBuffer = gl { FramebufferResource.create(pixelCount.bufWidth, pixelCount.bufHeight, ctx) }
        private val renderBuffer = gl { RenderbufferResource.create(ctx) }
        val pixelBuffer: ByteArray = ByteArray(pixelCount.bufSize * 4)
        private val uvCoordsFloat32 = uvCoords.toTypedArray()

        fun bindFramebuffer() {
            gl { glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer) }

            gl { glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer) }
//            console.error("pixel count: $pixelCount (${pixelCount.bufWidth} x ${pixelCount.bufHeight} = ${pixelCount.bufSize})")
            gl { glRenderbufferStorage(
                GL_RENDERBUFFER,
                GL_RGBA4, pixelCount.bufWidth, pixelCount.bufHeight) }
            gl { glFramebufferRenderbuffer(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0,
                GL_RENDERBUFFER, renderBuffer) }

            val status = gl { glCheckFramebufferStatus(GL_FRAMEBUFFER) }
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                throw RuntimeException("FrameBuffer huh? $status")
            }
        }

        fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: UniformTexture2D) {
            gl { glActiveTexture(uvCoordTexture.target) }
            gl { glBindTexture(GL_TEXTURE_2D, uvCoordTexture) }
            gl { glTexParameteri(
                GL_TEXTURE_2D,
                GL_TEXTURE_MIN_FILTER,
                GL_NEAREST
            ) }
            gl { glTexParameteri(
                GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER,
                GL_NEAREST
            ) }
            gl {
                glTexImage2D(
                    GL_TEXTURE_2D, 0,
                    GL_R32F, pixelCount.bufWidth * 2, pixelCount.bufHeight, 0,
                    GL_RED,
                    GL_FLOAT, uvCoordsFloat32
                )
            }
            gl { glUniform1i(uvCoordsLocation.location, textureIndex) }
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
            gl {
                glReadPixels(
                    0,
                    0,
                    pixelCount.bufWidth,
                    pixelCount.bufHeight,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    pixelBuffer
                )
            }
        }

        fun release() {
            println("Release $this with $pixelCount pixels and ${uvCoords.size} uvs")

            gl { glBindRenderbuffer(GL_RENDERBUFFER, null) }
            gl { glBindFramebuffer(GL_FRAMEBUFFER, null) }
            gl { glBindTexture(GL_TEXTURE_2D, null) }

            gl { glDeleteFramebuffer(frameBuffer) }
            gl { glDeleteRenderbuffer(renderBuffer) }
            gl { glDeleteTexture(uvCoordTexture) }
        }


        fun bindUniforms() {
            adjustableUniforms.forEach { (key, value) -> value.bind() }
        }

        fun setUniform(adjustableValue: GlslShader.AdjustableValue, surfaceOrdinal: Int, value: Any?) {
            adjustableUniforms[adjustableValue.ordinal]!!.setValue(surfaceOrdinal, value)
        }
    }

    inner class UnifyingAdjustableUniform(
        val adjustableValue: GlslShader.AdjustableValue, val surfaceCount: Int
    ) : AdjustibleUniform {
        val uniformLocation = gl { adjustableValue.getUniform() }
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

//    inner class AwesomerAdjustableUniform(val adjustableValue: GlslShader.AdjustableValue, val surfaceCount: Int) {
//        // TODO: we should save these in an array, one for each surface, but let's keep it simple for now.
//        val elementCount: Int
//            get() = when (adjustableValue.valueType) {
//                GlslShader.AdjustableValue.Type.INT -> surfaceCount
//                GlslShader.AdjustableValue.Type.FLOAT -> surfaceCount
//                GlslShader.AdjustableValue.Type.VEC3 -> surfaceCount * 3
//            }
//
//        val internalFormat: Int
//            get() = when (adjustableValue.valueType) {
//                GlslShader.AdjustableValue.Type.INT -> GL_INT
//                GlslShader.AdjustableValue.Type.FLOAT -> GL_R32F
//                GlslShader.AdjustableValue.Type.VEC3 -> GL_RGB
//            }
//
//        val buffer: ArrayBufferView = when (adjustableValue.valueType) {
//            GlslShader.AdjustableValue.Type.INT -> Uint32Array(elementCount)
//            GlslShader.AdjustableValue.Type.FLOAT -> Float32Array(elementCount)
//            GlslShader.AdjustableValue.Type.VEC3 -> Float32Array(elementCount)
//        }
//
//        val textureIndex = adjustableValueUniformIndices[adjustableValue.ordinal]
//        var texture = TextureResource.create(GL_TEXTURE0 + textureIndex,
//            TextureProps("", GL_NEAREST, GL_NEAREST), ctx)
//        val uniformLocation = gl { glGetUniformLocation(program, adjustableValue.varName) }
//
//        init {
//            gl { glActiveTexture(texture.target) }
//            gl { glBindTexture(GL_TEXTURE_2D, texture) }
//            gl { glTexParameteri(
//                GL_TEXTURE_2D,
//                GL_TEXTURE_MIN_FILTER,
//                GL_NEAREST
//            ) }
//            gl { glTexParameteri(
//                GL_TEXTURE_2D,
//                GL_TEXTURE_MAG_FILTER,
//                GL_NEAREST
//            ) }
//            gl {
//                glTexImage2D(
//                    GL_TEXTURE_2D, 0,
//                    GL_R32F, elementCount, 1, 0,
//                    GL_RED,
//                    GL_FLOAT, null
//                )
//            }
//
//            gl { glUniform1i(uvCoordsLocation!!.location, textureIndex) }
//        }
//    }

    val Int.bufWidth: Int get() = max(1, min(this, 1024))
    val Int.bufHeight: Int get() = this / 1024 + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Uniforms(internal val surfaceOrdinal: Int) {
        fun updateFrom(values: Array<Any?>) {
            adjustableValues.forEach {
                instance.setUniform(it, surfaceOrdinal, values[it.ordinal])
            }
        }
    }
}