package baaahs.fixtures

import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import com.danielgergely.kgl.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.min

interface DeviceType {
    val id: String
    val title: String
    val dataSourceBuilders: List<DataSourceBuilder<*>>
    val resultParams: List<ResultParam>
    val resultContentType: ContentType
    val errorIndicatorShader: Shader

    class Serializer(private val knownDeviceTypes: Map<String, DeviceType>) : KSerializer<DeviceType> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: DeviceType) {
            encoder.encodeString(value.id)
        }

        override fun deserialize(decoder: Decoder): DeviceType {
            return knownDeviceTypes.getBang(decoder.decodeString(), "device type")
        }
    }
}

interface ParamBuffer {
    fun resizeBuffer(width: Int, height: Int)
    fun uploadToTexture()
    fun setTexture(uniform: Uniform)
    fun bind(glslProgram: GlslProgram): ProgramFeed
    fun release()
}

class FloatsParamBuffer(val id: String, val stride: Int, private val gl: GlContext) : ParamBuffer {
    private val textureUnit = gl.getTextureUnit(this)
    private val texture = gl.check { createTexture() }
    private var floats = FloatArray(0)
    private var width = 0
    private var height = 0

    override fun resizeBuffer(width: Int, height: Int) {
        val size = width * height

        val newFloats = FloatArray(size * stride)
        floats.copyInto(newFloats, 0, 0, min(floats.size, size * stride))
        floats = newFloats

        this.width = width
        this.height = height
    }

    override fun uploadToTexture() {
        with(textureUnit) {
            bindTexture(texture)
            configure(GL_NEAREST, GL_NEAREST)

            val format = when(stride) {
                1 -> GL_RED
                2 -> GL_RG
                3 -> GL_RGB
                4 -> GL_RGBA
                else -> error("Stride currently has to be between 1 and 4.")
            }

            uploadTexture(
                0,
                GlContext.GL_RGB32F, width, height, 0,
                format,
                GL_FLOAT, FloatBuffer(floats)
            )
        }
    }

    override fun setTexture(uniform: Uniform) {
        uniform.set(textureUnit)
    }

    override fun bind(glslProgram: GlslProgram): ProgramFeed {
        val uniform = glslProgram.getUniform(id)

        return object : ProgramFeed {
            override val isValid get() = uniform != null

            override fun setOnProgram() {
                if (uniform != null) {
                    textureUnit.bindTexture(texture)
                    uniform.set(textureUnit)
                }
            }
        }
    }

    fun scoped(
        renderTarget: RenderTarget,
        callback: ((Int) -> Float)? = null
    ) = object : BufferView<Float> {
        val offset = renderTarget.pixel0Index
        val size = renderTarget.pixelCount

        override fun set(pixelIndex: Int, t: Float) = set(pixelIndex, 0, t)

        override fun set(pixelIndex: Int, index: Int, t: Float) {
            if (pixelIndex > size) throw IndexOutOfBoundsException("$pixelIndex > $size")
            floats[(offset + pixelIndex) * stride + index] = t
        }

        override fun get(pixelIndex: Int): Float = get(pixelIndex, 0)

        override fun get(pixelIndex: Int, index: Int): Float {
            if (pixelIndex > size) throw IndexOutOfBoundsException("$pixelIndex > $size")
            return floats[(offset + pixelIndex) * stride]
        }
    }.also {
        if (callback != null) {
            val offset = renderTarget.pixel0Index
            for (pixelIndex in 0 until renderTarget.pixelCount) {
                floats[(offset + pixelIndex) * stride] = callback.invoke(pixelIndex)
            }
        }
    }

    override fun release() {
        gl.check { deleteTexture(texture) }
        textureUnit.release()
    }
}

interface BufferView<T> {
    operator fun set(pixelIndex: Int, t: T)
    operator fun set(pixelIndex: Int, index: Int, t: T)
    operator fun get(pixelIndex: Int): T
    operator fun get(pixelIndex: Int, index: Int): T
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
    var pixelCount: Int = 0
        private set

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
        pixelCount = bufferSize
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

    abstract fun getView(pixelOffset: Int, pixelCount: Int): ResultView

    fun release() {
        gpuBuffer.release()
    }
}

abstract class ResultView(
    val pixelOffset: Int,
    val pixelCount: Int
)