package baaahs.shows

import baaahs.*
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class SomeDumbShow(sheepModel: SheepModel, showRunner: ShowRunner) : Show {
    val colorPicker = showRunner.getColorPicker()
    //    val panelShaderBuffers = sheepModel.allPanels.map { showRunner.getSolidShader(it) }
    val pixelShaderBuffers = sheepModel.allPanels.map { showRunner.getPixelShader(it).buffer }
    val movingHeads = sheepModel.eyes.map { showRunner.getMovingHead(it) }

    init {
        println("Creating new SomeDumbShow, we have ${pixelShaderBuffers.size} buffers")
    }

    override fun nextFrame() {
//        panelShaderBuffers.forEach { shader -> shader.color = Color.random() }
        val seed = Random(0)

        pixelShaderBuffers.forEach { shaderBuffer ->
            val baseSaturation = seed.nextFloat()
            val panelColor = if (seed.nextFloat() < 0.1) Color.random() else colorPicker.color

            shaderBuffer.colors.forEachIndexed { i, pixel ->
                shaderBuffer.colors[i] = desaturateRandomishly(baseSaturation, seed, panelColor)
            }
        }

        movingHeads.forEach { buf ->
            buf.colorWheel = buf.closestColorFor(colorPicker.color)
            buf.pan += (nextRandomFloat(seed) - .5).toFloat() / 5
            buf.tilt += (nextRandomFloat(seed) - .5).toFloat() / 5
        }
    }

    private fun desaturateRandomishly(
        baseSaturation: Float,
        seed: Random,
        panelColor: Color
    ): Color {
        val saturation = baseSaturation * abs(nextRandomFloat(seed)).toFloat()
        val desaturatedColor = panelColor.withSaturation(saturation)
        return desaturatedColor
    }

    private fun nextRandomFloat(seed: Random) =
        sin(seed.nextDouble() + getTimeMillis() / 1000.toDouble())

    class Meta: ShowMeta("SomeDumbShow") {
        override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) =
            SomeDumbShow(sheepModel, showRunner)
    }
}