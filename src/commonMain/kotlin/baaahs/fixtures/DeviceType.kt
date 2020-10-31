package baaahs.fixtures

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.FixtureRenderPlan
import com.danielgergely.kgl.*
import kotlin.math.min

interface DeviceType {
    val params: List<Param>
    val resultParams: List<ResultParam>

    fun setFixtureParamUniforms(fixtureRenderPlan: FixtureRenderPlan, paramBuffers: List<ParamBuffer>)
    fun initPixelParams(fixtureRenderPlan: FixtureRenderPlan, paramBuffers: List<ParamBuffer>)
}

interface ParamBuffer {
    fun resizeBuffer(width: Int, height: Int)
    fun resizeTexture(width: Int, height: Int)
    fun bind(glslProgram: GlslProgram): GlslProgram.Binding
    fun release()
}

class FloatsParamBuffer(val id: String, val stride: Int, private val gl: GlContext) : ParamBuffer {
    private val textureUnit = gl.getTextureUnit(this)
    private val texture = gl.check { createTexture() }
    var floats = FloatArray(0)

    override fun resizeBuffer(width: Int, height: Int) {
        val size = width * height

        val newFloats = FloatArray(size * stride)
        floats.copyInto(newFloats, 0, 0, min(floats.size, size * stride))
        floats = newFloats
    }

    override fun resizeTexture(width: Int, height: Int) {
        with(textureUnit) {
            bindTexture(texture)
            configure(GL_NEAREST, GL_NEAREST)

            // Stride currently has to be 3.
            uploadTexture(
                0,
                GlContext.GL_RGB32F, width, height, 0,
                GL_RGB,
                GL_FLOAT, FloatBuffer(floats)
            )
        }
    }

    override fun bind(glslProgram: GlslProgram): GlslProgram.Binding {
        val uniform = glslProgram.getUniform(id)

        return object : GlslProgram.Binding {
            override val dataFeed: GlslProgram.DataFeed? get() = null
            override val isValid get() = uniform != null

            override fun setOnProgram() {
                if (uniform != null) {
                    textureUnit.bindTexture(texture)
                    uniform.set(textureUnit)
                }
            }
        }
    }

    override fun release() {
        gl.check { deleteTexture(texture) }
    }
}

interface Param {
    val id: String
    val title: String
    fun allocate(gl: GlContext, index: Int): ParamBuffer
}

class FloatsPixelParam(
    override val id: String,
    override val title: String,
    val stride: Int
) : Param {
    override fun allocate(gl: GlContext, index: Int): ParamBuffer {
        return FloatsParamBuffer(id, stride, gl)
    }
}

class ResultParam(val title: String, val type: ResultType) {
    fun allocate(gl: GlContext, index: Int): ResultBuffer {
        return type.createParamBuffer(gl, index)
    }
}

abstract class ResultBuffer(
    gl: GlContext,
    private val paramIndex: Int,
    val type: ResultType
) {
    private var curWidth = 0
    private var curHeight = 0
    private var cpuBufferSize = 0

    private val gpuBuffer = gl.createRenderBuffer()
    abstract val cpuBuffer: Buffer

    // Storage smaller than 16x1 causes a GL error.
    init {
        resize(16, 1)
    }

    fun resize(width: Int, height: Int) {
        gpuBuffer.storage(type.renderPixelFormat, width, height)
        curWidth = width
        curHeight = height

        val bufferSize = width * height
        if (cpuBufferSize != bufferSize) {
            resizeBuffer(bufferSize)
            cpuBufferSize = bufferSize
        }
    }

    abstract fun resizeBuffer(size: Int)

    fun attachTo(fb: GlContext.FrameBuffer) {
        fb.attach(gpuBuffer, GL_COLOR_ATTACHMENT0 + paramIndex)
    }

    fun afterFrame(frameBuffer: GlContext.FrameBuffer) {
        frameBuffer.withRenderBufferAsAttachment0(gpuBuffer) {
            gpuBuffer.readPixels(
                0, 0, gpuBuffer.curWidth, gpuBuffer.curHeight,
                type.readPixelFormat, type.readType, cpuBuffer
            )
        }
    }

    fun release() {
        gpuBuffer.release()
    }
}