package baaahs.shows

import baaahs.*

object PanelTweenShow : ShowMeta("PanelTweenShow") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
        val colorArray = arrayOf(
            Color.from("#FF8A47"),
            Color.from("#FC6170"),
            Color.from("#8CEEEE"),
            Color.from("#26BFBF"),
            Color.from("#FFD747")
        )

        return object : Show {
            val shaders = sheepModel.allPanels.associate { panel -> Pair(panel, showRunner.getSolidShader(panel)) }
            var frameNumber = 0
            val fadeTimeMs = 500

            override fun nextFrame() {
                sheepModel.allPanels.forEach { panel ->
                    if (panel.number > -1) {
                        val now = getTimeMillis().toInt()
                        val colorIndex = (now / fadeTimeMs + panel.number) % colorArray.size
                        val startColor = colorArray[colorIndex]
                        val endColor = colorArray[(colorIndex + 1) % colorArray.size]
                        val tweenedColor = startColor.fade(endColor, (now % fadeTimeMs) / fadeTimeMs.toFloat())

                        shaders[panel]!!.buffer.color = tweenedColor
                    }
                }

                frameNumber++
            }
        }
    }

}

val SheepModel.Panel.number : Int
    get() = Regex("\\d+").find(name)?.value?.toInt() ?: -1