package baaahs.shows

import baaahs.*
import baaahs.gadgets.Slider
import baaahs.shaders.HeartShader
import baaahs.shaders.SolidShader
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

object HeartbleatShow : Show("Heartbleat") {
    override fun createRenderer(model: Model<*>, showApi: ShowApi): Renderer {
        model as SheepModel

        return object : Renderer {
            val hearts = showApi.allSurfaces.filter { it is IdentifiedSurface && it.number == 7 }
                .map { showApi.getShaderBuffer(it, HeartShader()) }
            val heartSizeGadget = showApi.getGadget("heartSize", Slider("Heart Size", .16f))
            val strokeSize = showApi.getGadget("strokeSize", Slider("Stroke Size", .5f))
            val xOff = showApi.getGadget("xOff", Slider("X Offset", .4f))
            val yOff = showApi.getGadget("yOff", Slider("Y Offset", .67f))
            val otherSurfaces = showApi.allUnusedSurfaces.map { showApi.getShaderBuffer(it, SolidShader()) }

            override fun nextFrame() {
                val currentBeat = showApi.currentBeat
                var phase = (currentBeat % 1.0) * 3.0f
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
                        .fade(Color(.75f, .3f, .3f), sin(currentBeat / 4.0f * PI).toFloat())
                }
            }
        }
    }

    val IdentifiedSurface.number: Int
        get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1
}
