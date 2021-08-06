package baaahs.fixtures

import baaahs.geom.Vector3F
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderResults
import baaahs.gl.render.ResultStorage
import baaahs.glsl.Uniform
import baaahs.model.Model
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.math.min

interface DeviceType {
    val id: String
    val title: String
    val dataSourceBuilders: List<DataSourceBuilder<*>>
    val resultContentType: ContentType
    val likelyPipelines: List<Pair<ContentType, ContentType>>
    val errorIndicatorShader: Shader
    val defaultConfig: FixtureConfig
    val serialModule: SerializersModule get() = SerializersModule {}

    fun createResultStorage(renderResults: RenderResults): ResultStorage

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

interface FixtureConfig {
    val deviceType: DeviceType

    fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? = null
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
        renderTarget: FixtureRenderTarget,
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

abstract class ResultBuffer(
    gl: GlContext,
    private val resultIndex: Int,
    val type: ResultType<*>
) {
    var pixelCount: Int = 0
        private set

    private var curWidth = 0
    private var curHeight = 0
    private var cpuBufferSize = 0

    val gpuBuffer = gl.createRenderBuffer()
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
        fb.attach(gpuBuffer, GL_COLOR_ATTACHMENT0 + resultIndex)
    }

    abstract fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults

    fun release() {
        gpuBuffer.release()
    }
}

abstract class FixtureResults(
    val pixelOffset: Int,
    val pixelCount: Int
) {
    // TODO: This is pretty janky, having send() call RemoteVisualizers. Find a better way.
    abstract fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers)
}