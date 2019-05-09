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

            val solidShader = SolidShader()
            val sparkleShader = SparkleShader()
            val compositorShader = CompositorShader(solidShader, sparkleShader)

            val shaders = sheepModel.allPanels.associateWith { panel ->
                val solidShaderBuffer = showRunner.getShaderBuffer(panel, solidShader)
                val sparkleShaderBuffer = showRunner.getShaderBuffer(panel, sparkleShader)
                val compositorShaderBuffer =
                    showRunner.getCompositorBuffer(panel, solidShaderBuffer, sparkleShaderBuffer)

                Shaders(solidShaderBuffer, sparkleShaderBuffer, compositorShaderBuffer)
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
                        shaderSet.solidShader.color = tweenedColor

                        shaderSet.sparkleShader.color = Color.WHITE
                        shaderSet.sparkleShader.sparkliness = slider.value

                        shaderSet.compositorShader.mode = CompositingMode.ADD
                        shaderSet.compositorShader.fade = 1f
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

    val SheepModel.Panel.number : Int
        get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1
}
