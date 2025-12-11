package baaahs.mapper

import baaahs.Color
import baaahs.MediaDevices
import baaahs.SparkleMotion
import baaahs.device.PixelFormat
import baaahs.imaging.Bitmap
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.shaders.PixelBrainShader
import baaahs.sm.brain.proto.BrainShaderMessage
import baaahs.sm.brain.proto.Message
import baaahs.sm.brain.proto.Ports

class MappableBrain(
    val address: Network.Address,
    val brainId: String,
    val sendUdp: (message: Message) -> Unit
) {
    val port get() = Ports.BRAIN

    var expectedPixelCount: Int? = null
    val expectedPixelCountOrDefault: Int
        get() = expectedPixelCount
            ?: (guessedEntity as? Model.Surface)?.expectedPixelCount
            ?: SparkleMotion.DEFAULT_PIXEL_COUNT
    var pixelFormat: PixelFormat? = null
    var brightness: Float = .1f
        set(value) {
            field = value
            pixelShaderBuffer.palette[1] = Color.WHITE.withBrightness(value)
        }

    var changeRegion: MediaDevices.Region? = null
    var guessedEntity: Model.Entity? = null
    var guessedVisibleSurface: Mapper.VisibleSurface? = null
    var panelDeltaBitmap: Bitmap? = null
    var deltaImageName: String? = null
//    var entityMetadata: MappingStrategy.EntityMetadata? = null
    val pixelMapData: MutableMap<Int, Mapper.PixelMapData> = mutableMapOf()

    private val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.INDEXED_2)
    val pixelShaderBuffer = pixelShader.createBuffer(Mapper.maxPixelsPerBrain).apply {
        palette[0] = Color.BLACK
        palette[1] = Color.WHITE.withBrightness(brightness)
        setAll(0)
    }

    fun shade(shaderMessage: () -> BrainShaderMessage) {
        sendUdp(shaderMessage())
    }

    fun send(message: Message) {
        sendUdp(message)
    }

    fun shadeSolidColor() {
        // Less voltage causes less LED glitches.
        var color = Color.GREEN.withBrightness(.4f)

        // Apply pixel format.
        val pixelFormat = pixelFormat
        if (pixelFormat != null) {
            val out = ByteArrayWriter()
            PixelFormat.RGB8.writeColor(color, out)
            color = pixelFormat.readColor(ByteArrayReader(out.copyBytes()))
        }

        shade {
            MapperUtil.solidColor(color)
        }
    }
}