package baaahs.shows

import baaahs.*
import kotlin.random.Random

object PixelTweenShow : Show.MetaData("PixelTweenShow") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
        val colorArray = arrayOf(
            Color.from("#FF8A47"),
            Color.from("#FC6170"),
            Color.from("#8CEEEE"),
            Color.from("#26BFBF"),
            Color.from("#FFD747")
        )

        return object : Show {
            val shaders = sheepModel.allPanels.associate { panel -> Pair(panel, showRunner.getPixelShader(panel)) }
            val fadeTimeMs = 1000

            override fun nextFrame() {
                sheepModel.allPanels.forEach { panel ->
                    if (panel.number > -1) {
                        val now = getTimeMillis().toInt()
                        val colorIndex = (now / fadeTimeMs + panel.number) % colorArray.size
                        val startColor = colorArray[colorIndex]
                        val endColor = colorArray[(colorIndex + 1) % colorArray.size]

                        val colors = shaders[panel]!!.buffer.colors
                        colors.forEachIndexed { index, color ->
                            if (Random.nextFloat() < .1) {
                                colors[index] = Color.WHITE
                            } else {
                                val tweenedColor = startColor.fade(endColor, ((now + index) % fadeTimeMs) / fadeTimeMs.toFloat())
                                colors[index] = tweenedColor
                            }
                        }
                    }
                }
            }
        }
    }

    val SheepModel.Panel.number : Int
        get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1

}
