package baaahs

import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

enum class ShaderType {
    SOLID
}

open class ShaderBuffer {
}

class SolidShaderBuffer : ShaderBuffer() {
    var color: Color = Color.WHITE
}

interface Show {

    /*{
        63: { shader: solid, data: { color: blue } }
    }*/

    fun nextFrame()
}

class SomeDumbShow(sheepModel: SheepModel, showContext: ShowContext) : Show {
//    val colorPicker = showContext.getColorPicker()
    val panelShaderBuffers = showContext.getShaderBuffersFor(ShaderType.SOLID, sheepModel.allPanels)

    init {
        println("Creating new SomeDumbShow, we have ${panelShaderBuffers.size} buffers")
    }

    override fun nextFrame() {
        panelShaderBuffers.forEach { shaderBuffer -> shaderBuffer.color = Color.random() }
    }

    fun nextFrame(color: Color?, beat: Int, brains: MutableMap<Network.Address, RemoteBrain>, link: Network.Link) {
        brains.values.forEach { brain ->
            val brainSeed = brain.address.toString().hashCode()
            val saturation = Random(brainSeed).nextFloat() *
                    abs(sin(brainSeed + getTimeMillis() / 1000.toDouble())).toFloat()
            val desaturatedColor = color!!.withSaturation(saturation)
            link.send(brain.address, Ports.BRAIN, BrainShaderMessage(desaturatedColor))
        }
    }
}