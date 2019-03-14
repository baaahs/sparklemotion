package baaahs

import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

enum class ShaderType {
    SOLID,
    PIXEL
}

abstract class ShaderBuffer(val type: ShaderType) {
    abstract fun serialize(writer: ByteArrayWriter)
}

class SolidShaderBuffer : ShaderBuffer(ShaderType.SOLID) {
    var color: Color = Color.WHITE

    override fun serialize(writer: ByteArrayWriter) {
        color.serialize(writer)
    }

    companion object {
        fun parse(reader: ByteArrayReader): SolidShaderBuffer {
            val buf = SolidShaderBuffer()
            buf.color = Color.parse(reader)
            return buf
        }
    }
}

class PixelShaderBuffer : ShaderBuffer(ShaderType.PIXEL) {
    var fakeyTerribleHardCodedNumberOfPixels: Int = 1337
    var colors: MutableList<Color> = ((0..fakeyTerribleHardCodedNumberOfPixels).map { Color.WHITE }).toMutableList()

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeInt(colors.size)
        colors.forEach { color -> color.serialize(writer) }
    }

    companion object {
        fun parse(reader: ByteArrayReader): PixelShaderBuffer {
            val buf = PixelShaderBuffer()
            (0 until reader.readInt()).forEach { index -> buf.colors[index] = Color.parse(reader) }
            return buf
        }
    }

}

interface Show {

    /*{
        63: { shader: solid, data: { color: blue } }
    }*/

    fun nextFrame()
}

class SomeDumbShow(sheepModel: SheepModel, showRunner: ShowRunner) : Show {
    val colorPicker = showRunner.getColorPicker()
//    val panelShaderBuffers = sheepModel.allPanels.map { showRunner.getSolidShaderBuffer(it) }
    val pixelShaderBuffers = sheepModel.allPanels.map { showRunner.getPixelShaderBuffer(it) }

    init {
        println("Creating new SomeDumbShow, we have ${pixelShaderBuffers.size} buffers")
    }

    override fun nextFrame() {
//        panelShaderBuffers.forEach { shaderBuffer -> shaderBuffer.color = Color.random() }
        val seed = Random(0)

        pixelShaderBuffers.forEach { shaderBuffer ->
            shaderBuffer.colors.forEachIndexed { i, pixel ->
                val saturation = seed.nextFloat() *
                        abs(sin(seed.nextDouble() + getTimeMillis() / 1000.toDouble())).toFloat()
                val desaturatedColor = colorPicker.color.withSaturation(saturation)
                shaderBuffer.colors[i] = desaturatedColor
            }
        }
    }
}