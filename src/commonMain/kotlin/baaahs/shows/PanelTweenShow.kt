package baaahs.shows

import baaahs.*
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SolidShader
import baaahs.shaders.SparkleShader

object PanelTweenShow : Show.MetaData("PanelTweenShow") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
        val colorArray = arrayOf(
            Color.from("#FF8A47"),
            Color.from("#FC6170"),
            Color.from("#8CEEEE"),
            Color.from("#26BFBF"),
            Color.from("#FFD747")
        )

        return object : Show {
            val slider = showRunner.getSlider()

            val shaders = sheepModel.allPanels.associateWith { panel ->
                val solidShader = showRunner.getSolidShader(panel)
                val sparkleShader = showRunner.getSparkleShader(panel)
                val compositorShader = showRunner.getCompositorShader(panel, solidShader, sparkleShader)

                Shaders(solidShader, sparkleShader, compositorShader)
            }
            val fadeTimeMs = 500

            override fun nextFrame() {
                sheepModel.allPanels.forEach { panel ->
                    if (panel.number > -1) {
                        val now = getTimeMillis().and(0xfffffff).toInt()
                        val colorIndex = (now / fadeTimeMs + panel.number) % colorArray.size
                        val startColor = colorArray[colorIndex]
                        val endColor = colorArray[(colorIndex + 1) % colorArray.size]
                        val tweenedColor = startColor.fade(endColor, (now % fadeTimeMs) / fadeTimeMs.toFloat())

                        val shaderSet = shaders[panel]!!
                        shaderSet.solidShader.buffer.color = tweenedColor

                        shaderSet.sparkleShader.buffer.color = Color.WHITE
                        shaderSet.sparkleShader.buffer.sparkliness = slider.value

                        shaderSet.compositorShader.buffer.mode = CompositingMode.ADD
                        shaderSet.compositorShader.buffer.fade = 1f
                    }
                }
            }
        }
    }

    class Shaders(
        val solidShader: SolidShader,
        val sparkleShader: SparkleShader,
        val compositorShader: CompositorShader
    )

    val SheepModel.Panel.number : Int
        get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1
}
