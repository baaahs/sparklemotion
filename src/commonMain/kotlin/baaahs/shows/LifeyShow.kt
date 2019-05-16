package baaahs.shows

import baaahs.*
import baaahs.shaders.SolidShader
import kotlin.random.Random

object LifeyShow : Show.MetaData("Lifey") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
        val shader = SolidShader()
        val shaderBuffers = sheepModel.allPanels.associateWith {
            showRunner.getShaderBuffer(it, shader).apply { color = Color.WHITE }
        }

        val selectedPanels = mutableListOf<SheepModel.Panel>()
        var lastUpdateMs : Long = 0
        var intervalMs : Long = 250

        fun SheepModel.Panel.neighbors() = sheepModel.neighborsOf(this)
        fun SheepModel.Panel.isSelected() = selectedPanels.contains(this)
        fun SheepModel.Panel.neighborsSelected() = neighbors().filter { selectedPanels.contains(it) }.count()

        return object : Show {
            override fun nextFrame() {
                val nowMs = getTimeMillis()
                if (nowMs > lastUpdateMs + intervalMs) {
                    if (selectedPanels.isEmpty()) {
                        selectedPanels.addAll(sheepModel.allPanels.filter { Random.nextFloat() < .5 })
                    } else {
                        val newSelectedPanels = mutableListOf<SheepModel.Panel>()
                        selectedPanels.forEach { panel ->
                            var living = panel.isSelected()

                            val neighborsSelected = panel.neighborsSelected()
                            if (living) {
                                if (neighborsSelected < 1 || neighborsSelected > 3) {
                                    living = false

                                    // super-lonely panels will move next door instead of dying...
                                    if (neighborsSelected == 0) {
                                        val moveToNeighbor = panel.neighbors().random()
                                        moveToNeighbor?.let { newSelectedPanels.add(it) }
                                        living = false
                                    }
                                }
                            } else {
                                if (neighborsSelected == 2 || neighborsSelected == 3) {
                                    living = true
                                }
                            }

                            // sometimes panels spontaneously become alive or die...
                            if (Random.nextFloat() < .1) {
                                living = !living
                            }

                            if (living) {
                                newSelectedPanels.add(panel)
                            }
                        }
                        selectedPanels.clear()
                        selectedPanels.addAll(newSelectedPanels)
                    }

                    lastUpdateMs = nowMs
                }

                shaderBuffers.forEach { (panel, buffer) ->
                    buffer.color = if (selectedPanels.contains(panel)) Color.WHITE else Color.BLACK
                }
            }
        }
    }
}