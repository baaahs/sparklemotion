package baaahs.glsl

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.SheepModel
import baaahs.geom.Vector2F
import baaahs.shaders.GlslShader
import baaahs.shows.GlslOtherShow
import baaahs.timeSync
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memAddress
import java.nio.ByteBuffer
import kotlin.random.Random

fun main() {
    // On a Mac, it's necessary to start the JVM with this arg: `-XstartOnFirstThread`
    val renderer = GlslBase.manager.createRenderer(GlslOtherShow.program, emptyList()) as JvmGlslRenderer

    renderer.addSurface(
        IdentifiedSurface(
            SheepModel.Panel("Panel"),
            600,
            List(600) { Vector2F(Random.nextFloat(), Random.nextFloat()) }),
        PanelSpaceUvTranslator
    )

    renderer.runStandalone()
}

actual object GlslBase {
    actual val manager: GlslManager by lazy { JvmGlslManager() }
}

class JvmGlslManager : GlslManager {
    private val window: Long

    /**
     * This is initialization stuff that's required on the main thread.
     */
    init {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SAMPLES, 8)
        glfwWindowHint(GLFW_CONTEXT_RELEASE_BEHAVIOR, GLFW_RELEASE_BEHAVIOR_NONE)

        window = glfwCreateWindow(300, 300, "Hello shaders!", NULL, NULL)
        if (window == NULL)
            throw RuntimeException("Failed to create the GLFW window")

        glfwPollEvents() // Get the event loop warmed up.
    }

    override fun createRenderer(
        fragShader: String,
        adjustableValues: List<GlslShader.AdjustableValue>
    ): GlslRenderer {
        println("Creating JvmGlslManager for $fragShader!")
        return JvmGlslRenderer(fragShader, adjustableValues, window)
    }
}

class JvmGlslRenderer(
    fragShader: String,
    adjustableValues: List<GlslShader.AdjustableValue>,
    private var window: Long
) : GlslRenderer(fragShader, adjustableValues) {
    internal lateinit var keyCallback: GLFWKeyCallback
    internal lateinit var fbCallback: GLFWFramebufferSizeCallback

    internal var width = 300
    internal var height = 300
    internal var lock = Any()
    internal var destroyed: Boolean = false

    internal var viewProjMatrix = Matrix4f()
        .setOrtho(-0.5f, 0.5f, -.5f, .5f, 1f, 10f)
        .lookAt(
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        )

    internal var fb = BufferUtils.createFloatBuffer(16)

    private var vaoId: Int = 0
    private var vboId: Int = 0

    private var program: Int = 0
    private val quad: Quad

    val outputToFrameBuffer = true

    init {
        glfwSetKeyCallback(window, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true)
            }
        })

        glfwSetFramebufferSizeCallback(window, object : GLFWFramebufferSizeCallback() {
            override fun invoke(window: Long, w: Int, h: Int) {
                if (outputToFrameBuffer) {
                    width = pixelCount.bufWidth
                    height = pixelCount.bufHeight
                } else if (w > 0 && h > 0) {
                    width = w
                    height = h
                }
            }
        })

        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        glfwSetWindowPos(window, (vidmode!!.width() - width) / 2, (vidmode.height() - height) / 2)
        if (!outputToFrameBuffer) {
            glfwShowWindow(window)
        }

        val framebufferSize = BufferUtils.createIntBuffer(2)
        nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4)
        width = framebufferSize.get(0)
        height = framebufferSize.get(1)

        withGlContext {
            glfwSwapInterval(0)

//            GlobalScope.launch {
//                while (true) {
//                    glfwPollEvents()
//                    delay(10)
//                }
//            }

            gl { glClearColor(0f, 0f, 0f, 1f) }
//            gl { glEnable(GL_DEPTH_TEST) }
//            gl { glEnable(GL_CULL_FACE) }

            program = createShaderProgram()
            findUniforms()
            matLocation = getUniformLocation("viewProjMatrix")

            instance = createInstance(1, FloatArray(2), nextSurfaceOffset)
        }

        quad = withGlContext { Quad() }
    }

    override fun getUniformLocation(name: String): Uniform {
        val loc = gl { glGetUniformLocation(program, name) }
        if (loc < 0) {
            throw IllegalStateException("Couldn't find uniform $name")
        }
        return Uniform(loc)
    }

    inner class Quad {
        // OpenGL expects vertices to be defined counter clockwise by default
        val vertices = floatArrayOf(
            // Left bottom triangle
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            // Right top triangle
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f
        )

        init {
            // Sending data to OpenGL requires the usage of (flipped) byte buffers
            val verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
            verticesBuffer.put(vertices)
            verticesBuffer.flip()

            // Create a new Vertex Array Object in memory and select it (bind)
            // A VAO can have up to 16 attributes (VBO's) assigned to it by default
            vaoId = gl { GL30.glGenVertexArrays() }
            gl { GL30.glBindVertexArray(vaoId) }

            // Create a new Vertex Buffer Object in memory and select it (bind)
            // A VBO is a collection of Vectors which in this case resemble the location of each vertex.
            vboId = gl { GL15.glGenBuffers() }
            gl { GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId) }
            gl { GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW) }
            val vertexAttr = gl { glGetAttribLocation(program, "Vertex") }
            gl { GL20.glVertexAttribPointer(vertexAttr, 3, GL11.GL_FLOAT, false, 0, 0) }
            // Deselect (bind to 0) the VBO
            gl { GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0) }

            // Deselect (bind to 0) the VAO
            gl { GL30.glBindVertexArray(0) }
        }

        internal fun render() {
//        gl { glPolygonMode(GL_FRONT, GL_FILL) }

            // Bind to the VAO that has all the information about the quad vertices
            gl { GL30.glBindVertexArray(vaoId) }
            gl { GL20.glEnableVertexAttribArray(0) }

            // Draw the vertices
            gl { GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices.size / 3) }

            // Put everything back to default (deselect)
            gl { GL20.glDisableVertexAttribArray(0) }
            gl { GL30.glBindVertexArray(0) }
        }

        internal fun release() {
            // Disable the VBO index from the VAO attributes list
            gl { GL20.glDisableVertexAttribArray(0) }

            // Delete the VBO
            gl { GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0) }
            gl { GL15.glDeleteBuffers(vboId) }

            // Delete the VAO
            gl { GL30.glBindVertexArray(0) }
            gl { GL30.glDeleteVertexArrays(vaoId) }
        }
    }

    override fun createSurfacePixels(surface: IdentifiedSurface, pixelOffset: Int): baaahs.glsl.SurfacePixels =
        SurfacePixels(surface, pixelOffset)

    override fun createInstance(pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int): GlslRenderer.Instance =
        Instance(pixelCount, uvCoords, surfaceCount)

    inner class SurfacePixels(
        surface: IdentifiedSurface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = instance.getPixel(pixel0Index + i)
    }

    fun runStandalone() {
        try {
            /* Spawn a new thread which to make the OpenGL context current in and which does the rendering. */
            Thread(Runnable {
                withGlContext {
                    glfwSwapInterval(0)

                    while (!destroyed) {
                        render()

                        synchronized(lock) {
                            if (!destroyed) {
                                gl { glfwSwapBuffers(window) }
                            }
                        }

                    }

                    quad.release()
                    instance.release()
                }
            }).start()

            /* Process window messages in the main thread */
            while (!glfwWindowShouldClose(window)) {
                glfwWaitEvents()
            }

            synchronized(lock) {
                destroyed = true
                glfwDestroyWindow(window)
            }
            keyCallback.free()
            fbCallback.free()
        } finally {
            glfwTerminate()
        }
    }

    private fun createShaderProgram(): Int {
        // Create a simple shader program
        val program = gl { glCreateProgram() }
        val vs = gl { glCreateShader(GL_VERTEX_SHADER) }
        glShaderSource(
            vs,
            """#version 330 core

in vec4 Vertex;
uniform mat4 viewProjMatrix;

void main(void) {
    gl_Position = viewProjMatrix * Vertex;
}
"""
        )
        compileShader(vs)

        gl { glAttachShader(program, vs) }
        val fs = gl { glCreateShader(GL_FRAGMENT_SHADER) }
        val src = """
    #version 330
    uniform sampler2D sm_uvCoords;

    out vec4 sm_fragColor;
    
    ${fragShader
            .replace(
                Regex("void main\\s*\\(\\s*void\\s*\\)"),
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
            texelFetch(sm_uvCoords, ivec2(uvX * 2, uvY), 0).r,    // u
            texelFetch(sm_uvCoords, ivec2(uvX * 2 + 1, uvY), 0).r // v
        );
    
        sm_main(pixelCoord);
    }
    """
        println(src)
        gl { glShaderSource(fs, src) }

        compileShader(fs)

        gl { glAttachShader(program, fs) }
        gl { glLinkProgram(program) }
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw RuntimeException("ProgramInfoLog: ${glGetProgramInfoLog(program)}")
        }

        gl { glUseProgram(program) }
        return program
    }

    override fun draw() {
        withGlContext {
            val addSurfacesMs = timeSync { incorporateNewSurfaces() }

            val bindFbMs = timeSync { instance.bindFramebuffer() }
            val renderMs = timeSync { render() }

            val readPxMs = timeSync {
                instance.copyToPixelBuffer()
            }

            gl { GL11.glFinish() }
            gl { glfwSwapBuffers(window) }

//            println("Render of $pixelCount took: " +
//                    "addSurface=${addSurfacesMs}ms " +
//                    "bindFbMs=${bindFbMs}ms " +
//                    "renderMs=${renderMs}ms " +
//                    "readPxMs=${readPxMs}ms " +
//                    "$this")
        }
    }

    private fun render() {
        val thisTime = (System.currentTimeMillis() and 0x7ffffff).toFloat() / 1000.0f

        // Upload the matrix stored in the FloatBuffer to the
        // shader uniform.
        gl { glUniformMatrix4fv(matLocation.location, false, viewProjMatrix.get(fb)) }
        gl { glUniform2f(resolutionLocation.location, 1f, 1f) }
        gl { glUniform1f(timeLocation.location, thisTime) }

        instance.bindUvCoordTexture(uvCoordTextureIndex, uvCoordsLocation!!)

        instance.bindUniforms()

        if (outputToFrameBuffer) {
            gl { glViewport(0, 0, pixelCount.bufWidth, pixelCount.bufHeight) }
        } else {
            gl { glViewport(0, 0, width, height) }
        }
        gl { glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        quad.render()

        gl { GL11.glFinish() }

        val programLog = gl { glGetProgramInfoLog(program) }
        if (programLog.isNotEmpty()) println("ProgramInfoLog: $programLog")
    }

    override fun <T> withGlContext(fn: () -> T): T {
        glfwMakeContextCurrent(window)
        glCapabilities.get() // because it's expensive and only has to happen once per thread

        try {
            return fn()
        } finally {
            glfwMakeContextCurrent(0)
        }
    }

    companion object {
        val glCapabilities = ThreadLocal.withInitial { GL.createCapabilities() }

        fun <T> gl(fn: () -> T): T {
            val result = fn.invoke()
            checkForGlError()
            return result
        }

        fun checkForGlError() {
            while (true) {
                val error = GL11.glGetError()
                val code = when (error) {
                    GL_INVALID_ENUM -> "GL_INVALID_ENUM"
                    GL_INVALID_VALUE -> "GL_INVALID_VALUE"
                    GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
                    GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW"
                    GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW"
                    GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
                    else -> "unknown error $error"
                }
                if (error != 0) throw RuntimeException("OpenGL Error: $code") else return
            }
        }
    }

    private fun compileShader(shader: Int) {
        gl { glCompileShader(shader) }

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw RuntimeException("Failed to compile shader: ${glGetShaderInfoLog(shader)}")
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

    inner class Instance(
        pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int
    ) : GlslRenderer.Instance(pixelCount, uvCoords, surfaceCount) {
        override val adjustableUniforms: List<AdjustibleUniform> =
            adjustableValues.map { adjustableValue -> UnifyingAdjustableUniform(adjustableValue, surfaceCount) }

        private var uvCoordTexture: Int = gl { GL11.glGenTextures() }
        private var frameBuffer: Int = gl { GL30.glGenFramebuffers() }
        private var renderBuffer: Int = gl { GL30.glGenRenderbuffers() }
        val pixelBuffer: ByteBuffer = ByteBuffer.allocateDirect(pixelCount.bufSize * 4)

        init {
            println("Created new $this with $pixelCount pixels and ${uvCoords.size} uvs")
        }

        override fun bindFramebuffer() {
            gl { GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer) }

            gl { GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer) }
            gl { GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL_RGBA8, pixelCount.bufWidth, pixelCount.bufHeight) }
            gl {
                GL30.glFramebufferRenderbuffer(
                    GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                    GL30.GL_RENDERBUFFER, renderBuffer
                )
            }

            val status = gl { glCheckFramebufferStatus(GL_FRAMEBUFFER) }
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                java.lang.RuntimeException("FrameBuffer huh? $status").printStackTrace()
            }
        }

        override fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: Uniform) {
            gl { glActiveTexture(GL_TEXTURE0 + textureIndex) }
            gl { glBindTexture(GL_TEXTURE_2D, uvCoordTexture) }
            gl { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0) }
            gl { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0) }
            gl { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
            gl { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
            gl {
                GL11.glTexImage2D(
                    GL_TEXTURE_2D, 0, GL_R32F, pixelCount.bufWidth * 2, pixelCount.bufHeight, 0,
                    GL_RED, GL_FLOAT, uvCoords
                )
            }
            gl { glUniform1i(uvCoordsLocation.location, textureIndex) }

            gl { glBindBuffer(GL_TEXTURE_BUFFER, 0) }
        }

        override fun getPixel(pixelIndex: Int): Color {
            val offset = pixelIndex * 4
            return Color(
                red = pixelBuffer[offset + 3],
                green = pixelBuffer[offset + 2],
                blue = pixelBuffer[offset + 1],
                alpha = pixelBuffer[offset]
            )
        }

        override fun copyToPixelBuffer() {
            pixelBuffer.position(0)
            GL11.glReadPixels(
                0,
                0,
                pixelCount.bufWidth,
                pixelCount.bufHeight,
                GL_RGBA,
                GL_UNSIGNED_INT_8_8_8_8,
                pixelBuffer
            )

        }

        override fun release() {
            println("Release $this with $pixelCount pixels and ${uvCoords.size} uvs")

            gl { GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0) }
            gl { GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0) }
            gl { glBindBuffer(GL_TEXTURE_BUFFER, 0) }
            gl { glBindTexture(GL_TEXTURE_1D, 0) }

            gl { GL30.glDeleteFramebuffers(frameBuffer) }
            gl { GL30.glDeleteRenderbuffers(renderBuffer) }
            gl { GL15.glDeleteTextures(uvCoordTexture) }
        }
    }

    private val Uniform?.location: Int get() = this?.locationInternal as Int
}