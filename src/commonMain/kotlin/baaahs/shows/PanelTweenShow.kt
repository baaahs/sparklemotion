package baaahs.shows

import baaahs.*
import baaahs.gadgets.PalettePicker
import baaahs.gadgets.Slider
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SolidShader
import baaahs.shaders.SparkleShader

object PanelTweenShow : Show("PanelTweenShow") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner): Renderer {
        val initialColors = listOf(
            Color.from("#FF8A47"),
            Color.from("#FC6170"),
            Color.from("#8CEEEE"),
            Color.from("#26BFBF"),
            Color.from("#FFD747")
        )

        return object : Renderer {
            val palettePicker = showRunner.getGadget("palette", PalettePicker("Palette", initialColors))
            val slider = showRunner.getGadget("sparkliness", Slider("Sparkliness", 0f))

            val solidShader = SolidShader()
            val sparkleShader = SparkleShader()

            val shaderBuffers = showRunner.allSurfaces.map { surface ->
                val solidShaderBuffer = showRunner.getShaderBuffer(surface, solidShader)
                val sparkleShaderBuffer = showRunner.getShaderBuffer(surface, sparkleShader)
                val compositorShaderBuffer = showRunner.getCompositorBuffer(
                    surface, solidShaderBuffer, sparkleShaderBuffer, CompositingMode.ADD, 1f
                )

                Shaders(solidShaderBuffer, sparkleShaderBuffer, compositorShaderBuffer)
            }
            val fadeTimeMs = 500

            override fun nextFrame() {
                val now = getTimeMillis().and(0xfffffff).toInt()
                val colors = palettePicker.colors
                shaderBuffers.forEachIndexed() { number, bufs ->
                    val colorIndex = (now / fadeTimeMs + number) % colors.size
                    val startColor = colors[colorIndex]
                    val endColor = colors[(colorIndex + 1) % colors.size]
                    val tweenedColor = startColor.fade(endColor, (now % fadeTimeMs) / fadeTimeMs.toFloat())

                    bufs.apply {
                        solidShader.color = tweenedColor

                        sparkleShader.color = Color.WHITE
                        sparkleShader.sparkliness = slider.value
                    }
                }
            }
        }
    }

    class Shaders(
        val solidShader: SolidShader.Buffer,
        val sparkleShader: SparkleShader.Buffer,
        val compositorShader: CompositorShader.Buffer
    )

    val SheepModel.Panel.number: Int
        get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1
}
