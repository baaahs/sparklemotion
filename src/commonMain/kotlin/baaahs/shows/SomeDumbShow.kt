package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.shaders.PixelShader
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

object SomeDumbShow : Show.MetaData("SomeDumbShow") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) = object : Show {
        val colorPicker = showRunner.getGadget(ColorPicker("Color"))
        val pixelShader = PixelShader()

        val pixelShaderBuffers =
            showRunner.allSurfaces.map { surface -> showRunner.getShaderBuffer(surface, pixelShader) }
        val movingHeads = sheepModel.eyes.map { showRunner.getMovingHead(it) }

        override fun nextFrame() {
            val seed = Random(0)
            val now = getTimeMillis()

            fun Random.nextTimeShiftedFloat(): Float =
                sin(nextFloat() + now / 1000.0).toFloat()

            fun Color.desaturateRandomishly(baseSaturation: Float, seed: Random): Color {
                return withSaturation(baseSaturation * abs(seed.nextFloat()))
            }

            pixelShaderBuffers.forEach { shaderBuffer ->
                val baseSaturation = seed.nextFloat()
                val panelColor = if (seed.nextTimeShiftedFloat() < 0.1) Color.random() else colorPicker.color

                shaderBuffer.colors.forEachIndexed { i, pixel ->
                    shaderBuffer.colors[i] = panelColor.desaturateRandomishly(baseSaturation, seed)
                }
            }

            movingHeads.forEach { buf ->
                buf.colorWheel = buf.closestColorFor(colorPicker.color)
                buf.pan += (seed.nextTimeShiftedFloat() - .5f) / 5
                buf.tilt += (seed.nextTimeShiftedFloat() - .5f) / 5
            }
        }
    }
}
