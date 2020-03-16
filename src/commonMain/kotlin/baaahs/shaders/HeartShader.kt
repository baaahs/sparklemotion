package baaahs.shaders

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.Surface
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.PanelSpaceUvTranslator
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.pow

class HeartShader : Shader<HeartShader.Buffer>(ShaderId.HEART) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface): Renderer = Renderer(surface)

    companion object : ShaderReader<HeartShader> {
        override fun parse(reader: ByteArrayReader) = HeartShader()
    }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> get() = this@HeartShader

        var edgeColor = Color.RED
        var centerColor = Color.RED.fade(Color.WHITE, .2f)
        var heartSize = 1f
        var strokeSize = 1f
        var xOff = 0f
        var yOff = 0f

        override fun serialize(writer: ByteArrayWriter) {
            writer.writeFloat(heartSize)
            writer.writeFloat(strokeSize)
            writer.writeFloat(xOff)
            writer.writeFloat(yOff)
        }

        override fun read(reader: ByteArrayReader) {
            heartSize = reader.readFloat()
            strokeSize = reader.readFloat()
            xOff = reader.readFloat()
            yOff = reader.readFloat()
        }

    }

    class Renderer(surface: Surface) : Shader.Renderer<Buffer> {
        private val uvTranslator =
            if (surface is IdentifiedSurface) {
                PanelSpaceUvTranslator.forPixels(LinearSurfacePixelStrategy.forSurface(surface))
            } else null

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            if (uvTranslator == null) return Color.BLACK

            var (x, y) = uvTranslator.getUV(pixelIndex)
            x -= .5f + buffer.xOff - .5f
            x *= 1.1f
            y -= .5f + buffer.yOff - .5f

            x /= buffer.heartSize
            y /= buffer.heartSize

            val upperCurveDist = y - (1 - (abs(x) - 1).pow(2))
            val lowerCurveDist = y - (acos(1 - abs(x)) - PI).toFloat()
            if (y >= 0) { // upper section
                if (upperCurveDist < 0) {
                    val fadeAmount = if (abs(upperCurveDist) < buffer.strokeSize) {
                        0f
                    } else {
                        abs(upperCurveDist / buffer.heartSize)
                    }
                    return buffer.edgeColor.fade(buffer.centerColor, fadeAmount)
                } else {
                    return Color.TRANSPARENT
                }
            } else if (lowerCurveDist > 0) {
                val fadeAmount = if (lowerCurveDist < buffer.strokeSize) {
                    1f
                } else {
                    lowerCurveDist / buffer.heartSize
                }
                return buffer.edgeColor.fade(buffer.centerColor, fadeAmount)
            } else {
                return Color.TRANSPARENT
            }
        }
    }
}