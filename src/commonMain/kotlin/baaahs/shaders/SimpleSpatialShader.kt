package baaahs.shaders

import baaahs.*
import baaahs.glsl.PanelSpaceUvTranslator
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.sqrt

/**
 * A shader that sets all pixels to a single color within x/y constraints.
 */
class SimpleSpatialShader() : Shader<SimpleSpatialShader.Buffer>(ShaderId.SIMPLE_SPATIAL) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface): Renderer = Renderer(surface)

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

    class Renderer(surface: Surface) : Shader.Renderer<Buffer> {
        private val uvTranslator =
            if (surface is IdentifiedSurface) PanelSpaceUvTranslator.forSurface(surface) else null

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            if (uvTranslator == null) return Color.BLACK

            val (pixX, pixY) = uvTranslator.getUV(pixelIndex)

            val distX = pixX - buffer.centerX
            val distY = pixY - buffer.centerY
            val dist = sqrt(distX * distX + distY * distY)
            return when {
                dist < buffer.radius - 0.025f -> buffer.color
                dist < buffer.radius + 0.025f -> Color.BLACK
                else -> buffer.color.fade(Color.BLACK, dist * 2)
            }
        }
    }
}
