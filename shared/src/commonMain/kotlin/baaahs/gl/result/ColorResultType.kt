package baaahs.gl.result

import baaahs.Color
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.io.ByteArrayWriter
import baaahs.sm.brain.proto.Pixels
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.ByteBuffer
import com.danielgergely.kgl.GL_RGBA
import com.danielgergely.kgl.GL_UNSIGNED_BYTE
import kotlin.math.pow
import kotlin.random.Random

object ColorResultType : ResultType<ColorResultType.Buffer> {
    private val renderPixelFormat: Int = GlContext.GL_RGBA8
    override val readFormat: Int
        get() = GL_RGBA
    override val readType: Int
        get() = GL_UNSIGNED_BYTE
    override val stride: Int
        get() = 4

    override fun createResultBuffer(gl: GlContext, index: Int): Buffer {
        return Buffer(gl, index)
    }

    class Buffer(gl: GlContext, resultIndex: Int) : ResultBuffer(gl, resultIndex, ColorResultType, renderPixelFormat) {
        private lateinit var byteBuffer: ByteBuffer

        override val cpuBuffer: com.danielgergely.kgl.Buffer
            get() = byteBuffer

        override fun resizeBuffer(size: Int): Int {
            byteBuffer = ByteBuffer(size * stride)
            return size * stride
        }

        operator fun get(componentIndex: Int): Color {
            val offset = componentIndex * stride

            // Using Color's int constructor fixes a bug in Safari causing
            // color values above 127 to be treated as 0. Untested. :-(
            return Color.from(
                r = byteBuffer[offset].toInt() and 0xff,
                g = byteBuffer[offset + 1].toInt() and 0xff,
                b = byteBuffer[offset + 2].toInt() and 0xff,
                a = byteBuffer[offset + 3].toInt() and 0xff
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): ColorFixtureResults {
            return ColorFixtureResults(this, bufferOffset, fixture)
        }
    }

    class ColorFixtureResults(
        private val buffer: Buffer,
        componentOffset: Int,
        private val fixture: Fixture,
    ) : FixtureResults(componentOffset, fixture.componentCount), Pixels {
        private val transport = fixture.transport

        override val size: Int
            get() = componentCount

        private val fixtureConfig = fixture.fixtureConfig as PixelArrayDevice.Config
        private val pixelFormat = fixtureConfig.pixelFormat
        private val gammaCorrector = GammaCorrector.create(fixtureConfig.gammaCorrection.toDouble())
        private val bytesPerPixel = pixelFormat.channelsPerPixel

        override operator fun get(i: Int): Color = buffer[componentOffset + i]

        override fun set(i: Int, color: Color) = TODO("not implemented")

        override fun set(colors: Array<Color>) = TODO("not implemented")

        override fun iterator(): Iterator<Color> {
            return iterator {
                for (i in 0 until componentCount) yield(get(i))
            }
        }

        override fun send(remoteVisualizers: RemoteVisualizers) {
            transport.deliverComponents(componentCount, bytesPerPixel) { i, buf ->
                val color = gammaCorrector.correct(this[i])
                pixelFormat.writeColor(color, buf)
            }

            val remoteVisualizersBytes by lazy {
                val buf = ByteArrayWriter()
                for (i in 0 until componentCount) {
                    val color = gammaCorrector.correct(this[i])
                    pixelFormat.writeColor(color, buf)
                }
                buf.toBytes()
            }
            remoteVisualizers.sendFrameData(fixture.modelEntity) { out ->
                out.writeInt(fixture.componentCount)
                out.writeBytes(remoteVisualizersBytes)
            }
        }
    }

    interface GammaCorrector {
        fun correct(color: Color): Color

        companion object {
            fun create(gamma: Double): GammaCorrector =
                when (gamma) {
                    1.0 -> LinearGammaCorrector()
                    else -> RealGammaCorrector(gamma)
                }
        }
    }

    class RealGammaCorrector(val gamma: Double = 2.2) : GammaCorrector {
        private val random = Random(0)
        private val fractRes = 1024
        private val lookupInt = (0..255)
            .map { (it / 255.0).pow(gamma).times(255).toInt() }.toIntArray()
        private val lookupFraction = (0..255)
            .map { ((it / 255.0).pow(gamma).times(255).mod(1.0) * fractRes).toInt() }.toIntArray()

        override fun correct(color: Color): Color {
            val random = random.nextInt(fractRes)
            val redI = color.redI
            val greenI = color.greenI
            val blueI = color.blueI

            return Color(
                lookupInt[redI] + if (random < lookupFraction[redI]) 1 else 0,
                lookupInt[greenI] + if (random < lookupFraction[greenI]) 1 else 0,
                lookupInt[blueI] + if (random < lookupFraction[blueI]) 1 else 0,
            )
        }
    }

    class LinearGammaCorrector : GammaCorrector {
        override fun correct(color: Color) = color
    }
}