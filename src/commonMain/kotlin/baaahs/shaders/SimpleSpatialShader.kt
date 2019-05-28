package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A shader that sets all pixels to a single color within x/y constraints.
 */
class SimpleSpatialShader() : Shader<SimpleSpatialShader.Buffer>(ShaderId.SIMPLE_SPATIAL) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface, pixels: Pixels): Renderer = Renderer(surface, pixels)

    companion object : ShaderReader<SimpleSpatialShader> {
        override fun parse(reader: ByteArrayReader) = SimpleSpatialShader()
    }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@SimpleSpatialShader

        var color: Color = Color.WHITE
        var centerX: Float = 0.5f
        var centerY: Float = 0.5f
        var radius: Float = 0.75f

        override fun serialize(writer: ByteArrayWriter) {
            color.serialize(writer)
            writer.writeFloat(centerX)
            writer.writeFloat(centerY)
            writer.writeFloat(radius)
        }

        override fun read(reader: ByteArrayReader) {
            color = Color.parse(reader)
            centerX = reader.readFloat()
            centerY = reader.readFloat()
            radius = reader.readFloat()
        }
    }

    class Renderer(private val surface: Surface, private val pixels: Pixels) : Shader.Renderer<Buffer> {
        private val colors = Array(pixels.count) { Color.WHITE }
        private val pixelVertices = (surface as? Brain.MappedSurface)?.pixelVertices

        override fun draw(buffer: Buffer) {
            if (pixelVertices == null) return

            for (i in 0 until min(colors.size, pixelVertices.size)) {
                val (pixX, pixY) = pixelVertices[i]

                val distX = pixX - buffer.centerX
                val distY = pixY - buffer.centerY
                val dist = sqrt(distX * distX + distY * distY)
                colors[i] = if (dist < buffer.radius - 0.025f) {
                    buffer.color
                } else if (dist < buffer.radius + 0.025f) {
                    Color.BLACK
                } else {
                    buffer.color.fade(Color.BLACK, dist * 2)
                }
            }
            pixels.set(colors)
        }
    }
}
