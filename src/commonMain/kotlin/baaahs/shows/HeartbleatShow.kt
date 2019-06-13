package baaahs.shows

import baaahs.Color
import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.Slider
import baaahs.shaders.HeartShader
import baaahs.shaders.SolidShader
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

object HeartbleatShow : Show("Heartbleat") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner): Renderer {
        return object : Renderer {
            val beatProvider = showRunner.getBeatProvider()
            val hearts = sheepModel.allPanels.filter { it.number == 7 }
                .map { showRunner.getShaderBuffer(it, HeartShader()) }
            val heartSizeGadget = showRunner.getGadget("heartSize", Slider("Heart Size", .16f))
            val strokeSize = showRunner.getGadget("strokeSize", Slider("Stroke Size", .5f))
            val xOff = showRunner.getGadget("xOff", Slider("X Offset", .4f))
            val yOff = showRunner.getGadget("yOff", Slider("Y Offset", .67f))
            val otherSurfaces = showRunner.allUnusedSurfaces.map { showRunner.getShaderBuffer(it, SolidShader()) }

            override fun nextFrame() {
                var phase = (beatProvider.beat % 1.0) * 3.0f
                val heartSize = heartSizeGadget.value * if (phase > 1.5 && phase < 2.5f) {
                    1f + ((.5f - abs(phase - 2)) / 4).toFloat()
                } else if (phase > 2.5f || phase < 0.5f) {
                    if (phase > 2.5f) phase -= 3
                    1f + ((.5f - abs(phase)) / 2).toFloat()
                } else {
                    1f
                }

                hearts.forEach {
                    it.heartSize = heartSize
                    it.strokeSize = strokeSize.value
                    it.xOff = xOff.value
                    it.yOff = yOff.value
                }

                otherSurfaces.forEach {
                    it.color = Color(.25f, .25f, .25f)
                        .fade(Color(.75f, .3f, .3f), sin(beatProvider.beat / 4.0f * PI).toFloat())
                }
            }
        }
    }

    val SheepModel.Panel.number: Int
        get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1

}
