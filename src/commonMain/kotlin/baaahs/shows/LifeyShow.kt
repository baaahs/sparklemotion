package baaahs.shows

import baaahs.*
import baaahs.gadgets.Slider
import baaahs.shaders.SolidShader
import kotlin.random.Random

object LifeyShow : Show("Lifey") {
    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        model as SheepModel

        val speedSlider = showRunner.getGadget("speed", Slider("Speed", .25f))

        val shader = SolidShader()
        val shaderBuffers = showRunner.allSurfaces.associateWith {
            showRunner.getShaderBuffer(it, shader).apply { color = Color.WHITE }
        }

        val selectedPanels = mutableListOf<SheepModel.Panel>()
        var lastUpdateMs: Long = 0

        fun SheepModel.Panel.neighbors() = model.neighborsOf(this)
        fun SheepModel.Panel.isSelected() = selectedPanels.contains(this)
        fun SheepModel.Panel.neighborsSelected() = neighbors().filter { selectedPanels.contains(it) }.count()

        return object : Renderer {
            override fun nextFrame() {
                val nowMs = getTimeMillis()
                val intervalMs = ((1f - speedSlider.value) * 1000).toLong()
                if (nowMs > lastUpdateMs + intervalMs) {
                    if (selectedPanels.isEmpty()) {
                        selectedPanels.addAll(model.allPanels.filter { Random.nextFloat() < .5 })
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

                shaderBuffers.forEach { (surface, buffer) ->
                    buffer.color =
                        if (surface is MappedSurface
                            && selectedPanels.contains(surface.modelSurface)) Color.WHITE else Color.BLACK
                }
            }
        }
    }
}