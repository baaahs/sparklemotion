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
    //    val colorPicker = showRunner.getColorPicker()
//    val panelShaderBuffers = sheepModel.allPanels.map { showRunner.getSolidShaderBuffer(it) }
    val pixelShaderBuffers = sheepModel.allPanels.map { showRunner.getPixelShaderBuffer(it) }

    init {
        println("Creating new SomeDumbShow, we have ${pixelShaderBuffers.size} buffers")
    }

    override fun nextFrame() {
//        panelShaderBuffers.forEach { shaderBuffer -> shaderBuffer.color = Color.random() }

        pixelShaderBuffers.forEach { shaderBuffer ->
            shaderBuffer.colors.forEachIndexed { i, pixel -> shaderBuffer.colors[i] = Color.random() }
        }
    }

    fun nextFrame(color: Color?, beat: Int, brains: MutableMap<Network.Address, RemoteBrain>, link: Network.Link) {
        brains.values.forEach { brain ->
            val brainSeed = brain.address.toString().hashCode()
            val saturation = Random(brainSeed).nextFloat() *
                    abs(sin(brainSeed + getTimeMillis() / 1000.toDouble())).toFloat()
            val desaturatedColor = color!!.withSaturation(saturation)
            link.send(
                brain.address,
                Ports.BRAIN,
                BrainShaderMessage(SolidShaderBuffer().also { it.color = desaturatedColor })
            )
        }
    }
}